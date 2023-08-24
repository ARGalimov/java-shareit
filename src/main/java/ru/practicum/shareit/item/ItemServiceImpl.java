package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.exception.UserCommentException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentStorage;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;

    @Override
    public ItemDto findItem(Integer itemId, Integer userId) {
        Item item = itemStorage.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id {} не найдена", itemId);
            throw new ObjectNotFoundException("Вещь не найдена");
        });
        log.info("Вещь с id {} найдена", itemId);
        ItemDto itemDto = ItemMapper.toDto(item);

        List<Comment> comments = commentStorage.findAllByItemId(itemId, Sort.by("id"));
        itemDto.setComments(CommentMapper.mapToCommentDto(comments));

        Integer ownerId = item.getOwner().getId();
        if (ownerId.equals(userId)) {
            log.info("Пользователь - владелец");
            loadBookingDates(itemDto);
        }
        return itemDto;
    }

    private void loadBookingDates(ItemDto itemDto) {
        Optional<Booking> lastBooking = bookingStorage.findFirstByItem_IdAndStartBeforeOrderByStartDesc(itemDto.getId(),
                LocalDateTime.now());
        itemDto.setLastBooking(mapToItemBookingDto(lastBooking));

        Optional<Booking> nextBooking = bookingStorage.findFirstByItem_IdAndStatusAndStartAfterOrderByStart(itemDto.getId(),
                BookingStatus.APPROVED, LocalDateTime.now());
        itemDto.setNextBooking(mapToItemBookingDto(nextBooking));
    }

    private ItemBookingDto mapToItemBookingDto(Optional<Booking> booking) {
        if (booking.isPresent()) {
            Integer id = booking.get().getId();
            Integer bookerId = booking.get().getBooker().getId();
            LocalDateTime start = booking.get().getStart();
            LocalDateTime end = booking.get().getEnd();
            return new ItemBookingDto(id, bookerId, start, end);
        }
        return null;
    }

    @Override
    public List<ItemDto> findItemsByOwner(Integer userId) {
//        List<Item> items = new ArrayList<>(itemStorage.findAllByOwnerId(userId));
//        items.sort(Comparator.comparing(Item::getId));
        List<Item> items = itemStorage.findAllByOwnerId(userId, Sort.by("id"));
        List<ItemDto> itemsDto = ItemMapper.toDto(items);
        log.info("Найдено {} вещей", itemsDto.size());
        itemsDto.forEach(this::loadBookingDates);
        return itemsDto;
    }

    @Override
    public List<ItemDto> findItemsByText(String text) {
        List<ItemDto> itemsDto = new ArrayList<>();
        List<Item> items = new ArrayList<>(itemStorage.findAll());
        for (Item item : items) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                    item.isAvailable() &&
                    !text.equals("")) {
                itemsDto.add(ItemMapper.toDto(item));
            }
        }
        return itemsDto;
    }

    @Transactional
    @Override
    public ItemDto createItem(Integer userId, ItemDto itemDto) {
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            log.warn("Description is Empty");
            throw new NullObjectException("Description is Empty");
        }
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            log.warn("Description is Empty");
            throw new NullObjectException("Description is Empty");
        }
        if (itemDto.getAvailable() == null) {
            log.warn("Available is Empty");
            throw new NullObjectException("Available is Empty");
        }
        User user = userStorage.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь не найден");
            throw new ObjectNotFoundException("Пользователь не найден");
        });
        log.info("Вещь создана");
        return ItemMapper.toDto(itemStorage.save(ItemMapper.createNewEntity(user, itemDto)));
    }

    @Transactional
    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        Item item = itemStorage.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id {} не найдена", itemId);
            throw new ObjectNotFoundException("Вещь не найдена");
        });
        log.info("Вещь с id {} найдена", itemId);
        checkPermissions(userId, item);

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
            log.info("Вещь с id {} обновляем Name", itemId);
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
            log.info("Вещь с id {} обновляем Description", itemId);
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
            log.info("Вещь с id {} обновляем Available", itemId);
        }

        return ItemMapper.toDto(itemStorage.save(item));
    }

    @Override
    public void deleteItem(Integer userId, Integer id) {
        Item item = itemStorage.findById(id).orElseThrow(() -> {
            log.warn("Вещь с id {} не найдена", id);
            throw new ObjectNotFoundException("Вещь не найдена");
        });
        itemStorage.delete(item);
    }

    @Transactional
    @Override
    public CommentDto createComment(Integer userId, Integer itemId, CommentDto commentDto) {
        User author = userStorage.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new ObjectNotFoundException("Пользователь не найден");
        });

        bookingStorage.findFirstByBookerAndItemIdAndEndBefore(author, itemId, LocalDateTime.now()).orElseThrow(
                () -> throwUserCommentException(userId));
        Item item = itemStorage.findById(itemId).orElseThrow(() -> throwObjectNotFoundException(itemId));

        Comment comment = CommentMapper.mapToComment(author, item, commentDto, LocalDateTime.now());
        return CommentMapper.mapToCommentDto(commentStorage.save(comment));
    }

    @Override
    public void checkPermissions(Integer userId, Item item) {
        if (!Objects.equals(userId, item.getOwner().getId())) {
            String message = "Пользователь с id " + userId + " не владелец предмета с id " + item.getId();
            log.warn(message);
            throw new PermissionException(message);
        }
    }

    private ObjectNotFoundException throwObjectNotFoundException(Integer id) {
        String message = "Предмет с id " + id + " не найден!";
        log.warn(message);
        throw new ObjectNotFoundException(message);
    }

    private ObjectNotFoundException throwUserCommentException(Integer id) {
        String message = "Пользователь с id " + id + "не имеет прав комментировать";
        log.warn(message);
        throw new UserCommentException(message);
    }
}

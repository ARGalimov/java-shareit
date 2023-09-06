package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @GetMapping("/{itemId}")
    public ItemDto find(@RequestHeader(xSharerUserId) Integer userId,
                        @PathVariable Integer itemId) {
        return itemService.findItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> find(@RequestHeader(xSharerUserId) Integer userId,
                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        return itemService.findItemsByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestParam String text,
                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        return itemService.findItemsByText(text, from, size);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(xSharerUserId) Integer userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto amend(@RequestHeader(xSharerUserId) Integer userId,
                         @PathVariable Integer itemId,
                         @Valid @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@RequestHeader(xSharerUserId) Integer userId,
                       @PathVariable Integer id) {
        itemService.deleteItem(userId, id);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return itemService.createComment(userId, itemId, commentDto);
    }
}

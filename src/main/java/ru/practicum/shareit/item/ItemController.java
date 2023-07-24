package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @GetMapping("/{itemId}")
    public ItemDto find(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                        @PathVariable Integer itemId) {
        return itemService.findItem(itemId);
    }

    @GetMapping
    public List<ItemDto> find(@RequestHeader(X_SHARER_USER_ID) Integer userId) {
        return itemService.findItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                              @RequestParam String text) {
        return itemService.findItemsByText(text);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto amend(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                         @PathVariable Integer itemId,
                         @Valid @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                       @PathVariable Integer id) {
        itemService.deleteItem(userId, id);
    }
}

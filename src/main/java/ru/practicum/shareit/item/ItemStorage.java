package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Integer> {
    List<Item> findAllByOwnerId(Integer ownerId, Sort sort);

    List<Item> findAllByOwnerId(Integer ownerId, Pageable pageable);

    @Query("select i from Item i where " +
            "(lower(i.name) like lower(concat('%', :text,'%')) or " +
            "lower(i.description) like lower(concat('%', :text,'%'))) " +
            "and i.available = true")
    List<Item> search(String text);

    @Query("select i from Item i where " +
            "(lower(i.name) like lower(concat('%', :text,'%')) or " +
            "lower(i.description) like lower(concat('%', :text,'%'))) " +
            "and i.available = true")
    List<Item> search(String text, Pageable pageable);

    List<Item> findAllByItemRequestId(Integer requestId);
}

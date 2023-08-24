package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Integer> {
    List<Item> findAllByOwnerId(Integer ownerId, Sort sort);

    @Query("select i from Item i where " +
            "(lower(i.name) like lower(concat('%', :text,'%')) or " +
            "lower(i.description) like lower(concat('%', :text,'%'))) " +
            "and i.available = true")
    List<Item> search(String text);
}

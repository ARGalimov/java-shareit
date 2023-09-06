package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findAllByRequesterId(Integer requesterId, Sort sort);

    List<ItemRequest> findAllByRequesterIdNot(Integer requesterId, Pageable pageable);
}

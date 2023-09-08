package ru.practicum.shareit;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetPage implements Pageable {
    private final int offset;
    private final int limit;
    private Sort sort = Sort.unsorted();

    public OffsetPage(int offset, int limit, Sort sort) {
        if (offset < 0)
            throw new IllegalArgumentException("Offset > 0!");
        if (limit <= 0)
            throw new IllegalArgumentException("Limit >= 0!");

        this.offset = offset;
        this.limit = limit;
        if (sort != null) {
            this.sort = sort;
        }
    }

    public OffsetPage(int offset, int limit) {
        this(offset, limit, null);
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    //Остальные методы заглушки не нужны для нашей задачи
    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public Pageable next() {
        return this;
    }

    @Override
    public Pageable previousOrFirst() {
        return this;
    }

    @Override
    public Pageable first() {
        return this;
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return this;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}

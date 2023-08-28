package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

class OffsetPageTest {
    Pageable pageable = new OffsetPage(10, 2);

    @Test
    void  shouldExceptionWhenSizeLessThanZero() {
        assertThrows(IllegalArgumentException.class, () -> new OffsetPage(-10, 2));
    }

    @Test
    void  shouldExceptionWhenLimitLessThanZero() {
        assertThrows(IllegalArgumentException.class, () -> new OffsetPage(10, -2));
    }


    @Test
    void getPageNumber() {
        assertEquals(0, pageable.getPageNumber());
    }

    @Test
    void next() {
        assertEquals(pageable, pageable.next());
    }

    @Test
    void previousOrFirst() {
        assertEquals(pageable, pageable.previousOrFirst());
    }

    @Test
    void first() {
        assertEquals(pageable, pageable.first());
    }

    @Test
    void withPage() {
        pageable.withPage(1);
    }

    @Test
    void hasPrevious() {
        assertFalse(pageable.hasPrevious());
    }
}
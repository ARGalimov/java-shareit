package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Integer> {
    @Query("select b from Booking b where ( " +
            ":start <= b.start and b.start <= :end or " +
            ":start <= b.end and b.end <= :end) and " +
            "b.item.id = :itemId and " +
            "b.status = 'APPROVED'")
    List<Booking> findAllByDateAndId(Integer itemId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerId(Integer bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(Integer bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndIsBefore(Integer bookerId, LocalDateTime date, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsAfter(Integer bookerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where  " +
            "b.start < :date and :date < b.end and " +
            "b.booker.id = :bookerId")
    List<Booking> findByBookerIdCurrDate(Integer bookerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where  " +
            "b.item.owner.id = :ownerId")
    List<Booking> findAllItemBooking(Integer ownerId, Pageable pageable);

    @Query("select b from Booking b where  " +
            "b.item.owner.id = :ownerId and " +
            "b.end < :date")
    List<Booking> findAllItemBookingEndIsBefore(Integer ownerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where  " +
            "b.item.owner.id = :ownerId and " +
            "b.start > :date")
    List<Booking> findAllItemBookingAndStartIsAfter(Integer ownerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where  " +
            "b.start < :date and :date < b.end and " +
            "b.item.owner.id = :ownerId")
    List<Booking> findAllItemBookingCurrDate(Integer ownerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where  " +
            "b.item.owner.id = :ownerId and b.status = :status")
    List<Booking> findAllItemBookingStatus(Integer ownerId, BookingStatus status, Pageable pageable);

    Optional<Booking> findFirstByItem_IdAndStartBeforeOrderByStartDesc(Integer itemId, LocalDateTime date);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartAfterOrderByStart(Integer itemId, BookingStatus status, LocalDateTime date);

    Optional<Booking> findFirstByBookerAndItemIdAndEndBefore(User booker, Integer itemId, LocalDateTime date);
}

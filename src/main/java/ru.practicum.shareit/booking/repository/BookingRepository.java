package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdAndItem_IdAndStatusAndEndDateBefore(Long bookerId, Long itemId,
                                                                     BookingStatus status, LocalDateTime endDate);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND " +
            "(:state = 'ALL' OR " +
            "(:state = 'CURRENT' AND b.status = 'APPROVED' AND b.endDate > :now AND b.startDate < :now) OR " +
            "(:state = 'PAST' AND b.status = 'APPROVED' AND b.endDate < :now) OR " +
            "(:state = 'FUTURE' AND b.status = 'APPROVED' AND b.startDate > :now) OR " +
            "(:state = 'WAITING' AND b.status = 'WAITING') OR " +
            "(:state = 'REJECTED' AND b.status = 'REJECTED')) " +
            "ORDER BY b.startDate DESC")
    List<Booking> findBookingsByUserWithState(@Param("userId") Long userId,
                                              @Param("state") String state,
                                              @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND " +
            "(:state = 'ALL' OR " +
            "(:state = 'CURRENT' AND b.status = 'APPROVED' AND b.endDate > :now AND b.startDate < :now) OR " +
            "(:state = 'PAST' AND b.status = 'APPROVED' AND b.endDate < :now) OR " +
            "(:state = 'FUTURE' AND b.status = 'APPROVED' AND b.startDate > :now) OR " +
            "(:state = 'WAITING' AND b.status = 'WAITING') OR " +
            "(:state = 'REJECTED' AND b.status = 'REJECTED')) " +
            "ORDER BY b.startDate DESC")
    List<Booking> findBookingsByOwnerWithState(@Param("ownerId") Long ownerId,
                                               @Param("state") String state,
                                               @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.status = :status AND b.endDate < :now ORDER BY b.endDate DESC")
    List<Booking> findLastBookings(@Param("itemId") Long itemId,
                                   @Param("status") BookingStatus status,
                                   @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.status = :status AND b.startDate > :now ORDER BY b.startDate ASC")
    List<Booking> findNextBookings(@Param("itemId") Long itemId,
                                   @Param("status") BookingStatus status,
                                   @Param("now") LocalDateTime now);

    List<Booking> findByItem_Id(Long itemId);
}

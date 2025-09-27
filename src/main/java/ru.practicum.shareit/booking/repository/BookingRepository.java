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
    List<Booking> findByItem_IdAndStatus(Long itemId, BookingStatus status);

    List<Booking> findByBooker_Id(Long bookerId);

    List<Booking> findByItem_Owner_Id(Long ownerId);

    List<Booking> findByItem_Id(Long itemId);

    List<Booking> findByBooker_IdAndItem_IdAndStatusAndEndDateBefore(Long bookerId, Long itemId,
                                                                     BookingStatus status, LocalDateTime endDate);

    List<Booking> findByBooker_IdAndStatusAndEndDateAfter(Long userId, BookingStatus bookingStatus, LocalDateTime now);

    List<Booking> findByBooker_IdAndStatusAndEndDateBefore(Long userId, BookingStatus bookingStatus, LocalDateTime now);

    List<Booking> findByBooker_IdAndStatusAndStartDateAfter(Long userId, BookingStatus bookingStatus, LocalDateTime now);

    List<Booking> findByBooker_IdAndStatus(Long userId, BookingStatus bookingStatus);

    List<Booking> findByItem_Owner_IdAndStatusAndEndDateAfter(Long ownerId, BookingStatus bookingStatus, LocalDateTime now);

    List<Booking> findByItem_Owner_IdAndStatusAndEndDateBefore(Long ownerId, BookingStatus bookingStatus, LocalDateTime now);

    List<Booking> findByItem_Owner_IdAndStatusAndStartDateAfter(Long ownerId, BookingStatus bookingStatus, LocalDateTime now);

    List<Booking> findByItem_Owner_IdAndStatus(Long ownerId, BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.status = :status AND b.endDate < :now ORDER BY b.endDate DESC")
    List<Booking> findLastBookings(@Param("itemId") Long itemId,
                                   @Param("status") BookingStatus status,
                                   @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.status = :status AND b.startDate > :now ORDER BY b.startDate ASC")
    List<Booking> findNextBookings(@Param("itemId") Long itemId,
                                   @Param("status") BookingStatus status,
                                   @Param("now") LocalDateTime now);
}

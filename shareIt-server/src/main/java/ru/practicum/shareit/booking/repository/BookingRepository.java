package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.Pagination;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pagination page);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now,
                                                                Pagination page);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now,
                                                                 Pagination page);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, Status status,
                                                             Pagination page);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long userId, Pagination page);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime now,
                                                                             LocalDateTime now1,
                                                                             Pagination page);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now,
                                                                Pagination page);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now,
                                                                 Pagination page);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long userId, Status status,
                                                             Pagination page);

    Optional<Booking> findFirstByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long userId,
                                                                        Status status, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Long itemId, Status status,
                                                                             LocalDateTime start);

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(Long id, Status status, LocalDateTime now);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime now,
                                                                          LocalDateTime now1,
                                                                          Pagination page);
}

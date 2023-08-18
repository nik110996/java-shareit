package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

public class BookingDtoMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemDtoMapper.toItemDto(booking.getItem()),
                UserDtoMapper.toUserRequestDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static BookingDtoForItem toBookingDtoForItem(Booking booking) {
        return new BookingDtoForItem(
                booking.getId(),
                booking.getItem(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDtoRequest bookingDto, Item item, User user, Status status) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(user)
                .item(item)
                .status(status)
                .build();
    }
}

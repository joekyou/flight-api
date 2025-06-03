package com.flight.controller;

import com.flight.dto.BaseResponse;
import com.flight.dto.BookingDTO;
import com.flight.service.BookingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseResponse<BookingDTO>> createBooking(
            @Valid @RequestBody BookingDTO bookingDTO,
            Authentication authentication) {
        BookingDTO createdBooking = bookingService.createBooking(bookingDTO, authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse<>(201, "预订创建成功", createdBooking));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseResponse<List<BookingDTO>>> getCurrentUserBookings(
            Authentication authentication,
            @RequestParam(required = false) String status) {
        List<BookingDTO> bookings = bookingService.getCurrentUserBookings(authentication, status);
        return ResponseEntity.ok(new BaseResponse<>(200, "获取预订列表成功", bookings));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseResponse<BookingDTO>> getBookingById(
            @PathVariable Long id,
            Authentication authentication) {
        BookingDTO booking = bookingService.getBookingById(id, authentication);
        return ResponseEntity.ok(new BaseResponse<>(200, "获取预订详情成功", booking));
    }

    @GetMapping("/reference/{reference}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseResponse<BookingDTO>> getBookingByReference(
            @PathVariable String reference,
            Authentication authentication) {
        BookingDTO booking = bookingService.getBookingByReference(reference, authentication);
        return ResponseEntity.ok(new BaseResponse<>(200, "获取预订详情成功", booking));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseResponse<Void>> cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {
        bookingService.cancelBooking(id, authentication);
        return ResponseEntity.ok(new BaseResponse<>(200, "预订取消成功", null));
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseResponse<List<BookingDTO>>> getUpcomingBookings(
            Authentication authentication) {
        List<BookingDTO> bookings = bookingService.getUpcomingBookings(authentication);
        return ResponseEntity.ok(new BaseResponse<>(200, "获取即将到来的预订成功", bookings));
    }

    @GetMapping("/past")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseResponse<List<BookingDTO>>> getPastBookings(
            Authentication authentication) {
        List<BookingDTO> bookings = bookingService.getPastBookings(authentication);
        return ResponseEntity.ok(new BaseResponse<>(200, "获取历史预订成功", bookings));
    }

    // Admin endpoints
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<BookingDTO>>> getAllBookings(
            @RequestParam(required = false) String status) {
        List<BookingDTO> bookings = bookingService.getAllBookings(status);
        return ResponseEntity.ok(new BaseResponse<>(200, "获取所有预订成功", bookings));
    }

    @GetMapping("/admin/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<BookingDTO>>> getBookingsByUser(
            @PathVariable Long userId) {
        List<BookingDTO> bookings = bookingService.getBookingsByUser(userId);
        return ResponseEntity.ok(new BaseResponse<>(200, "获取用户预订成功", bookings));
    }

    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<BookingDTO>> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        BookingDTO booking = bookingService.updateBookingStatus(id, status);
        return ResponseEntity.ok(new BaseResponse<>(200, "预订状态更新成功", booking));
    }
}

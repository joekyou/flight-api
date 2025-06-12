package com.flight.dto;

import com.flight.dto.FlightDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * 预订数据传输对象
 * 用于在前端和后端之间传输预订信息
 */
@Data
public class BookingDTO {
    /**
     * 预订ID
     */
    private Long id;
    
    /**
     * 预订参考号
     */
    private String bookingReference;
    
    /**
     * 主航班ID（可能是出发航班或返程航班）
     */
    private Long flightId;
    
    /**
     * 返程航班ID（仅往返行程时使用）
     */
    private Long returnFlightId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 主航班信息
     */
    private FlightDTO flight;
    
    /**
     * 返程航班信息
     */
    private FlightDTO returnFlight;
    
    /**
     * 行程类型：ONE_WAY（单程）或 ROUND_TRIP（往返）
     */
    private String flightType;
    
    /**
     * 主航班类型：OUTBOUND（出发）或 RETURN（返程）
     * 用于标识用户主要选择的是出发航班还是返程航班
     */
    private String mainFlightType;
    
    /**
     * 乘客数量
     */
    private int numberOfPassengers;
    
    /**
     * 总价格
     */
    private double totalPrice;
    
    /**
     * 预订状态
     */
    private String status;
    
    /**
     * 预订日期
     */
    private LocalDateTime bookingDate;
    
    /**
     * 乘客ID列表（用于创建预订时传递）
     */
    private List<Long> passengerIds;
    
    /**
     * 预订乘客信息列表（用于返回预订详情时）
     */
    private List<BookingPassengerDTO> bookingPassengers;
}

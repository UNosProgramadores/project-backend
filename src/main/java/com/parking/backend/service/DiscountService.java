package com.parking.backend.service;

import java.math.BigDecimal;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.User;

public interface DiscountService {
    // Retorna el monto a descontar (BigDecimal.ZERO si no aplica ningún descuento)
    BigDecimal calculateDiscount(ParkingLot parkingLot, User user, BigDecimal baseAmount);
}
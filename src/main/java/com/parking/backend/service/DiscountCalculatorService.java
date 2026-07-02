package com.parking.backend.service;

import com.parking.backend.entity.DiscountConfig;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.User;
import com.parking.backend.repository.DiscountConfigRepository;
import com.parking.backend.repository.EntryRecordRepository;
import com.parking.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class RealDiscountCalculator implements DiscountService {

    private final DiscountConfigRepository discountConfigRepository;
    private final EntryRecordRepository entryRecordRepository;
    private final PaymentRepository paymentRepository;

    public RealDiscountCalculator(DiscountConfigRepository discountConfigRepository,
                                   EntryRecordRepository entryRecordRepository,
                                   PaymentRepository paymentRepository) {
        this.discountConfigRepository = discountConfigRepository;
        this.entryRecordRepository = entryRecordRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public BigDecimal calculateDiscount(ParkingLot parkingLot, User user, BigDecimal baseAmount) {
        if (!Boolean.TRUE.equals(parkingLot.getDiscountsEnabled())) {
            return BigDecimal.ZERO;
        }

        Optional<DiscountConfig> configOpt = discountConfigRepository.findByParkingLotAndActiveTrue(parkingLot);
        if (configOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }

        DiscountConfig config = configOpt.get();
        boolean qualifies = false;

        if (config.getMinExternalInvoice() != null) {
            qualifies = paymentRepository.existsCompletedWithInvoiceRefByUserAndParkingLot(user, parkingLot);
        }

        if (!qualifies && config.getMinVisits() != null) {
            long visits = entryRecordRepository.countCompletedByOwnerAndParkingLot(user, parkingLot);
            qualifies = visits >= config.getMinVisits();
        }

        if (!qualifies) {
            return BigDecimal.ZERO;
        }

        return baseAmount
                .multiply(config.getDiscountPercentage())
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
    }
}

package com.parking.backend.repository;

import com.parking.backend.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT i FROM Invoice i JOIN i.payment p JOIN p.entryRecord er JOIN er.cell c JOIN c.parkingLot pl " +
           "WHERE pl.id = :parkingLotId " +
           "AND (COALESCE(:startDate, i.issuedAt) <= i.issuedAt) " +
           "AND (COALESCE(:endDate, i.issuedAt) >= i.issuedAt) " +
           "AND (COALESCE(:paymentMethod, p.paymentMethod) = p.paymentMethod) " +
           "AND (COALESCE(:minTotal, p.totalPaid) <= p.totalPaid) " +
           "AND (COALESCE(:maxTotal, p.totalPaid) >= p.totalPaid)")
    List<Invoice> findByParkingLotIdWithFilters(
            @Param("parkingLotId") Long parkingLotId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("paymentMethod") String paymentMethod,
            @Param("minTotal") BigDecimal minTotal,
            @Param("maxTotal") BigDecimal maxTotal);
}

package com.parking.backend.repository;

import com.parking.backend.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT i FROM Invoice i JOIN i.payment p JOIN p.entryRecord er JOIN er.cell c JOIN c.parkingLot pl WHERE pl.id = :parkingLotId")
    List<Invoice> findByParkingLotId(@Param("parkingLotId") Long parkingLotId);
}

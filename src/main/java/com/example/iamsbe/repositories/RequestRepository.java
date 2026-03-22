package com.example.iamsbe.repositories;

import com.example.iamsbe.models.entities.Asset;
import com.example.iamsbe.models.entities.Request;
import com.example.iamsbe.models.entities.User;
import com.example.iamsbe.models.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Page<Request> findAllByUserUsername(String username, Pageable pageable);
    boolean existsByUserAndAssetAndStatus(User user, Asset asset, String status);
    List<Request> findAllByStatusAndBorrowDateBefore(String status, LocalDate date);
    long countByStatus(String status);
    @Query("SELECT COUNT(r) FROM Request r WHERE r.borrowDate = :date")
    long countByBorrowDate(LocalDate date);
    Page<Request> findAllByStatus(String status, Pageable pageable);
}
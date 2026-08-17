package com.sooraj.payflow.repository;

import com.sooraj.payflow.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}

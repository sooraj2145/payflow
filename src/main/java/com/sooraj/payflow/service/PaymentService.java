package com.sooraj.payflow.service;


import com.sooraj.payflow.dto.PaymentRequest;
import com.sooraj.payflow.entity.Payment;
import com.sooraj.payflow.entity.PaymentStatus;
import com.sooraj.payflow.entity.TransactionLog;
import com.sooraj.payflow.exception.PaymentNotFoundException;
import com.sooraj.payflow.repository.PaymentRepository;
import com.sooraj.payflow.repository.TransactionLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionLogRepository transactionLogRepository;

    public PaymentService(PaymentRepository paymentRepository, TransactionLogRepository transactionLogRepository) {
        this.paymentRepository = paymentRepository;
        this.transactionLogRepository = transactionLogRepository;

    }

    @Transactional
    public Payment createPayment(PaymentRequest request) {
        Optional<Payment> existing = paymentRepository.findByIdempotencyKey(request.idempotencyKey());
        if (existing.isPresent()) {
            return existing.get();
        }

        Payment payment = new Payment();
        payment.setAmount(request.amount());
        payment.setCurrency(request.currency());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setIdempotencyKey(request.idempotencyKey());

        Payment saved = paymentRepository.save(payment);

        TransactionLog log = new TransactionLog(saved, null, PaymentStatus.PENDING);
        transactionLogRepository.save(log);

        return saved;
    }

    @Transactional
    public Payment refundPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        PaymentStatus previousStatus = payment.getStatus();
        payment.setStatus(PaymentStatus.REFUNDED);

        Payment saved = paymentRepository.save(payment);

        TransactionLog log = new TransactionLog(saved, previousStatus, PaymentStatus.REFUNDED);
        transactionLogRepository.save(log);

        return saved;
    }

    public Payment getPayment(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }
}

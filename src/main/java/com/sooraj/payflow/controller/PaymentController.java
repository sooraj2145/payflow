package com.sooraj.payflow.controller;


import com.sooraj.payflow.dto.PaymentRequest;
import com.sooraj.payflow.entity.Payment;
import com.sooraj.payflow.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
public class PaymentController {


   private final PaymentService paymentService;

   public PaymentController(PaymentService paymentService) {
       this.paymentService = paymentService;
   }


    @GetMapping("/payments/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
       Payment payment = paymentService.getPayment(id);

        return ResponseEntity.ok(payment);
    }

    @PostMapping("/payments")
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody PaymentRequest request)  {
        Payment saved =  paymentService.createPayment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/payments/{id}/refund")
    public ResponseEntity<Payment> refundPayment(@PathVariable Long id)  {
       Payment refund = paymentService.refundPayment(id);
       return ResponseEntity.ok(refund);
    }
}

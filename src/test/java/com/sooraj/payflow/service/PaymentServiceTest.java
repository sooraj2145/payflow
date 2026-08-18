package com.sooraj.payflow.service;

import com.sooraj.payflow.dto.PaymentRequest;
import com.sooraj.payflow.entity.Payment;
import com.sooraj.payflow.entity.PaymentStatus;
import com.sooraj.payflow.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPayment_savesNewPayment_whenIdempotencyKeyIsNew() {
        PaymentRequest request = new PaymentRequest(BigDecimal.valueOf(100), "INR", "key-abc");

        when(paymentRepository.findByIdempotencyKey("key-abc")).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.createPayment(request);

        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PENDING);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
}
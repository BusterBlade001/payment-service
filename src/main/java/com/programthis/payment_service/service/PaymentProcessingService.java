package com.programthis.payment_service.service;

import com.programthis.payment_service.entity.Payment;
import com.programthis.payment_service.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID; // Para generar un ID de transacción

@Service
public class PaymentProcessingService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Simulación de un DTO (Data Transfer Object) para la solicitud de pago
    public static record PaymentRequest(String orderId, BigDecimal amount, String paymentMethodDetails) {}

    public Payment processPayment(PaymentRequest paymentRequest) {
        // Aquí iría la lógica de integración con una pasarela de pagos real (Stripe, PayPal, MercadoPago, etc.)
        // Por ahora, simularemos un procesamiento exitoso.

        Payment payment = new Payment();
        payment.setOrderId(paymentRequest.orderId());
        payment.setAmount(paymentRequest.amount());
        payment.setPaymentMethod("PROCESSED_METHOD"); // Extraer de paymentRequest.paymentMethodDetails
        payment.setTransactionDate(LocalDateTime.now());

        // Simulación de respuesta de pasarela de pago
        boolean paymentSuccessful = simulatePaymentGatewayInteraction(paymentRequest.paymentMethodDetails());

        if (paymentSuccessful) {
            payment.setPaymentStatus("COMPLETED");
            payment.setTransactionId(UUID.randomUUID().toString()); // ID de transacción real de la pasarela
        } else {
            payment.setPaymentStatus("FAILED");
            // No se asigna transactionId si falla antes de la pasarela, o se guarda el error de la pasarela.
        }
        return paymentRepository.save(payment);
    }

    private boolean simulatePaymentGatewayInteraction(String paymentMethodDetails) {
        // Simulación: aquí conectarías con la pasarela.
        // Por ejemplo, si 'paymentMethodDetails' contiene "fail_card", simulas un fallo.
        System.out.println("Simulating payment gateway interaction for: " + paymentMethodDetails);
        return !paymentMethodDetails.contains("fail"); // Falla si contiene "fail"
    }

    public Payment getPaymentStatusByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order ID: " + orderId));
    }

    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found for transaction ID: " + transactionId));
    }

    // Podrías añadir métodos para reembolso, etc.
}
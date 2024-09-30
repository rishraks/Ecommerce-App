package com.ecommerce.application.service;


import com.ecommerce.application.payload.OrderDTO;
import jakarta.transaction.Transactional;

public interface OrderService {
    @Transactional
    OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgPaymentStatus, String pgResponseMessage);
}

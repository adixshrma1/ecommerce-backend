package com.aditya.project.service;

import com.aditya.project.payload.OrderDTO;
import jakarta.transaction.Transactional;

public interface OrderService {
    @Transactional
    OrderDTO placeOrder(String email, Long addressId, String paymentMethod, String pgName,
                        String pgPaymentId, String pgStatus, String pgResponseMessage);

}

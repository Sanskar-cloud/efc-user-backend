package com.example.efc_user.services;

import com.example.efc_user.collections.Order;
import com.example.efc_user.payloads.OrderDto;
import com.example.efc_user.payloads.OrderRequest;

import java.security.Principal;
import java.util.List;

public interface OrderService {
    public String createOrder(Principal principal, OrderRequest orderRequest);
    public List<OrderDto> getUserOrders(String userId) ;


}

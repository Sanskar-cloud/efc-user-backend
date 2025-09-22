package com.example.efc_user.controllers;



import com.example.efc_user.collections.User;
import com.example.efc_user.payloads.CartDto;
import com.example.efc_user.payloads.OrderItemDto;
import com.example.efc_user.payloads.Updatequantityrequest;
import com.example.efc_user.response.BaseApiResponse;
import com.example.efc_user.response.LoginDeliveryResponse;
import com.example.efc_user.security.CustomUserDetailService;
import com.example.efc_user.services.impl.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<BaseApiResponse<String>> addToCart(Principal principal, @RequestBody OrderItemDto orderItem) {

        CartDto cartDto= cartService.addToCart(principal, orderItem);
        return ResponseEntity.ok(
                BaseApiResponse.<String>builder()
                        .success(true)
                        .message("Successfully Added")
                        .data(null)
                        .build()
        );
    }

    @DeleteMapping("/remove")
    public ResponseEntity<BaseApiResponse<String>> removeFromCart(Principal principal, @RequestParam String itemId) {
        CartDto cartDto= cartService.removeFromCart(principal, itemId);
        return ResponseEntity.ok(
                BaseApiResponse.<String>builder()
                        .success(true)
                        .message("Successfully Removed")
                        .data(null)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<BaseApiResponse<CartDto>> getCart(Principal principal) {
        CartDto cartDto= cartService.getCart(principal);
        return ResponseEntity.ok(
                BaseApiResponse.<CartDto>builder()
                        .success(true)
                        .message("Successfully Retrieved")
                        .data(cartDto)
                        .build()
        );
    }

    @DeleteMapping("/clear")
    public void clearCart(Principal principal) {
        cartService.clearCart(principal);
    }
    @PostMapping("/update-quantity")
    public ResponseEntity<BaseApiResponse<CartDto>> updateCartQuantity(Principal principal, @RequestBody Updatequantityrequest updateRequest) {
        CartDto updatedCart = cartService.updateQuantity(principal, updateRequest.getItemId(), updateRequest.getQuantity());
        return ResponseEntity.ok(
                BaseApiResponse.<CartDto>builder()
                        .success(true)
                        .message("Quantity updated successfully")
                        .data(updatedCart)
                        .build()
        );
    }
}


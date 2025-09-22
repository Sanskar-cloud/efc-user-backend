package com.example.efc_user.services.impl;




import com.example.efc_user.collections.Cart;
import com.example.efc_user.collections.User;
import com.example.efc_user.payloads.CartDto;
import com.example.efc_user.payloads.OrderItemDto;
import com.example.efc_user.repo.CartRepo;
import com.example.efc_user.repo.UserRepo;
import com.example.efc_user.security.CustomUserDetailService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    @Autowired
    private CartRepo cartRepository;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;

    public CartDto addToCart(Principal principal, OrderItemDto orderItem) {

        User user=userRepo.findByEmail(principal.getName()).orElseThrow();
        Cart cart = cartRepository.findByUserId(user.getId()).orElse(new Cart(user.getId()));
        cart.setUserId(user.getId());
        cart.addItem(orderItem);
        cart.calculateTotalPrice();
        Cart savedCart=cartRepository.save(cart);
        return this.modelMapper.map(savedCart,CartDto.class);
    }

    public CartDto removeFromCart(Principal principal, String itemId) {
        User user=userRepo.findByEmail(principal.getName()).orElseThrow();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + user.getId()));
        cart.removeItem(itemId);
        cart.calculateTotalPrice();
        cartRepository.save(cart);
        return new CartDto(cart);
    }

    public CartDto getCart(Principal principal) {
        User user=userRepo.findByEmail(principal.getName()).orElseThrow();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + user.getId()));
        return new CartDto(cart);
    }

    public void clearCart(Principal principal) {
        User user=userRepo.findByEmail(principal.getName()).orElseThrow();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + user.getId()));
        cart.getItems().clear();
        cart.calculateTotalPrice();
        cartRepository.save(cart);
    }
    public CartDto updateQuantity(Principal principal, String itemId, int quantity) {
        // Fetch the user's cart
        User user=userRepo.findByEmail(principal.getName()).orElseThrow();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        // Find the item in the cart
        OrderItemDto item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getItemId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        // Update the item's quantity
        item.setQuantity(quantity);

        // Save the updated cart
       Cart savedCart= cartRepository.save(cart);

        // Map the updated cart to CartDto and return
        return this.modelMapper.map(savedCart,CartDto.class);
    }

}

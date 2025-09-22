package com.example.efc_user.services;

import com.example.efc_user.collections.*;
import com.example.efc_user.payloads.*;
import com.example.efc_user.repo.AddressRepo;
import com.example.efc_user.repo.DeliveryBoyRepo;
import com.example.efc_user.repo.OarderRepo;
import com.example.efc_user.repo.UserRepo;
import com.example.efc_user.services.impl.CartService;
import org.checkerframework.checker.units.qual.A;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class OrderServiceImpl implements OrderService{

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired

    private KafkaTemplate<String, Order> orderKafkaTemplate;

    @Autowired
    UserRepo userRepo;
    @Autowired
    AddressRepo addressRepo;
    @Autowired
    DeliveryBoyRepo deliveryBoyRepo;
    @Autowired
    SimpMessagingTemplate simpmessagingTemplate;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    OarderRepo oarderRepo;
    @Autowired
    CartService cartService;

    @Override
    public String createOrder(Principal principal, OrderRequest orderRequest) {
//        User user = userRepo.findByEmail(principal.getName())
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        List<OrderItemDto> orderItem = orderRequest.getItems();
//        List<OrderItem> orderItems = orderRequest.getItems().stream()
//                .map(itemDto -> modelMapper.map(itemDto, OrderItem.class))
//                .toList();
//
//        AddressDto deliveryAddress = orderRequest.getAddressDto();
//        Address address = this.modelMapper.map(deliveryAddress, Address.class);
//
//        Order order = new Order();
//        order.setOrderTime(LocalDateTime.now());
//        order.setItems(orderItems);
//        order.setUser(user);
//        order.setDeliveryStatus(null);
//        order.setDeliveryBoy(null);
//        order.setDeliveryAddress(address);
//        order.setDeliveryType(orderRequest.getOrdertype());
//        order.setPaymentMethod(orderRequest.getPaymentMethod());
//        order.setPaymentStatus("Unpaid");
//        order.setTotalPrice(calculateTotalPrice(orderItems));
//
//        Order savedOrder = oarderRepo.save(order);
//
//        // Initialize the order list if it is null
////        if (user.getOrderIds() == null) {
////            user.setOrderIds(new ArrayList<>()); // Initialize the list
////        }
////
////        user.getOrderIds().add(savedOrder.getId());
////
////        // Save the updated user (if needed by your JPA setup)
////        userRepo.save(user);
//
//        // Publish order to Kafka
//
//
//        return "Order Placed Successfully!!";
//    }
        return "";
    }


    @Override
    public List<OrderDto> getUserOrders(String userId) {
        // Fetch orders for the user
        List<Order> orders = oarderRepo.findByUserId(userId);

        // Map each Order to an OrderDto
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .toList();
    }



    public  List<OrderDeliverBoyDto> getPendingOrders(){
        List<Order> pendingOrders = oarderRepo.findByDeliveryBoyIsNull();
        return pendingOrders.stream()
                .map(order -> modelMapper.map(order, OrderDeliverBoyDto.class))
                .toList();
    }
    public Order acceptOrder(String orderId, Principal principal) {

        // Fetch the order
        Order order = oarderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " not found"));

        // Validate the order's current state
        if (order.getDeliveryStatus() != null) {
            throw new IllegalStateException("Order is not available for assignment");
        }

        // Get the delivery boy
        String userName = principal.getName();
        DeliveryBoy deliveryBoy = deliveryBoyRepo.findByEmail(userName)
                .orElseThrow(() -> new IllegalArgumentException("Delivery boy with email " + userName + " not found"));

        // Update order details
        order.setDeliveryBoy(deliveryBoy);
        order.setDeliveryStatus("Out for Delivery");

        // Save the updated order
        oarderRepo.save(order);

        // Log the action


        // Return the updated order
        return order;
    }


    private double calculateTotalPrice(List<OrderItem> items) {
        return items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }
    @Transactional
    public OrderDto createOrderFromCart(Principal principal, OrderFromCartRequest request) {
        User user = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CartDto cart = cartService.getCart(principal);
        Cart cart1 = this.modelMapper.map(cart, Cart.class);

        if (cart1.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot create an order.");
        }

        double totalPrice = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        List<OrderItem> orderItems = cart1.getItems().stream()
                .map(item -> this.modelMapper.map(item, OrderItem.class))
                .toList();

        AddressDto addressDto = request.getDeliveryAddress();
        if (addressDto == null || addressDto.getAddressLine1() == null) {
            throw new RuntimeException("Delivery address is incomplete. Cannot create order.");
        }

        Address address = this.modelMapper.map(addressDto, Address.class);

        Order order = new Order();
        order.setUser(user);
        order.setItems(orderItems);
        order.setDeliveryAddress(address);
        order.setDeliveryType(request.getDeliveryType());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentStatus("Paid");
        order.setDeliveryStatus("Pending");
        order.setTotalPrice(totalPrice);
        order.setOrderTime(LocalDateTime.now());

        Order savedOrder = oarderRepo.save(order);

        address.setOrderId(savedOrder.getId());
        addressRepo.save(address);

        simpmessagingTemplate.convertAndSend("/topic/orders", savedOrder);


//        try {
//            SendResult<String, Order> result = orderKafkaTemplate.send("order-topic", savedOrder).get();
//            log.info("Message sent to Kafka successfully: topic = {}, partition = {}, offset = {}",
//                    result.getRecordMetadata().topic(),
//                    result.getRecordMetadata().partition(),
//                    result.getRecordMetadata().offset());
//        } catch (Exception ex) {
//            log.error("Failed to send message to Kafka: {}", ex.getMessage(), ex);
//            throw new RuntimeException("Error while sending order to Kafka", ex);
//        }


        log.info(user.getPhone());
        cartService.clearCart(principal);

        return this.modelMapper.map(savedOrder, OrderDto.class);

    }

    private void sendWhatsAppMessageUsingTemplate(String phoneNumber, Order order) {
        String apiUrl = "https://graph.facebook.com/v17.0/491294544076498/messages";
        String accessToken = "EAASNxZBjzZAVEBO0DM9vEeIG8yRuyqeFLI0FZCptL38BQW4Mkyh5PDA0yTNZCeZBTLMYpDwQZBen1W5cbZBy71isHpOUKZAvyZCYNalsRKb52mWV2ZCFItFZCQ9A9S0IkP34k85Opg3d7y6ssRfoqj9fCeNnLZBh1l2P746fNLRwZBXjJUpRbYIRFZAEtlwNNOvv2nf6X7KX7vmm5uZCT2ZC7Hu0Pp9Fwn7TFWMZD"; // Secure storage

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        log.info(order.getDeliveryAddress().getAddressLine1()+"68845784");

        String formattedAddress = String.format("%s, %s, %s - %s",

                order.getDeliveryAddress().getAddressLine1(),
                order.getDeliveryAddress().getCity(),
                order.getDeliveryAddress().getState(),
                order.getDeliveryAddress().getPostalCode());

        Map<String, Object> payload = Map.of(
                "messaging_product", "whatsapp",
                "to", "+91"+phoneNumber,
                "type", "template",
                "template", Map.of(
                        "name", "order_confirm",
                        "language", Map.of("code", "en_US"),
                        "components", List.of(
                                Map.of(
                                        "type", "body",
                                        "parameters", List.of(
                                                Map.of("type", "text", "text", order.getUser().getName()),
                                                Map.of("type", "text", "text", String.valueOf(order.getId())),
                                                Map.of("type", "text", "text", formattedAddress)
                                        )
                                )
                        )
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("WhatsApp template message sent: {}", response.getBody());
            } else {
                log.error("Failed to send WhatsApp message with status: {} and body: {}",
                        response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("Failed to send WhatsApp template message: {}", e.getMessage(), e);
        }
    }
    @Transactional
    public Order updateOrderStatus(String orderId, String status) {
        Order order = oarderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setDeliveryStatus(status);
        oarderRepo.save(order);

        // Broadcast the updated order to all clients
        simpmessagingTemplate.convertAndSend("/topic/order-updates", order);

        return order;
    }


}




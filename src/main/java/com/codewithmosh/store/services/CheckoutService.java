package com.codewithmosh.store.services;

import org.springframework.stereotype.Service;

import com.codewithmosh.store.Dtos.CheckoutRequest;
import com.codewithmosh.store.Dtos.CheckoutResponse;
import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.entities.OrderStatus;
import com.codewithmosh.store.entities.Order_items;
import com.codewithmosh.store.exceptions.CartEmptyException;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.PaymentException;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepository;
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;


    public CheckoutResponse checkout(CheckoutRequest request){
                var cart = cartRepository.findById(request.getCartId()).orElse(null);
        if(cart == null){
            throw new CartNotFoundException();
        }
        if(cart.getCartItems() == null || cart.getCartItems().isEmpty()){
           throw new CartEmptyException();
        }
        var order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(cart.getTotalPrice());
        order.setCustomer(authService.getCurrentUser());

        cart.getCartItems().forEach(item ->{
            var order_items = new Order_items();
            order_items.setOrder(order);
            order_items.setProduct(item.getProduct());
            order_items.setQuantity(item.getQuantity());
            order_items.setTotal_price(item.getTotalPrice());
            order_items.setUnitPrice(item.getProduct().getPrice());
            order.getItems().add(order_items);
        });

        orderRepository.save(order);
        //create checkout session
        try {
            var session = paymentGateway.createCheckoutSession(order);
            cart.getCartItems().clear();

            return new CheckoutResponse(order.getId() , session.getUrl());

        } catch (PaymentException e) {
            orderRepository.delete(order);
            throw e;
        }
    } 

    public void handleWebhookEvent(WebhookRequest request){
        paymentGateway
            .parseWebhookRequest(request)
            .ifPresent(paymentResult->{
                var order = orderRepository.findById(paymentResult.getOrderId()).orElseThrow();
                order.setStatus(paymentResult.getPaymenStatus());
                orderRepository.save(order);
            });
       
    }
}

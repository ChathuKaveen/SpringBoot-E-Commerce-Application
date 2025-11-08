package com.codewithmosh.store.services;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.codewithmosh.store.Dtos.CheckoutRequest;
import com.codewithmosh.store.Dtos.CheckoutResponse;
import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.entities.OrderStatus;
import com.codewithmosh.store.entities.Order_items;
import com.codewithmosh.store.exceptions.CartEmptyException;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.OrderRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepository;
    private final AuthService authService;
    private final OrderRepository orderRepository;
    public CheckoutResponse checkout(CheckoutRequest request) throws StripeException{
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
            var builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://localhost:4242/payment-success?orderId="+order.getId())
                .setCancelUrl("https://localhost:4242/payment-cancel");
        order.getItems().forEach(item ->{
            var lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(Long.valueOf(item.getQuantity()))
                .setPriceData(
                    SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("usd")
                        .setUnitAmountDecimal(item.getUnitPrice().multiply(BigDecimal.valueOf(100)))
                        .setProductData(
                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(item.getProduct().getName())
                                .build()   
                        )
                        .build()
                )
                .build();
               builder.addLineItem(lineItem);
        });

        var session = Session.create(builder.build());
        cart.getCartItems().clear();

        return new CheckoutResponse(order.getId() , session.getUrl());

        } catch (StripeException e) {
            System.out.println(e);
            orderRepository.delete(order);
            throw e;
        }
    } 
}

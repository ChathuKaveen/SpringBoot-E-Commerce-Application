package com.codewithmosh.store.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codewithmosh.store.Dtos.CheckoutRequest;
import com.codewithmosh.store.services.CheckoutService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
@AllArgsConstructor
@RequestMapping("/checkout")
@RestController
public class CheckoutController {
   
    private final CheckoutService checkoutService;

    @PostMapping
    @Transactional
    public ResponseEntity<?> checkout(@Valid @RequestBody CheckoutRequest request) throws StripeException{
        // var cart = cartRepository.findById(request.getCartId()).orElse(null);
        // if(cart == null){
        //     return ResponseEntity.badRequest().body(Map.of("error", "cart Not Found"));
        // }
        // if(cart.getCartItems() == null || cart.getCartItems().isEmpty()){
        //     return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty"));
        // }
        // var order = new Order();
        // order.setStatus(OrderStatus.PENDING);
        // order.setTotalPrice(cart.getTotalPrice());
        // order.setCustomer(authService.getCurrentUser());

        // cart.getCartItems().forEach(item ->{
        //     var order_items = new Order_items();
        //     order_items.setOrder(order);
        //     order_items.setProduct(item.getProduct());
        //     order_items.setQuantity(item.getQuantity());
        //     order_items.setTotal_price(item.getTotalPrice());
        //     order_items.setUnitPrice(item.getProduct().getPrice());
        //     order.getItems().add(order_items);
        // });

        // orderRepository.save(order);

        // //create checkout session

        // try {
        //     var builder = SessionCreateParams.builder()
        //         .setMode(SessionCreateParams.Mode.PAYMENT)
        //         .setSuccessUrl("https://localhost:4242/payment-success?orderId="+order.getId())
        //         .setCancelUrl("https://localhost:4242/payment-cancel");
        // order.getItems().forEach(item ->{
        //     var lineItem = SessionCreateParams.LineItem.builder()
        //         .setQuantity(Long.valueOf(item.getQuantity()))
        //         .setPriceData(
        //             SessionCreateParams.LineItem.PriceData.builder()
        //                 .setCurrency("usd")
        //                 .setUnitAmountDecimal(item.getUnitPrice().multiply(BigDecimal.valueOf(100)))
        //                 .setProductData(
        //                     SessionCreateParams.LineItem.PriceData.ProductData.builder()
        //                         .setName(item.getProduct().getName())
        //                         .build()   
        //                 )
        //                 .build()
        //         )
        //         .build();
        //        builder.addLineItem(lineItem);
        // });

        // var session = Session.create(builder.build());
        // cart.getCartItems().clear();
        // return ResponseEntity.ok(new CheckoutResponse(order.getId() , session.getUrl()));
        // } catch (StripeException e) {
        //     System.out.println(e);
        //     orderRepository.delete(order);
        //     return ResponseEntity
        //                 .status(HttpStatus.INTERNAL_SERVER_ERROR)
        //                 .body("Checkout failed");
        // }
        try {
            return ResponseEntity.ok(checkoutService.checkout(request));
        } catch (StripeException e) {
           return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro creating checkout");
        }
    }
}

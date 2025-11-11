package com.codewithmosh.store.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codewithmosh.store.Dtos.CheckoutRequest;
import com.codewithmosh.store.Dtos.CheckoutResponse;
import com.codewithmosh.store.exceptions.PaymentException;
import com.codewithmosh.store.services.CheckoutService;
import com.codewithmosh.store.services.WebhookRequest;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@RequestMapping("/checkout")
@RestController
public class CheckoutController {
   
    private final CheckoutService checkoutService;
   

    @PostMapping
    @Transactional
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request) throws StripeException{
        return checkoutService.checkout(request);
    }

    @PostMapping("/webhook")
    public void handleWebhook(@RequestHeader Map<String , String> headers , @RequestBody String payload){
           checkoutService.handleWebhookEvent(new WebhookRequest(headers , payload));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<?> handlePaymentException(){
        return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro creating checkout");
    }   
}

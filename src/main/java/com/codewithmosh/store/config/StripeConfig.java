package com.codewithmosh.store.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.stripe.Stripe;

import jakarta.annotation.PostConstruct;

@Configuration
public class StripeConfig {
    @Value("${stripe.stripeKey}")
    private String secretKy;

    @PostConstruct
    public void init(){
        Stripe.apiKey = secretKy;
    }
}

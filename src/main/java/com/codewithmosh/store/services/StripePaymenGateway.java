package com.codewithmosh.store.services;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.entities.OrderStatus;
import com.codewithmosh.store.exceptions.PaymentException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripePaymenGateway implements PaymentGateway{

     @Value("${stripe.webhookSeceret}")
    private String webhookSecert;

    @Override
    public CheckoutSession createCheckoutSession(Order order) {

            try {
                var builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://localhost:4242/payment-success?orderId="+order.getId())
                .setCancelUrl("https://localhost:4242/payment-cancel")
                .putMetadata("order_id", order.getId().toString());
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

            return new CheckoutSession(session.getUrl());
            } catch (StripeException e) {
                System.out.println(e.getMessage());
                throw new PaymentException();
            }
    }

    @Override
    public Optional<PaymentResult> parseWebhookRequest(WebhookRequest request) {
         try {
            var payload = request.getPayload();
            var signature = request.getHeaders().get("stripe-Signature");
            var event = Webhook.constructEvent(payload, signature, webhookSecert);
            System.out.println(event.getType());
            var stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);

            switch (event.getType()) {
                case "payment_intent.succeeded"->{
                    var paymentIntent = (PaymentIntent) stripeObject;
                    if(paymentIntent !=null){
                        var orderId = paymentIntent.getMetadata().get("order_id");
                        return Optional.of(new PaymentResult(Long.valueOf(orderId) , OrderStatus.PAID));
                    }
                    
                }
                case "payment_intent.payment_failed"->{
                    var paymentIntent = (PaymentIntent) stripeObject;
                    if(paymentIntent !=null){
                        var orderId = paymentIntent.getMetadata().get("order_id");
                        return Optional.of(new PaymentResult(Long.valueOf(orderId) , OrderStatus.FAILED));
                    }
                }
                
            }

            return Optional.empty(); 
         } catch (SignatureVerificationException e) {
            throw new PaymentException();
         }
    }

}

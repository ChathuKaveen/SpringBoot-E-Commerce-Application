package com.codewithmosh.store.controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.codewithmosh.store.Dtos.AddToCartRequest;
import com.codewithmosh.store.Dtos.CartDto;
import com.codewithmosh.store.Dtos.CartItemsDto;
import com.codewithmosh.store.Dtos.UpdateCartItemRequest;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.ProductNotFoundException;
import com.codewithmosh.store.services.CartService;

import lombok.AllArgsConstructor;

@AllArgsConstructor

@RestController
@RequestMapping("/carts")
public class CartController {
    private CartService cartService;

    @PostMapping
    public ResponseEntity<CartDto> createCart(){
        var cartDto = cartService.createCart();
        return new ResponseEntity<>(cartDto , HttpStatus.CREATED);
    }

    @PostMapping("/{cartId}/item")
    public ResponseEntity<CartItemsDto> addToCart(@PathVariable(name = "cartId") UUID cartId ,@RequestBody AddToCartRequest request){
        var cartItemDto = cartService.addToCart(cartId, request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartDto> getCart(@PathVariable UUID cartId){
        return ResponseEntity.ok(cartService.getCart(cartId));
    }


    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> updateItem(@PathVariable(name = "cartId") UUID cartId , @PathVariable(name = "productId") Long productId , @RequestBody UpdateCartItemRequest request){
        return ResponseEntity.ok(cartService.updateCart(cartId, productId, request.getQuantity()));
    }

    
    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> deleteItem(@PathVariable(name = "cartId") UUID cartId , @PathVariable(name = "productId") Long productId){
        cartService.clearCart(cartId, productId);
        return ResponseEntity.noContent().build();    
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleCartNotFound(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Cart Not Found"));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleProductNotFound(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product Not Found"));
    }
    
    

   

}

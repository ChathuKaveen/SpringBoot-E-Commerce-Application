package com.codewithmosh.store.controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.codewithmosh.store.Mappers.CartMapper;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.entities.CartItem;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import com.codewithmosh.store.services.CartService;

import lombok.AllArgsConstructor;

@AllArgsConstructor

@RestController
@RequestMapping("/carts")
public class CartController {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;
    private CartService cartService;

    
    @PostMapping
    public ResponseEntity<CartDto> createCart(){
        var cartDto = cartService.createCart();
        return new ResponseEntity<>(cartDto , HttpStatus.CREATED);
    }

    @PostMapping("/{cartId}/item")
    public ResponseEntity<CartItemsDto> addToCart(@PathVariable UUID cartId ,@RequestBody AddToCartRequest request){
        var cartItemDto = cartService.addToCart(cartId, request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartDto> getCart(@PathVariable UUID cartId){
        var cart = cartRepository.findById(cartId).orElse(null);
        if(cart == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cartMapper.toDto(cart));
    }


    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> updateItem(@PathVariable(name = "cartId") UUID cartId , @PathVariable(name = "productId") Long productId , @RequestBody UpdateCartItemRequest request){
        var cart = cartRepository.findById(cartId).orElse(null);
        if(cart == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", "Cart Not Found")
            );
        }
        var cartItems = cart.getCartItems().stream().filter(item ->item.getProduct().getId().equals(productId))
        .findFirst()
        .orElse(null);
        if(cartItems == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", "Cart Item Not Found")
            );
        }
        cartItems.setQuantity(request.getQuantity());
        cartRepository.save(cart);
        return ResponseEntity.ok(cartMapper.toDto(cartItems));
    
    
    }
    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> deleteItem(@PathVariable(name = "cartId") UUID cartId , @PathVariable(name = "productId") Long productId){
        var cart = cartRepository.findById(cartId).orElse(null);
        if(cart == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", "Cart Not Found")
            );
        }
        var cartItems = cart.getCartItems().stream().filter(item ->item.getProduct().getId().equals(productId))
        .findFirst()
        .orElse(null);
        if(cartItems != null){
            cart.getCartItems().remove(cartItems);
            cartRepository.save(cart);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", "Cart Item Not Found")
            );
        
    }
    
    

   

}

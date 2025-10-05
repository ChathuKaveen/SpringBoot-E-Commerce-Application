package com.codewithmosh.store.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.codewithmosh.store.Dtos.CartDto;
import com.codewithmosh.store.Dtos.CartItemsDto;
import com.codewithmosh.store.Dtos.UpdateCartItemRequest;
import com.codewithmosh.store.Mappers.CartMapper;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.entities.CartItem;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.ProductNotFoundException;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.ProductRepository;

@Service
public class CartService {
    private CartMapper cartMapper;
    private CartRepository cartRepository;
    private ProductRepository productRepository;
        
    public CartDto createCart(){
        var cart  = new Cart();
        cartRepository.save(cart);

        return cartMapper.toDto(cart);
    }

    public CartItemsDto addToCart(UUID cartId ,Long productId){
        var cart  = cartRepository.findById(cartId).orElse(null);
        if(cart == null){
            throw new CartNotFoundException();
        }

        var product = productRepository.findById(productId).orElse(null);
        if(product == null){
            throw new ProductNotFoundException();
        }

        var cartItems = cart.getCartItems().stream().filter(item ->item.getProduct().getId().equals(product.getId()))
                        .findFirst()
                        .orElse(null);
        if(cartItems != null){
            cartItems.setQuantity(cartItems.getQuantity()+1);
        }else{
            cartItems = new CartItem();
            cartItems.setProduct(product);
            cartItems.setQuantity(1);
            cartItems.setCart(cart);
            cart.getCartItems().add(cartItems);
        }
        cartRepository.save(cart);
        return cartMapper.toDto(cartItems);
    }

    public CartDto getCart(UUID cartId){
        var cart = cartRepository.findById(cartId).orElse(null);
        if(cart == null){
           throw new CartNotFoundException();
        }

        return cartMapper.toDto(cart);
    }

    public CartItemsDto updateCart(UUID cartId ,Long productId , int quantity){
        var cart = cartRepository.findById(cartId).orElse(null);
        if(cart == null){
            throw new CartNotFoundException();
        }
        var cartItems = cart.getCartItems().stream().filter(item ->item.getProduct().getId().equals(productId))
        .findFirst()
        .orElse(null);
        if(cartItems == null){
            throw new ProductNotFoundException();
        }
        cartItems.setQuantity(quantity);
        cartRepository.save(cart);
        return cartMapper.toDto(cartItems);
    }

    public void clearCart(UUID cartId ,Long productId){
        var cart = cartRepository.findById(cartId).orElse(null);
        if(cart == null){
           throw new CartNotFoundException();
        }
        var cartItems = cart.getCartItems().stream().filter(item ->item.getProduct().getId().equals(productId))
        .findFirst()
        .orElse(null);
        if(cartItems == null){
            throw new ProductNotFoundException();
        }
        cart.getCartItems().remove(cartItems);
        cartRepository.save(cart);
        
        
    }
}

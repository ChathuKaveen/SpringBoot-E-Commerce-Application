package com.codewithmosh.store.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.codewithmosh.store.Dtos.CartDto;
import com.codewithmosh.store.Dtos.CartItemsDto;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.entities.CartItem;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "items" , source = "cartItems")
    @Mapping(target = "totalPrice" , expression = "java(cart.getTotalPrice())")
    CartDto toDto(Cart cart);
    
    @Mapping(target = "totalPrice" , expression = "java(cartItem.getTotalPrice())")
    CartItemsDto toDto(CartItem cartItem);
}

package com.codewithmosh.store.Mappers;

import org.mapstruct.Mapper;

import com.codewithmosh.store.Dtos.OrderDto;
import com.codewithmosh.store.entities.Order;

@Mapper(componentModel = "Spring")//, uses = {OrderItemMapper.class}
public interface OrderMapper {
    OrderDto toDto(Order order);
}

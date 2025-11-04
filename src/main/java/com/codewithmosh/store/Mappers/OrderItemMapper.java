package com.codewithmosh.store.Mappers;

import org.mapstruct.Mapper;
import com.codewithmosh.store.Dtos.OrderItemDto;
import com.codewithmosh.store.entities.Order_items;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface OrderItemMapper {
    OrderItemDto toDto(Order_items orderItem);
}

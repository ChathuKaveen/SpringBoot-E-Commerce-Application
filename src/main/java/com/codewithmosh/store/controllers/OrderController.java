package com.codewithmosh.store.controllers;

import java.util.List;
import com.codewithmosh.store.repositories.OrderRepository;
import com.codewithmosh.store.services.AuthService;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codewithmosh.store.Dtos.OrderDto;
import com.codewithmosh.store.Mappers.OrderMapper;
import com.codewithmosh.store.exceptions.OrderNotFoundException;

@AllArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderRepository orderRepository;

    private final AuthService authService;
    private final OrderMapper orderMapper;

    @GetMapping
    public List<OrderDto> getAllOrders(){
        var user = authService.getCurrentUser();
        var order = orderRepository.findAllByCustomer(user);
        return order.stream().map(orderMapper::toDto).toList();
    }

    @GetMapping("/{orderId}")
    public OrderDto getOrder(@PathVariable("orderId")Long orderId){
        var order = orderRepository.getOrderWithItems(orderId).orElse(null);
        if(order==null){
            throw new OrderNotFoundException(); 
        }

        var user = authService.getCurrentUser();
        if(!order.getCustomer().getId().equals(user.getId())){
            throw new AccessDeniedException("You dont have access");
        }
        return orderMapper.toDto(order);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Void> handelOrderNotFound(){
        return ResponseEntity.notFound().build();
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> handleAccessDenied(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

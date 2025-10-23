package com.codewithmosh.store.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    @Column(name = "id" , nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cutomer_id")
    private User user;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    @Column(name = "created_at" , insertable = false , updatable = false)
    private LocalDateTime created_at;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order")
    private Set<Order_items> items= new LinkedHashSet<>(); 

}

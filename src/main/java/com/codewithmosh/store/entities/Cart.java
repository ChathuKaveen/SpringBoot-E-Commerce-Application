package com.codewithmosh.store.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;


    @Column(name = "date_created" , insertable = false , updatable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "cart" , cascade = CascadeType.MERGE ,  orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();

    public BigDecimal getTotalPrice(){
        BigDecimal total = BigDecimal.ZERO;

        for(CartItem cartItem : cartItems){
            total = total.add(cartItem.getTotalPrice());
        }
        return total;
    }
}

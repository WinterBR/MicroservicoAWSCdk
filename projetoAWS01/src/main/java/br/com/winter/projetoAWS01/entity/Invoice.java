package br.com.winter.projetoAWS01.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(length = 32, nullable = false, unique = true)
    private String invoiceNumber;

    @Column(length = 32, nullable = false)
    private String customerName;

    private float totalValue;

    private long productId;

    private int quantity;
}

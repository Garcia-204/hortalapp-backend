package com.hortalapp.hortalapp_backen.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "venta_items")
@Data
public class VentaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    @JsonBackReference
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal cantidad;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal precioUsd;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal subtotalUsd;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal subtotalCop;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal subtotalBs;
}
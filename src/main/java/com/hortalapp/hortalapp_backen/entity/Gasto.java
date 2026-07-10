package com.hortalapp.hortalapp_backen.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gastos")
@Data
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "jornada_id", nullable = false)
    private Jornada jornada;

    @Column(nullable = false)
    private String tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Jornada.Moneda moneda;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal valor;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal valorCop;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal valorUsd;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal valorBs;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal tasaUsdCopUsada;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal tasaBsCopUsada;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @PrePersist
    public void prePersist() {
        this.fechaHora = LocalDateTime.now();
    }
}
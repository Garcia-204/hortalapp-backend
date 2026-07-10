package com.hortalapp.hortalapp_backen.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "jornada_id", nullable = false)
    private Jornada jornada;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUnidad tipoUnidad;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal cantidadInicial;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal cantidadActual;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal precioValor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Jornada.Moneda precioMoneda;

    public enum TipoUnidad {
        UNITARIO,
        KG
    }
}
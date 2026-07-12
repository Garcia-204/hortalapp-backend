package com.hortalapp.hortalapp_backen.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "jornadas")
@Data
public class Jornada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Moneda monedaBase;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal tasaUsdCop;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal tasaUsdBs;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column
    private LocalDateTime fechaCierre;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = Estado.ABIERTA;
    }

    public enum Estado {
        ABIERTA,
        CERRADA
    }

    public enum Moneda {
        COP, USD, BS
    }
}
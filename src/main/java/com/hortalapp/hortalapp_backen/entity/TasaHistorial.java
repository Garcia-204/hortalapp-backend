package com.hortalapp.hortalapp_backen.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasas_historial")
@Data
public class TasaHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "jornada_id", nullable = false)
    private Jornada jornada;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal tasaUsdCop;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal tasaBsCop;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @PrePersist
    public void prePersist() {
        this.fechaHora = LocalDateTime.now();
    }
}
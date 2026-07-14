package com.hortalapp.hortalapp_backen.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ventas")
@Data
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "jornada_id", nullable = false)
    private Jornada jornada;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<VentaItem> items;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal totalUsd;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal totalCop;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal totalBs;

    @Column(nullable = false)
    private Boolean pagoMixto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Jornada.Moneda pagoMoneda1;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal pagoCantidad1;

    @Enumerated(EnumType.STRING)
    @Column
    private Jornada.Moneda pagoMoneda2;

    @Column(precision = 20, scale = 4)
    private BigDecimal pagoCantidad2;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal pagoTotalCop;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal vueltoCop;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal vueltoUsd;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal vueltoBs;

    @Column(nullable = false)
    private Boolean vueltoMixto;

    @Enumerated(EnumType.STRING)
    @Column
    private Jornada.Moneda vueltoMoneda1;

    @Column(precision = 20, scale = 4)
    private BigDecimal vueltoCantidad1;

    @Enumerated(EnumType.STRING)
    @Column
    private Jornada.Moneda vueltoMoneda2;

    @Column(precision = 20, scale = 4)
    private BigDecimal vueltoCantidad2;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal tasaUsdCopUsada;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal tasaUsdBsUsada;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @PrePersist
    public void prePersist() {
        this.fechaHora = LocalDateTime.now();
    }
}
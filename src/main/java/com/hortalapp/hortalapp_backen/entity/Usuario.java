package com.hortalapp.hortalapp_backen.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private Boolean activo;

    @Column
    private String nombreFeria;

    @Column(nullable = false)
    private Boolean feriaConfigurada = false;

    @Column
    private LocalDateTime fechaVencimiento;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
        this.feriaConfigurada = false;
    }

    public enum Rol {
        ROLE_ADMIN,
        ROLE_USER
    }
}
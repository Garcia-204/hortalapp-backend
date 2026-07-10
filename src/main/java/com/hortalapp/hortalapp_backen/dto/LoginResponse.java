package com.hortalapp.hortalapp_backen.dto;


import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String nombre;
    private String nombreFeria;
    private Boolean feriaConfigurada;
    private String rol;
}
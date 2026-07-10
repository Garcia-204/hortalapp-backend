package com.hortalapp.hortalapp_backen.dto;


import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoRequest {
    private String nombre;
    private String tipoUnidad;
    private BigDecimal cantidad;
    private BigDecimal precioValor;
    private String precioMoneda;
}
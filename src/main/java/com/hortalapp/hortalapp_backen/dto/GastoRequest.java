package com.hortalapp.hortalapp_backen.dto;


import lombok.Data;
import java.math.BigDecimal;

@Data
public class GastoRequest {
    private String tipo;
    private String moneda;
    private BigDecimal valor;
}
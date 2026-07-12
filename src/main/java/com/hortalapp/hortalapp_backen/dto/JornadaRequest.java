package com.hortalapp.hortalapp_backen.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class JornadaRequest {
    private String nombre;
    private String monedaBase;
    private BigDecimal tasaUsdCop;
    private BigDecimal tasaUsdBs;
}
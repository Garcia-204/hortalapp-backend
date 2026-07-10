package com.hortalapp.hortalapp_backen.dto;


import lombok.Data;
import java.math.BigDecimal;

@Data
public class TasaRequest {
    private BigDecimal tasaUsdCop;
    private BigDecimal tasaBsCop;
}
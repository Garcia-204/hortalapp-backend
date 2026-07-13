package com.hortalapp.hortalapp_backen.dto;


import lombok.Data;
import java.math.BigDecimal;

@Data
public class CarritoItemDTO {
    private Long productoId;
    private BigDecimal cantidad;
}
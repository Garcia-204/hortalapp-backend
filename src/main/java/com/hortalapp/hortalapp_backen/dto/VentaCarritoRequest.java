package com.hortalapp.hortalapp_backen.dto;



import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class VentaCarritoRequest {
    private List<CarritoItemDTO> items;
    private Boolean pagoMixto;
    private String pagoMoneda1;
    private BigDecimal pagoCantidad1;
    private String pagoMoneda2;
    private BigDecimal pagoCantidad2;
    private Boolean vueltoMixto;
    private String vueltoMoneda1;
    private BigDecimal vueltoCantidad1;
    private String vueltoMoneda2;
}
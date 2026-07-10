package com.hortalapp.hortalapp_backen.util;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class MonedaUtil {

    private static final int ESCALA = 4;
    private static final RoundingMode REDONDEO = RoundingMode.HALF_UP;

    public static BigDecimal convertirACop(BigDecimal valor, String moneda,
                                           BigDecimal tasaUsdCop, BigDecimal tasaBsCop) {
        return switch (moneda) {
            case "COP" -> valor;
            case "USD" -> valor.multiply(tasaUsdCop).setScale(ESCALA, REDONDEO);
            case "BS"  -> valor.multiply(tasaBsCop).setScale(ESCALA, REDONDEO);
            default -> throw new IllegalArgumentException("Moneda no válida: " + moneda);
        };
    }

    public static BigDecimal convertirDesdeACop(BigDecimal valorCop, String monedaDestino,
                                                BigDecimal tasaUsdCop, BigDecimal tasaBsCop) {
        return switch (monedaDestino) {
            case "COP" -> valorCop;
            case "USD" -> valorCop.divide(tasaUsdCop, ESCALA, REDONDEO);
            case "BS"  -> valorCop.divide(tasaBsCop, ESCALA, REDONDEO);
            default -> throw new IllegalArgumentException("Moneda no válida: " + monedaDestino);
        };
    }

    public static BigDecimal[] convertirATres(BigDecimal valorCop,
                                              BigDecimal tasaUsdCop, BigDecimal tasaBsCop) {
        BigDecimal enUsd = convertirDesdeACop(valorCop, "USD", tasaUsdCop, tasaBsCop);
        BigDecimal enBs  = convertirDesdeACop(valorCop, "BS",  tasaUsdCop, tasaBsCop);
        return new BigDecimal[]{valorCop, enUsd, enBs};
    }
}
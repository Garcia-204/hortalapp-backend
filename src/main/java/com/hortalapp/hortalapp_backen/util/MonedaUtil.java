package com.hortalapp.hortalapp_backen.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MonedaUtil {

    private static final int ESCALA = 4;
    private static final RoundingMode REDONDEO = RoundingMode.HALF_UP;

    // Convierte cualquier moneda a COP
    public static BigDecimal convertirACop(BigDecimal valor, String moneda,
                                           BigDecimal tasaUsdCop,
                                           BigDecimal tasaUsdBs) {
        return switch (moneda) {
            case "COP" -> valor.setScale(ESCALA, REDONDEO);
            case "USD" -> valor.multiply(tasaUsdCop).setScale(ESCALA, REDONDEO);
            case "BS"  -> valor.divide(tasaUsdBs, ESCALA, REDONDEO)
                    .multiply(tasaUsdCop).setScale(ESCALA, REDONDEO);
            default -> throw new IllegalArgumentException("Moneda no válida: " + moneda);
        };
    }

    // Convierte cualquier moneda a USD
    public static BigDecimal convertirAUsd(BigDecimal valor, String moneda,
                                           BigDecimal tasaUsdCop,
                                           BigDecimal tasaUsdBs) {
        return switch (moneda) {
            case "USD" -> valor.setScale(ESCALA, REDONDEO);
            case "COP" -> valor.divide(tasaUsdCop, ESCALA, REDONDEO);
            case "BS"  -> valor.divide(tasaUsdBs, ESCALA, REDONDEO);
            default -> throw new IllegalArgumentException("Moneda no válida: " + moneda);
        };
    }

    // Convierte cualquier moneda a BS
    public static BigDecimal convertirABs(BigDecimal valor, String moneda,
                                          BigDecimal tasaUsdCop,
                                          BigDecimal tasaUsdBs) {
        return switch (moneda) {
            case "BS"  -> valor.setScale(ESCALA, REDONDEO);
            case "USD" -> valor.multiply(tasaUsdBs).setScale(ESCALA, REDONDEO);
            case "COP" -> valor.divide(tasaUsdCop, ESCALA, REDONDEO)
                    .multiply(tasaUsdBs).setScale(ESCALA, REDONDEO);
            default -> throw new IllegalArgumentException("Moneda no válida: " + moneda);
        };
    }

    // Convierte desde COP a cualquier moneda destino
    public static BigDecimal convertirDesdeCop(BigDecimal valorCop, String monedaDestino,
                                               BigDecimal tasaUsdCop,
                                               BigDecimal tasaUsdBs) {
        return switch (monedaDestino) {
            case "COP" -> valorCop.setScale(ESCALA, REDONDEO);
            case "USD" -> valorCop.divide(tasaUsdCop, ESCALA, REDONDEO);
            case "BS"  -> valorCop.divide(tasaUsdCop, ESCALA, REDONDEO)
                    .multiply(tasaUsdBs).setScale(ESCALA, REDONDEO);
            default -> throw new IllegalArgumentException("Moneda no válida: " + monedaDestino);
        };
    }

    // Convierte un valor a las 3 monedas y devuelve [COP, USD, BS]
    public static BigDecimal[] convertirATres(BigDecimal valor, String moneda,
                                              BigDecimal tasaUsdCop,
                                              BigDecimal tasaUsdBs) {
        BigDecimal enCop = convertirACop(valor, moneda, tasaUsdCop, tasaUsdBs);
        BigDecimal enUsd = convertirAUsd(valor, moneda, tasaUsdCop, tasaUsdBs);
        BigDecimal enBs  = convertirABs(valor, moneda, tasaUsdCop, tasaUsdBs);
        return new BigDecimal[]{enCop, enUsd, enBs};
    }
}
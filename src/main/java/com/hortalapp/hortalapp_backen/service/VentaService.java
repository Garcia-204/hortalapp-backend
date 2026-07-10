package com.hortalapp.hortalapp_backen.service;


import com.hortalapp.hortalapp_backen.entity.Jornada;
import com.hortalapp.hortalapp_backen.entity.Producto;
import com.hortalapp.hortalapp_backen.entity.Usuario;
import com.hortalapp.hortalapp_backen.entity.Venta;
import com.hortalapp.hortalapp_backen.repository.VentaRepository;
import com.hortalapp.hortalapp_backen.util.MonedaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoService productoService;
    private final JornadaService jornadaService;

    public Venta registrarVenta(Long jornadaId, Usuario usuario,
                                Long productoId, BigDecimal cantidad,
                                Boolean pagoMixto,
                                String pagoMoneda1, BigDecimal pagoCantidad1,
                                String pagoMoneda2, BigDecimal pagoCantidad2,
                                Boolean vueltoMixto,
                                String vueltoMoneda1, BigDecimal vueltoCantidad1,
                                String vueltoMoneda2) {

        Jornada jornada = jornadaService.obtenerJornadaDeUsuario(jornadaId, usuario);

        if (jornada.getEstado() == Jornada.Estado.CERRADA) {
            throw new RuntimeException("No puedes registrar ventas en una jornada cerrada");
        }

        Producto producto = productoService.obtenerProducto(productoId);
        BigDecimal tasaUsdCop = jornada.getTasaUsdCop();
        BigDecimal tasaBsCop  = jornada.getTasaBsCop();

        // Total de la venta en COP
        BigDecimal precioCop = MonedaUtil.convertirACop(
                producto.getPrecioValor(),
                producto.getPrecioMoneda().name(),
                tasaUsdCop, tasaBsCop);
        BigDecimal totalCop = precioCop.multiply(cantidad)
                .setScale(4, RoundingMode.HALF_UP);
        BigDecimal totalUsd = MonedaUtil.convertirDesdeACop(totalCop, "USD", tasaUsdCop, tasaBsCop);
        BigDecimal totalBs  = MonedaUtil.convertirDesdeACop(totalCop, "BS",  tasaUsdCop, tasaBsCop);

        // Total recibido en COP
        BigDecimal pagoCop1 = MonedaUtil.convertirACop(
                pagoCantidad1, pagoMoneda1, tasaUsdCop, tasaBsCop);
        BigDecimal pagoCop2 = BigDecimal.ZERO;
        if (pagoMixto && pagoMoneda2 != null && pagoCantidad2 != null) {
            pagoCop2 = MonedaUtil.convertirACop(
                    pagoCantidad2, pagoMoneda2, tasaUsdCop, tasaBsCop);
        }
        BigDecimal pagoTotalCop = pagoCop1.add(pagoCop2);

        // Validar que el pago alcanza
        if (pagoTotalCop.compareTo(totalCop) < 0) {
            throw new RuntimeException("El pago recibido no cubre el total de la venta");
        }

        // Calcular vuelto
        BigDecimal vueltoTotalCop = pagoTotalCop.subtract(totalCop);
        BigDecimal vueltoTotalUsd = MonedaUtil.convertirDesdeACop(vueltoTotalCop, "USD", tasaUsdCop, tasaBsCop);
        BigDecimal vueltoTotalBs  = MonedaUtil.convertirDesdeACop(vueltoTotalCop, "BS",  tasaUsdCop, tasaBsCop);

        // Calcular vuelto mixto
        BigDecimal vueltoCantidad2Calculada = BigDecimal.ZERO;
        if (vueltoMixto && vueltoMoneda1 != null && vueltoCantidad1 != null && vueltoMoneda2 != null) {
            BigDecimal vueltoDado1Cop = MonedaUtil.convertirACop(
                    vueltoCantidad1, vueltoMoneda1, tasaUsdCop, tasaBsCop);
            BigDecimal vueltoRestanteCop = vueltoTotalCop.subtract(vueltoDado1Cop);
            if (vueltoRestanteCop.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("El vuelto en moneda 1 supera el total del vuelto");
            }
            vueltoCantidad2Calculada = MonedaUtil.convertirDesdeACop(
                    vueltoRestanteCop, vueltoMoneda2, tasaUsdCop, tasaBsCop);
        }

        // Descontar inventario
        productoService.descontarInventario(producto, cantidad);

        // Guardar venta
        Venta venta = new Venta();
        venta.setJornada(jornada);
        venta.setProducto(producto);
        venta.setCantidad(cantidad);
        venta.setTotalCop(totalCop);
        venta.setTotalUsd(totalUsd);
        venta.setTotalBs(totalBs);
        venta.setTotalEnMonedaBase(MonedaUtil.convertirDesdeACop(
                totalCop, jornada.getMonedaBase().name(), tasaUsdCop, tasaBsCop));
        venta.setPagoMixto(pagoMixto);
        venta.setPagoMoneda1(Jornada.Moneda.valueOf(pagoMoneda1));
        venta.setPagoCantidad1(pagoCantidad1);
        venta.setPagoMoneda2(pagoMoneda2 != null ? Jornada.Moneda.valueOf(pagoMoneda2) : null);
        venta.setPagoCantidad2(pagoCantidad2);
        venta.setPagoTotalCop(pagoTotalCop);
        venta.setVueltoCop(vueltoTotalCop);
        venta.setVueltoUsd(vueltoTotalUsd);
        venta.setVueltoBs(vueltoTotalBs);
        venta.setVueltoMixto(vueltoMixto);
        venta.setVueltoMoneda1(vueltoMoneda1 != null ? Jornada.Moneda.valueOf(vueltoMoneda1) : null);
        venta.setVueltoCantidad1(vueltoCantidad1);
        venta.setVueltoMoneda2(vueltoMoneda2 != null ? Jornada.Moneda.valueOf(vueltoMoneda2) : null);
        venta.setVueltoCantidad2(vueltoCantidad2Calculada);
        venta.setTasaUsdCopUsada(tasaUsdCop);
        venta.setTasaBsCopUsada(tasaBsCop);
        return ventaRepository.save(venta);
    }

    public List<Venta> listarVentas(Long jornadaId, Usuario usuario) {
        Jornada jornada = jornadaService.obtenerJornadaDeUsuario(jornadaId, usuario);
        return ventaRepository.findByJornadaOrderByFechaHoraDesc(jornada);
    }
}
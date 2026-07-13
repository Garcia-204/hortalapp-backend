package com.hortalapp.hortalapp_backen.service;


import com.hortalapp.hortalapp_backen.dto.CarritoItemDTO;
import com.hortalapp.hortalapp_backen.dto.VentaCarritoRequest;
import com.hortalapp.hortalapp_backen.entity.*;
import com.hortalapp.hortalapp_backen.repository.*;
import com.hortalapp.hortalapp_backen.util.MonedaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final VentaItemRepository ventaItemRepository;
    private final ProductoService productoService;
    private final JornadaService jornadaService;

    @Transactional
    public Venta registrarVentaCarrito(Long jornadaId, Usuario usuario,
                                       VentaCarritoRequest request) {

        Jornada jornada = jornadaService.obtenerJornadaDeUsuario(jornadaId, usuario);

        if (jornada.getEstado() == Jornada.Estado.CERRADA) {
            throw new RuntimeException("No puedes registrar ventas en una jornada cerrada");
        }

        BigDecimal tasaUsdCop = jornada.getTasaUsdCop();
        BigDecimal tasaUsdBs  = jornada.getTasaUsdBs();

        // Calcular totales del carrito
        BigDecimal totalUsd = BigDecimal.ZERO;
        List<Producto> productosADescontar = new ArrayList<>();
        List<CarritoItemDTO> items = request.getItems();

        for (CarritoItemDTO item : items) {
            Producto producto = productoService.obtenerProducto(item.getProductoId());
            BigDecimal precioUsd = MonedaUtil.convertirAUsd(
                    producto.getPrecioValor(),
                    producto.getPrecioMoneda().name(),
                    tasaUsdCop, tasaUsdBs);
            totalUsd = totalUsd.add(precioUsd.multiply(item.getCantidad()));
            productosADescontar.add(producto);
        }

        BigDecimal totalCop = MonedaUtil.convertirACop(totalUsd, "USD", tasaUsdCop, tasaUsdBs);
        BigDecimal totalBs  = MonedaUtil.convertirABs(totalUsd, "USD", tasaUsdCop, tasaUsdBs);

        // Calcular pago total en COP
        BigDecimal pagoCop1 = MonedaUtil.convertirACop(
                request.getPagoCantidad1(), request.getPagoMoneda1(), tasaUsdCop, tasaUsdBs);
        BigDecimal pagoCop2 = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(request.getPagoMixto()) &&
                request.getPagoMoneda2() != null && request.getPagoCantidad2() != null) {
            pagoCop2 = MonedaUtil.convertirACop(
                    request.getPagoCantidad2(), request.getPagoMoneda2(), tasaUsdCop, tasaUsdBs);
        }
        BigDecimal pagoTotalCop = pagoCop1.add(pagoCop2);

        if (pagoTotalCop.compareTo(totalCop) < 0) {
            throw new RuntimeException("El pago recibido no cubre el total de la venta");
        }

        // Calcular vuelto
        BigDecimal vueltoTotalCop = pagoTotalCop.subtract(totalCop);
        BigDecimal vueltoTotalUsd = MonedaUtil.convertirDesdeCop(vueltoTotalCop, "USD", tasaUsdCop, tasaUsdBs);
        BigDecimal vueltoTotalBs  = MonedaUtil.convertirDesdeCop(vueltoTotalCop, "BS",  tasaUsdCop, tasaUsdBs);

        // Calcular vuelto mixto
        BigDecimal vueltoCantidad2Calculada = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(request.getVueltoMixto()) &&
                request.getVueltoMoneda1() != null &&
                request.getVueltoCantidad1() != null &&
                request.getVueltoMoneda2() != null) {
            BigDecimal vueltoDado1Cop = MonedaUtil.convertirACop(
                    request.getVueltoCantidad1(), request.getVueltoMoneda1(), tasaUsdCop, tasaUsdBs);
            BigDecimal vueltoRestanteCop = vueltoTotalCop.subtract(vueltoDado1Cop);
            if (vueltoRestanteCop.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("El vuelto en moneda 1 supera el total del vuelto");
            }
            vueltoCantidad2Calculada = MonedaUtil.convertirDesdeCop(
                    vueltoRestanteCop, request.getVueltoMoneda2(), tasaUsdCop, tasaUsdBs);
        }

        // Guardar la venta cabecera
        Venta venta = new Venta();
        venta.setJornada(jornada);
        venta.setTotalUsd(totalUsd.setScale(4, RoundingMode.HALF_UP));
        venta.setTotalCop(totalCop.setScale(4, RoundingMode.HALF_UP));
        venta.setTotalBs(totalBs.setScale(4, RoundingMode.HALF_UP));
        venta.setPagoMixto(request.getPagoMixto());
        venta.setPagoMoneda1(Jornada.Moneda.valueOf(request.getPagoMoneda1()));
        venta.setPagoCantidad1(request.getPagoCantidad1());
        venta.setPagoMoneda2(request.getPagoMoneda2() != null ?
                Jornada.Moneda.valueOf(request.getPagoMoneda2()) : null);
        venta.setPagoCantidad2(request.getPagoCantidad2());
        venta.setPagoTotalCop(pagoTotalCop);
        venta.setVueltoCop(vueltoTotalCop);
        venta.setVueltoUsd(vueltoTotalUsd);
        venta.setVueltoBs(vueltoTotalBs);
        venta.setVueltoMixto(request.getVueltoMixto());
        venta.setVueltoMoneda1(request.getVueltoMoneda1() != null ?
                Jornada.Moneda.valueOf(request.getVueltoMoneda1()) : null);
        venta.setVueltoCantidad1(request.getVueltoCantidad1());
        venta.setVueltoMoneda2(request.getVueltoMoneda2() != null ?
                Jornada.Moneda.valueOf(request.getVueltoMoneda2()) : null);
        venta.setVueltoCantidad2(vueltoCantidad2Calculada);
        venta.setTasaUsdCopUsada(tasaUsdCop);
        venta.setTasaUsdBsUsada(tasaUsdBs);
        venta = ventaRepository.save(venta);

        // Guardar items y descontar inventario
        for (CarritoItemDTO item : items) {
            Producto producto = productoService.obtenerProducto(item.getProductoId());
            BigDecimal precioUsd = MonedaUtil.convertirAUsd(
                    producto.getPrecioValor(),
                    producto.getPrecioMoneda().name(),
                    tasaUsdCop, tasaUsdBs);
            BigDecimal subtotalUsd = precioUsd.multiply(item.getCantidad()).setScale(4, RoundingMode.HALF_UP);
            BigDecimal subtotalCop = MonedaUtil.convertirACop(subtotalUsd, "USD", tasaUsdCop, tasaUsdBs);
            BigDecimal subtotalBs  = MonedaUtil.convertirABs(subtotalUsd, "USD", tasaUsdCop, tasaUsdBs);

            VentaItem ventaItem = new VentaItem();
            ventaItem.setVenta(venta);
            ventaItem.setProducto(producto);
            ventaItem.setCantidad(item.getCantidad());
            ventaItem.setPrecioUsd(precioUsd);
            ventaItem.setSubtotalUsd(subtotalUsd);
            ventaItem.setSubtotalCop(subtotalCop);
            ventaItem.setSubtotalBs(subtotalBs);
            ventaItemRepository.save(ventaItem);

            productoService.descontarInventario(producto, item.getCantidad());
        }

        return venta;
    }

    public List<Venta> listarVentas(Long jornadaId, Usuario usuario) {
        Jornada jornada = jornadaService.obtenerJornadaDeUsuario(jornadaId, usuario);
        return ventaRepository.findByJornadaOrderByFechaHoraDesc(jornada);
    }
}
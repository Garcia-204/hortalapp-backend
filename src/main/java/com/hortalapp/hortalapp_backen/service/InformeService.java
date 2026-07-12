package com.hortalapp.hortalapp_backen.service;


import com.hortalapp.hortalapp_backen.entity.Gasto;
import com.hortalapp.hortalapp_backen.entity.Jornada;
import com.hortalapp.hortalapp_backen.entity.Producto;
import com.hortalapp.hortalapp_backen.entity.Usuario;
import com.hortalapp.hortalapp_backen.entity.Venta;
import com.hortalapp.hortalapp_backen.repository.GastoRepository;
import com.hortalapp.hortalapp_backen.repository.ProductoRepository;
import com.hortalapp.hortalapp_backen.repository.VentaRepository;
import com.hortalapp.hortalapp_backen.util.MonedaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InformeService {

    private final VentaRepository ventaRepository;
    private final GastoRepository gastoRepository;
    private final ProductoRepository productoRepository;
    private final JornadaService jornadaService;

    public InformeDTO generarInforme(Long jornadaId, Usuario usuario) {
        Jornada jornada = jornadaService.obtenerJornadaDeUsuario(jornadaId, usuario);
        BigDecimal tasaUsdCop = jornada.getTasaUsdCop();
        BigDecimal tasaUsdBs  = jornada.getTasaUsdBs();

        List<Venta> ventas   = ventaRepository.findByJornadaOrderByFechaHoraDesc(jornada);
        List<Gasto> gastos   = gastoRepository.findByJornadaOrderByFechaHoraDesc(jornada);
        List<Producto> prods = productoRepository.findByJornada(jornada);

        BigDecimal ingresosCop = BigDecimal.ZERO;
        BigDecimal ingresosUsd = BigDecimal.ZERO;
        BigDecimal ingresosBs  = BigDecimal.ZERO;

        for (Venta v : ventas) {
            ingresosCop = ingresosCop.add(v.getTotalCop());
            ingresosUsd = ingresosUsd.add(v.getTotalUsd());
            ingresosBs  = ingresosBs.add(v.getTotalBs());
        }

        BigDecimal gastosCop = BigDecimal.ZERO;
        BigDecimal gastosUsd = BigDecimal.ZERO;
        BigDecimal gastosBs  = BigDecimal.ZERO;

        for (Gasto g : gastos) {
            gastosCop = gastosCop.add(g.getValorCop());
            gastosUsd = gastosUsd.add(g.getValorUsd());
            gastosBs  = gastosBs.add(g.getValorBs());
        }

        BigDecimal gananciaCop = ingresosCop.subtract(gastosCop).setScale(4, RoundingMode.HALF_UP);
        BigDecimal gananciaUsd = ingresosUsd.subtract(gastosUsd).setScale(4, RoundingMode.HALF_UP);
        BigDecimal gananciaBs  = ingresosBs.subtract(gastosBs).setScale(4, RoundingMode.HALF_UP);

        BigDecimal cajaCop = BigDecimal.ZERO;
        BigDecimal cajaUsd = BigDecimal.ZERO;
        BigDecimal cajaBs  = BigDecimal.ZERO;

        for (Venta v : ventas) {
            if (v.getPagoMoneda1().name().equals("COP")) cajaCop = cajaCop.add(v.getPagoCantidad1());
            if (v.getPagoMoneda1().name().equals("USD")) cajaUsd = cajaUsd.add(v.getPagoCantidad1());
            if (v.getPagoMoneda1().name().equals("BS"))  cajaBs  = cajaBs.add(v.getPagoCantidad1());
            if (v.getPagoMixto() && v.getPagoMoneda2() != null) {
                if (v.getPagoMoneda2().name().equals("COP")) cajaCop = cajaCop.add(v.getPagoCantidad2());
                if (v.getPagoMoneda2().name().equals("USD")) cajaUsd = cajaUsd.add(v.getPagoCantidad2());
                if (v.getPagoMoneda2().name().equals("BS"))  cajaBs  = cajaBs.add(v.getPagoCantidad2());
            }
        }

        return new InformeDTO(
                jornada,
                ventas.size(),
                ingresosCop, ingresosUsd, ingresosBs,
                gastos.size(),
                gastosCop, gastosUsd, gastosBs,
                gananciaCop, gananciaUsd, gananciaBs,
                cajaCop, cajaUsd, cajaBs,
                prods
        );
    }

    public record InformeDTO(
            Jornada jornada,
            int totalVentas,
            BigDecimal ingresosCop, BigDecimal ingresosUsd, BigDecimal ingresosBs,
            int totalGastos,
            BigDecimal gastosCop, BigDecimal gastosUsd, BigDecimal gastosBs,
            BigDecimal gananciaCop, BigDecimal gananciaUsd, BigDecimal gananciaBs,
            BigDecimal cajaCop, BigDecimal cajaUsd, BigDecimal cajaBs,
            List<Producto> inventarioRestante
    ) {}
}
package com.hortalapp.hortalapp_backen.service;

import com.hortalapp.hortalapp_backen.entity.Gasto;
import com.hortalapp.hortalapp_backen.entity.Jornada;
import com.hortalapp.hortalapp_backen.entity.Usuario;
import com.hortalapp.hortalapp_backen.repository.GastoRepository;
import com.hortalapp.hortalapp_backen.repository.VentaRepository;
import com.hortalapp.hortalapp_backen.util.MonedaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GastoService {

    private final GastoRepository gastoRepository;
    private final VentaRepository ventaRepository;
    private final JornadaService jornadaService;

    public Gasto registrarGasto(Long jornadaId, Usuario usuario, String tipo,
                                Jornada.Moneda moneda, BigDecimal valor) {
        Jornada jornada = jornadaService.obtenerJornadaDeUsuario(jornadaId, usuario);

        if (jornada.getEstado() == Jornada.Estado.CERRADA) {
            throw new RuntimeException("No puedes registrar gastos en una jornada cerrada");
        }

        BigDecimal tasaUsdCop = jornada.getTasaUsdCop();
        BigDecimal tasaUsdBs  = jornada.getTasaUsdBs();

        BigDecimal valorCop = MonedaUtil.convertirACop(valor, moneda.name(), tasaUsdCop, tasaUsdBs);
        BigDecimal valorUsd = MonedaUtil.convertirAUsd(valor, moneda.name(), tasaUsdCop, tasaUsdBs);
        BigDecimal valorBs  = MonedaUtil.convertirABs(valor, moneda.name(), tasaUsdCop, tasaUsdBs);

        BigDecimal totalRecibidoCop = ventaRepository.sumPagoTotalCopByJornadaId(jornadaId);
        BigDecimal totalGastadoCop  = gastoRepository.sumValorCopByJornadaId(jornadaId);
        BigDecimal saldoDisponible  = totalRecibidoCop.subtract(totalGastadoCop);

        if (valorCop.compareTo(saldoDisponible) > 0) {
            BigDecimal disponibleEnMoneda = MonedaUtil.convertirDesdeCop(
                    saldoDisponible, moneda.name(), tasaUsdCop, tasaUsdBs);
            throw new RuntimeException(
                    "Saldo insuficiente. Disponible: " +
                            disponibleEnMoneda + " " + moneda.name());
        }

        Gasto gasto = new Gasto();
        gasto.setJornada(jornada);
        gasto.setTipo(tipo);
        gasto.setMoneda(moneda);
        gasto.setValor(valor);
        gasto.setValorCop(valorCop);
        gasto.setValorUsd(valorUsd);
        gasto.setValorBs(valorBs);
        gasto.setTasaUsdCopUsada(tasaUsdCop);
        gasto.setTasaUsdBsUsada(tasaUsdBs);
        return gastoRepository.save(gasto);
    }

    public void eliminarGasto(Long gastoId, Usuario usuario) {
        Gasto gasto = gastoRepository.findById(gastoId)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));
        jornadaService.obtenerJornadaDeUsuario(gasto.getJornada().getId(), usuario);
        gastoRepository.delete(gasto);
    }

    public List<Gasto> listarGastos(Long jornadaId, Usuario usuario) {
        Jornada jornada = jornadaService.obtenerJornadaDeUsuario(jornadaId, usuario);
        return gastoRepository.findByJornadaOrderByFechaHoraDesc(jornada);
    }

    public BigDecimal saldoDisponibleCop(Long jornadaId) {
        BigDecimal totalRecibido = ventaRepository.sumPagoTotalCopByJornadaId(jornadaId);
        BigDecimal totalGastado  = gastoRepository.sumValorCopByJornadaId(jornadaId);
        return totalRecibido.subtract(totalGastado);
    }
}
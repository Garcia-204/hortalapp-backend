package com.hortalapp.hortalapp_backen.service;


import com.hortalapp.hortalapp_backen.entity.Jornada;
import com.hortalapp.hortalapp_backen.entity.TasaHistorial;
import com.hortalapp.hortalapp_backen.entity.Usuario;
import com.hortalapp.hortalapp_backen.repository.JornadaRepository;
import com.hortalapp.hortalapp_backen.repository.TasaHistorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JornadaService {

    private final JornadaRepository jornadaRepository;
    private final TasaHistorialRepository tasaHistorialRepository;

    public Jornada crearJornada(Usuario usuario, String nombre,
                                Jornada.Moneda monedaBase,
                                BigDecimal tasaUsdCop,
                                BigDecimal tasaBsCop) {
        Jornada jornada = new Jornada();
        jornada.setUsuario(usuario);
        jornada.setNombre(nombre);
        jornada.setMonedaBase(monedaBase);
        jornada.setTasaUsdCop(tasaUsdCop);
        jornada.setTasaBsCop(tasaBsCop);
        jornada = jornadaRepository.save(jornada);

        TasaHistorial tasa = new TasaHistorial();
        tasa.setJornada(jornada);
        tasa.setTasaUsdCop(tasaUsdCop);
        tasa.setTasaBsCop(tasaBsCop);
        tasaHistorialRepository.save(tasa);

        return jornada;
    }

    public Jornada actualizarTasas(Long jornadaId, Usuario usuario,
                                   BigDecimal tasaUsdCop, BigDecimal tasaBsCop) {
        Jornada jornada = obtenerJornadaDeUsuario(jornadaId, usuario);
        jornada.setTasaUsdCop(tasaUsdCop);
        jornada.setTasaBsCop(tasaBsCop);
        jornada = jornadaRepository.save(jornada);

        TasaHistorial tasa = new TasaHistorial();
        tasa.setJornada(jornada);
        tasa.setTasaUsdCop(tasaUsdCop);
        tasa.setTasaBsCop(tasaBsCop);
        tasaHistorialRepository.save(tasa);

        return jornada;
    }

    public Jornada cerrarJornada(Long jornadaId, Usuario usuario) {
        Jornada jornada = obtenerJornadaDeUsuario(jornadaId, usuario);
        if (jornada.getEstado() == Jornada.Estado.CERRADA) {
            throw new RuntimeException("La jornada ya está cerrada");
        }
        jornada.setEstado(Jornada.Estado.CERRADA);
        jornada.setFechaCierre(LocalDateTime.now());
        return jornadaRepository.save(jornada);
    }

    public List<Jornada> listarJornadas(Usuario usuario) {
        return jornadaRepository.findByUsuarioOrderByFechaCreacionDesc(usuario);
    }

    public Jornada obtenerJornadaDeUsuario(Long jornadaId, Usuario usuario) {
        return jornadaRepository.findByIdAndUsuario(jornadaId, usuario)
                .orElseThrow(() -> new RuntimeException("Jornada no encontrada"));
    }
}
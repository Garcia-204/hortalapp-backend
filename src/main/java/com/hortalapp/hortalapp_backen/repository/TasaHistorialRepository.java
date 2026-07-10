package com.hortalapp.hortalapp_backen.repository;

import com.hortalapp.hortalapp_backen.entity.TasaHistorial;
import com.hortalapp.hortalapp_backen.entity.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TasaHistorialRepository extends JpaRepository<TasaHistorial, Long> {
    List<TasaHistorial> findByJornadaOrderByFechaHoraDesc(Jornada jornada);
    Optional<TasaHistorial> findFirstByJornadaOrderByFechaHoraDesc(Jornada jornada);
}
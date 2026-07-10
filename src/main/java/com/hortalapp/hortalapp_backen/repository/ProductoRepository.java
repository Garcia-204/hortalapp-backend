package com.hortalapp.hortalapp_backen.repository;

import com.hortalapp.hortalapp_backen.entity.Producto;
import com.hortalapp.hortalapp_backen.entity.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByJornada(Jornada jornada);
    List<Producto> findByJornadaId(Long jornadaId);
}
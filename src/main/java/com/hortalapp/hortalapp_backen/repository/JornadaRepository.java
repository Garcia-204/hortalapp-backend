package com.hortalapp.hortalapp_backen.repository;

import com.hortalapp.hortalapp_backen.entity.Jornada;
import com.hortalapp.hortalapp_backen.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JornadaRepository extends JpaRepository<Jornada, Long> {
    List<Jornada> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);
    Optional<Jornada> findByIdAndUsuario(Long id, Usuario usuario);
    List<Jornada> findByUsuarioAndEstado(Usuario usuario, Jornada.Estado estado);
}
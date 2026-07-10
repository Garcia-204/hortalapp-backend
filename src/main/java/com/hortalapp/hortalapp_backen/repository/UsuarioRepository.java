package com.hortalapp.hortalapp_backen.repository;

import com.hortalapp.hortalapp_backen.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    Boolean existsByCorreo(String correo);
}
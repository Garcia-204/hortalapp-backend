package com.hortalapp.hortalapp_backen.controller;


import com.hortalapp.hortalapp_backen.entity.Usuario;
import com.hortalapp.hortalapp_backen.repository.UsuarioRepository;
import com.hortalapp.hortalapp_backen.service.InformeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jornadas/{jornadaId}/informe")
@RequiredArgsConstructor
public class InformeController {

    private final InformeService informeService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<?> obtener(@PathVariable Long jornadaId,
                                     Authentication auth) {
        Usuario usuario = usuarioRepository
                .findByCorreo(auth.getName()).orElseThrow();
        return ResponseEntity.ok(
                informeService.generarInforme(jornadaId, usuario));
    }
}
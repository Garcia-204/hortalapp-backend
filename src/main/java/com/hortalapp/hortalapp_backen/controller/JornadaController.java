package com.hortalapp.hortalapp_backen.controller;


import com.hortalapp.hortalapp_backen.dto.JornadaRequest;
import com.hortalapp.hortalapp_backen.dto.TasaRequest;
import com.hortalapp.hortalapp_backen.entity.Jornada;
import com.hortalapp.hortalapp_backen.entity.Usuario;
import com.hortalapp.hortalapp_backen.repository.UsuarioRepository;
import com.hortalapp.hortalapp_backen.service.JornadaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jornadas")
@RequiredArgsConstructor
public class JornadaController {

    private final JornadaService jornadaService;
    private final UsuarioRepository usuarioRepository;

    private Usuario getUsuario(Authentication auth) {
        return usuarioRepository.findByCorreo(auth.getName()).orElseThrow();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody JornadaRequest request,
                                   Authentication auth) {
        Jornada jornada = jornadaService.crearJornada(
                getUsuario(auth),
                request.getNombre(),
                Jornada.Moneda.valueOf(request.getMonedaBase()),
                request.getTasaUsdCop(),
                request.getTasaBsCop()
        );
        return ResponseEntity.ok(jornada);
    }

    @GetMapping
    public ResponseEntity<List<Jornada>> listar(Authentication auth) {
        return ResponseEntity.ok(jornadaService.listarJornadas(getUsuario(auth)));
    }

    @PutMapping("/{id}/tasas")
    public ResponseEntity<?> actualizarTasas(@PathVariable Long id,
                                             @RequestBody TasaRequest request,
                                             Authentication auth) {
        return ResponseEntity.ok(jornadaService.actualizarTasas(
                id, getUsuario(auth),
                request.getTasaUsdCop(),
                request.getTasaBsCop()
        ));
    }

    @PutMapping("/{id}/cerrar")
    public ResponseEntity<?> cerrar(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(jornadaService.cerrarJornada(id, getUsuario(auth)));
    }
}
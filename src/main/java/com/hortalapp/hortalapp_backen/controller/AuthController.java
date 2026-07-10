package com.hortalapp.hortalapp_backen.controller;


import com.hortalapp.hortalapp_backen.dto.LoginRequest;
import com.hortalapp.hortalapp_backen.dto.LoginResponse;
import com.hortalapp.hortalapp_backen.dto.FeriaRequest;
import com.hortalapp.hortalapp_backen.entity.Usuario;
import com.hortalapp.hortalapp_backen.repository.UsuarioRepository;
import com.hortalapp.hortalapp_backen.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getCorreo(), request.getPassword()));

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow();

        if (!usuario.getActivo()) {
            return ResponseEntity.status(403).body("Cuenta desactivada");
        }

        if (usuario.getFechaVencimiento() != null &&
                usuario.getFechaVencimiento().isBefore(java.time.LocalDateTime.now())) {
            return ResponseEntity.status(403).body("Suscripción vencida");
        }

        String token = jwtUtil.generarToken(usuario.getCorreo());
        return ResponseEntity.ok(new LoginResponse(
                token,
                usuario.getNombre(),
                usuario.getNombreFeria(),
                usuario.getFeriaConfigurada(),
                usuario.getRol().name()
        ));
    }

    @PutMapping("/feria")
    public ResponseEntity<?> configurarFeria(@RequestBody FeriaRequest request,
                                             Authentication auth) {
        Usuario usuario = usuarioRepository.findByCorreo(auth.getName()).orElseThrow();
        usuario.setNombreFeria(request.getNombreFeria());
        usuario.setFeriaConfigurada(true);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Feria configurada correctamente");
    }
}
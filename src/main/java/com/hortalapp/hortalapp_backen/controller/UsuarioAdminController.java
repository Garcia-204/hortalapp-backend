package com.hortalapp.hortalapp_backen.controller;



import com.hortalapp.hortalapp_backen.entity.Usuario;
import com.hortalapp.hortalapp_backen.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
public class UsuarioAdminController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Usuario request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            return ResponseEntity.badRequest().body("El correo ya existe");
        }
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        request.setRol(Usuario.Rol.ROLE_USER);
        return ResponseEntity.ok(usuarioRepository.save(request));
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<?> activar(@PathVariable Long id) {
        Usuario u = usuarioRepository.findById(id).orElseThrow();
        u.setActivo(true);
        return ResponseEntity.ok(usuarioRepository.save(u));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivar(@PathVariable Long id) {
        Usuario u = usuarioRepository.findById(id).orElseThrow();
        u.setActivo(false);
        return ResponseEntity.ok(usuarioRepository.save(u));
    }

    @PutMapping("/{id}/renovar")
    public ResponseEntity<?> renovar(@PathVariable Long id,
                                     @RequestParam int dias) {
        Usuario u = usuarioRepository.findById(id).orElseThrow();
        LocalDateTime base = u.getFechaVencimiento() != null &&
                u.getFechaVencimiento().isAfter(LocalDateTime.now())
                ? u.getFechaVencimiento()
                : LocalDateTime.now();
        u.setFechaVencimiento(base.plusDays(dias));
        u.setActivo(true);
        return ResponseEntity.ok(usuarioRepository.save(u));
    }
}
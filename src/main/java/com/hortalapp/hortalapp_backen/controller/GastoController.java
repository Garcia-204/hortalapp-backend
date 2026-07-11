package com.hortalapp.hortalapp_backen.controller;


import com.hortalapp.hortalapp_backen.dto.GastoRequest;
import com.hortalapp.hortalapp_backen.entity.Jornada;
import com.hortalapp.hortalapp_backen.entity.Usuario;
import com.hortalapp.hortalapp_backen.repository.UsuarioRepository;
import com.hortalapp.hortalapp_backen.service.GastoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jornadas/{jornadaId}/gastos")
@RequiredArgsConstructor
public class GastoController {

    private final GastoService gastoService;
    private final UsuarioRepository usuarioRepository;

    private Usuario getUsuario(Authentication auth) {
        return usuarioRepository.findByCorreo(auth.getName()).orElseThrow();
    }

    @PostMapping
    public ResponseEntity<?> registrar(@PathVariable Long jornadaId,
                                       @RequestBody GastoRequest request,
                                       Authentication auth) {
        return ResponseEntity.ok(gastoService.registrarGasto(
                jornadaId, getUsuario(auth),
                request.getTipo(),
                Jornada.Moneda.valueOf(request.getMoneda()),
                request.getValor()
        ));
    }

    @GetMapping
    public ResponseEntity<?> listar(@PathVariable Long jornadaId,
                                    Authentication auth) {
        return ResponseEntity.ok(
                gastoService.listarGastos(jornadaId, getUsuario(auth)));
    }

    @DeleteMapping("/{gastoId}")
    public ResponseEntity<?> eliminar(@PathVariable Long jornadaId,
                                      @PathVariable Long gastoId,
                                      Authentication auth) {
        gastoService.eliminarGasto(gastoId, getUsuario(auth));
        return ResponseEntity.ok("Gasto eliminado");
    }

    @GetMapping("/saldo")
    public ResponseEntity<?> saldo(@PathVariable Long jornadaId,
                                   Authentication auth) {
        getUsuario(auth);
        return ResponseEntity.ok(gastoService.saldoDisponibleCop(jornadaId));
    }
}
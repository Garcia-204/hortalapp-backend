package com.hortalapp.hortalapp_backen.controller;



import com.hortalapp.hortalapp_backen.dto.VentaRequest;
import com.hortalapp.hortalapp_backen.entity.Usuario;
import com.hortalapp.hortalapp_backen.repository.UsuarioRepository;
import com.hortalapp.hortalapp_backen.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jornadas/{jornadaId}/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;
    private final UsuarioRepository usuarioRepository;

    private Usuario getUsuario(Authentication auth) {
        return usuarioRepository.findByCorreo(auth.getName()).orElseThrow();
    }

    @PostMapping
    public ResponseEntity<?> registrar(@PathVariable Long jornadaId,
                                       @RequestBody VentaRequest r,
                                       Authentication auth) {
        return ResponseEntity.ok(ventaService.registrarVenta(
                jornadaId, getUsuario(auth),
                r.getProductoId(), r.getCantidad(),
                r.getPagoMixto(),
                r.getPagoMoneda1(), r.getPagoCantidad1(),
                r.getPagoMoneda2(), r.getPagoCantidad2(),
                r.getVueltoMixto(),
                r.getVueltoMoneda1(), r.getVueltoCantidad1(),
                r.getVueltoMoneda2()
        ));
    }

    @GetMapping
    public ResponseEntity<?> listar(@PathVariable Long jornadaId,
                                    Authentication auth) {
        return ResponseEntity.ok(
                ventaService.listarVentas(jornadaId, getUsuario(auth)));
    }
}
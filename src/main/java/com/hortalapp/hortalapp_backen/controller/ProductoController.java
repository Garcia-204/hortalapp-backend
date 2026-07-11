package com.hortalapp.hortalapp_backen.controller;



import com.hortalapp.hortalapp_backen.dto.ProductoRequest;
import com.hortalapp.hortalapp_backen.entity.Jornada;
import com.hortalapp.hortalapp_backen.entity.Producto;
import com.hortalapp.hortalapp_backen.entity.Usuario;
import com.hortalapp.hortalapp_backen.repository.UsuarioRepository;
import com.hortalapp.hortalapp_backen.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jornadas/{jornadaId}/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final UsuarioRepository usuarioRepository;

    private Usuario getUsuario(Authentication auth) {
        return usuarioRepository.findByCorreo(auth.getName()).orElseThrow();
    }

    @PostMapping
    public ResponseEntity<?> agregar(@PathVariable Long jornadaId,
                                     @RequestBody ProductoRequest request,
                                     Authentication auth) {
        return ResponseEntity.ok(productoService.agregarProducto(
                jornadaId, getUsuario(auth),
                request.getNombre(),
                Producto.TipoUnidad.valueOf(request.getTipoUnidad()),
                request.getCantidad(),
                request.getPrecioValor(),
                Jornada.Moneda.valueOf(request.getPrecioMoneda())
        ));
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listar(@PathVariable Long jornadaId,
                                                 Authentication auth) {
        return ResponseEntity.ok(
                productoService.listarProductos(jornadaId, getUsuario(auth)));
    }

    @DeleteMapping("/{productoId}")
    public ResponseEntity<?> eliminar(@PathVariable Long jornadaId,
                                      @PathVariable Long productoId,
                                      Authentication auth) {
        productoService.eliminarProducto(productoId, getUsuario(auth));
        return ResponseEntity.ok("Producto eliminado");
    }
}
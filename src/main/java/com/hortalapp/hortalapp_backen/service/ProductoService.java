package com.hortalapp.hortalapp_backen.service;


import com.hortalapp.hortalapp_backen.entity.Jornada;
import com.hortalapp.hortalapp_backen.entity.Producto;
import com.hortalapp.hortalapp_backen.entity.Usuario;
import com.hortalapp.hortalapp_backen.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final JornadaService jornadaService;

    public Producto agregarProducto(Long jornadaId, Usuario usuario, String nombre,
                                    Producto.TipoUnidad tipoUnidad,
                                    BigDecimal cantidad, BigDecimal precioValor,
                                    Jornada.Moneda precioMoneda) {
        Jornada jornada = jornadaService.obtenerJornadaDeUsuario(jornadaId, usuario);

        if (jornada.getEstado() == Jornada.Estado.CERRADA) {
            throw new RuntimeException("No puedes agregar productos a una jornada cerrada");
        }

        Producto producto = new Producto();
        producto.setJornada(jornada);
        producto.setNombre(nombre);
        producto.setTipoUnidad(tipoUnidad);
        producto.setCantidadInicial(cantidad);
        producto.setCantidadActual(cantidad);
        producto.setPrecioValor(precioValor);
        producto.setPrecioMoneda(precioMoneda);
        return productoRepository.save(producto);
    }

    public void eliminarProducto(Long productoId, Usuario usuario) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        jornadaService.obtenerJornadaDeUsuario(
                producto.getJornada().getId(), usuario);
        productoRepository.delete(producto);
    }

    public List<Producto> listarProductos(Long jornadaId, Usuario usuario) {
        jornadaService.obtenerJornadaDeUsuario(jornadaId, usuario);
        return productoRepository.findByJornadaId(jornadaId);
    }

    public Producto obtenerProducto(Long productoId) {
        return productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public void descontarInventario(Producto producto, BigDecimal cantidad) {
        BigDecimal nueva = producto.getCantidadActual().subtract(cantidad);
        if (nueva.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("No hay suficiente inventario de " + producto.getNombre());
        }
        producto.setCantidadActual(nueva);
        productoRepository.save(producto);
    }
}
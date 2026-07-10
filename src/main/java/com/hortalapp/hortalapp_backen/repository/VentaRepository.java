package com.hortalapp.hortalapp_backen.repository;

import com.hortalapp.hortalapp_backen.entity.Venta;
import com.hortalapp.hortalapp_backen.entity.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByJornadaOrderByFechaHoraDesc(Jornada jornada);
    List<Venta> findByJornadaId(Long jornadaId);

    @Query("SELECT COALESCE(SUM(v.pagoTotalCop), 0) FROM Venta v WHERE v.jornada.id = :jornadaId")
    BigDecimal sumPagoTotalCopByJornadaId(@Param("jornadaId") Long jornadaId);
}
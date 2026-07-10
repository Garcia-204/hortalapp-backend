package com.hortalapp.hortalapp_backen.repository;

import com.hortalapp.hortalapp_backen.entity.Gasto;
import com.hortalapp.hortalapp_backen.entity.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {
    List<Gasto> findByJornadaOrderByFechaHoraDesc(Jornada jornada);
    List<Gasto> findByJornadaId(Long jornadaId);

    @Query("SELECT COALESCE(SUM(g.valorCop), 0) FROM Gasto g WHERE g.jornada.id = :jornadaId")
    BigDecimal sumValorCopByJornadaId(@Param("jornadaId") Long jornadaId);
}
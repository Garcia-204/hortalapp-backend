package com.hortalapp.hortalapp_backen.repository;



import com.hortalapp.hortalapp_backen.entity.VentaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VentaItemRepository extends JpaRepository<VentaItem, Long> {
    List<VentaItem> findByVentaId(Long ventaId);
}

package com.gestion.reservas.repository;

import com.gestion.reservas.entity.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReservaRepositoryCustomImpl implements ReservaRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Reserva> buscarPorFiltros(LocalDateTime fechaInicio, LocalDateTime fechaFin, Long tipoEspacioId, Long estadoId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Reserva> cq = cb.createQuery(Reserva.class);
        Root<Reserva> reserva = cq.from(Reserva.class);
        Join<Object, Object> espacio = reserva.join("espacio");

        List<Predicate> predicates = new ArrayList<>();

        if (fechaInicio != null) {
            predicates.add(cb.greaterThanOrEqualTo(reserva.get("fechaInicio"), fechaInicio));
        }

        if (fechaFin != null) {
            predicates.add(cb.lessThanOrEqualTo(reserva.get("fechaFin"), fechaFin));
        }

        if (tipoEspacioId != null) {
            predicates.add(cb.equal(espacio.get("tipoEspacio").get("idTipoEspacio"), tipoEspacioId));
        }

        if (estadoId != null) {
            predicates.add(cb.equal(reserva.get("estado").get("idEstado"), estadoId));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(reserva.get("fechaInicio"))); // orden opcional

        return entityManager.createQuery(cq).getResultList();
    }
}
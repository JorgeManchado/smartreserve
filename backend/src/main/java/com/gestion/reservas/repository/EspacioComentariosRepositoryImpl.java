package com.gestion.reservas.repository;

import com.gestion.reservas.dto.ComentarioDTO;
import com.gestion.reservas.dto.EspacioComentariosDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.stream.Collectors;

public class EspacioComentariosRepositoryImpl implements EspacioComentariosRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<EspacioComentariosDTO> obtenerResumenComentariosPorEspacio() {
        String sql = """
        SELECT 
            e.id_espacio,
            e.nombre,
            e.imagen,
            COALESCE(AVG(CASE WHEN ec.descripcion = 'Aprobado' THEN c.valoracion ELSE NULL END), 0.0) AS promedio,
            COUNT(CASE WHEN ec.descripcion = 'Aprobado' THEN 1 ELSE NULL END) AS cantidad
        FROM espacios e
        LEFT JOIN reservas r ON r.id_espacio = e.id_espacio
        LEFT JOIN comentarios c ON c.id_reserva = r.id_reserva
        LEFT JOIN estados_comentario ec ON ec.id_estado = c.id_estado
        GROUP BY e.id_espacio, e.nombre, e.imagen
    """;

        List<Object[]> resultados = entityManager.createNativeQuery(sql).getResultList();

        return resultados.stream().map(row -> {
            Long idEspacio = ((Number) row[0]).longValue();

            EspacioComentariosDTO dto = new EspacioComentariosDTO(
                    idEspacio,
                    (String) row[1],
                    (String) row[2],
                    ((Number) row[3]).doubleValue(),
                    ((Number) row[4]).longValue()
            );

            dto.setComentarios(obtenerComentariosAprobadosPorEspacio(idEspacio));
            return dto;
        }).collect(Collectors.toList());

    }

    public List<ComentarioDTO> obtenerComentariosAprobadosPorEspacio(Long idEspacio) {
        String sql = """
        SELECT 
            c.id_comentario,
            c.texto,
            c.valoracion,
            c.fecha,
            ec.descripcion,
            ec.id_estado,
            u.nombre AS nombre_usuario,
            u.id_usuario,
            r.id_reserva
        FROM comentarios c
        JOIN reservas r ON r.id_reserva = c.id_reserva
        JOIN usuarios u ON u.id_usuario = r.id_usuario
        JOIN estados_comentario ec ON ec.id_estado = c.id_estado
        WHERE r.id_espacio = :idEspacio AND ec.descripcion = 'Aprobado'
        ORDER BY c.fecha DESC
    """;

        List<Object[]> rows = entityManager.createNativeQuery(sql)
                .setParameter("idEspacio", idEspacio)
                .getResultList();

        return rows.stream()
                .map(r -> new ComentarioDTO(
                        ((Number) r[0]).longValue(),         // idComentario
                        (String) r[1],                       // texto
                        ((Number) r[2]).intValue(),          // valoracion
                        ((java.sql.Timestamp) r[3]).toLocalDateTime(), // fecha
                        (String) r[4],                       // estadoDescripcion
                        ((Number) r[5]).longValue(),         // idEstado
                        (String) r[6],                       // nombreUsuario
                        ((Number) r[7]).longValue(),         // idUsuario
                        ((Number) r[8]).longValue()          // idReserva
                ))
                .collect(Collectors.toList());
    }
}
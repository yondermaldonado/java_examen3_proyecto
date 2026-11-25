package com.carmotors.dao;

import com.carmotors.excepcion.DaoException;
import com.carmotors.modelo.FiltroReporte;
import com.carmotors.modelo.SolicitudServicioReporteDato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReporteServiciosDAOJdbc
        implements ReporteServiciosDAO {

    private static final String CONSULTAR_SOLICITUDES = """
            SELECT orden.id, tipo.nombre, orden.fecha_recepcion
            FROM servicios orden
            JOIN mantenimiento_tipos tipo
                ON tipo.id = orden.mantenimiento_tipo_id
            WHERE orden.fecha_recepcion >= ?
              AND orden.fecha_recepcion <= ?
            """;

    private static final LocalTime FIN_DEL_DIA =
            LocalTime.of(23, 59, 59, 999_999_000);

    @Override
    public List<SolicitudServicioReporteDato> listarSolicitudes(
            Connection conexion,
            FiltroReporte filtro
    ) {

        String sql = CONSULTAR_SOLICITUDES;

        // Agregar la condición si se seleccionó un tipo.
        if (filtro.tipoMantenimiento() != null) {
            sql += " AND tipo.nombre = ?";
        }

        try (PreparedStatement sentencia =
                     conexion.prepareStatement(sql)) {

            sentencia.setObject(
                    1,
                    filtro.fechaInicio().atStartOfDay()
            );

            sentencia.setObject(
                    2,
                    filtro.fechaFin().atTime(FIN_DEL_DIA)
            );

            // Establecer el tercer parámetro si existe.
            if (filtro.tipoMantenimiento() != null) {

                sentencia.setString(
                        3,
                        filtro.tipoMantenimiento()
                );
            }

            List<SolicitudServicioReporteDato> solicitudes =
                    new ArrayList<>();

            try (ResultSet resultados =
                         sentencia.executeQuery()) {

                while (resultados.next()) {

                    solicitudes.add(
                            new SolicitudServicioReporteDato(

                                    resultados.getLong("id"),

                                    resultados.getString("nombre"),

                                    resultados.getObject(
                                            "fecha_recepcion",
                                            LocalDateTime.class
                                    ).toLocalDate()
                            )
                    );
                }
            }

            return solicitudes;

        } catch (SQLException e) {

            throw new DaoException(
                    "No se pudo consultar el reporte de servicios más solicitados.",
                    e
            );
        }
    }
}
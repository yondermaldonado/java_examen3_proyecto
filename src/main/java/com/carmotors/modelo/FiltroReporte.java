package com.carmotors.modelo;

import java.time.LocalDate;

public record FiltroReporte(
        LocalDate fechaInicio,
        LocalDate fechaFin,
        OrdenReporte orden,
        String tipoMantenimiento
) {

    // Constructor compatible con los reportes anteriores.
    public FiltroReporte(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            OrdenReporte orden
    ) {
        this(fechaInicio, fechaFin, orden, null);
    }

    public FiltroReporte {

        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException(
                    "Las fechas son obligatorias"
            );
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException(
                    "La fecha inicial no puede ser posterior a la final"
            );
        }

        if (orden == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un orden"
            );
        }

        // Validación del nuevo filtro.
        if (tipoMantenimiento != null
                && !tipoMantenimiento.equals("Preventivo")
                && !tipoMantenimiento.equals("Correctivo")) {

            throw new IllegalArgumentException(
                    "Tipo de mantenimiento inválido"
            );
        }
    }

    public boolean incluye(LocalDate fecha) {

        return fecha != null
                && !fecha.isBefore(fechaInicio)
                && !fecha.isAfter(fechaFin);
    }
}
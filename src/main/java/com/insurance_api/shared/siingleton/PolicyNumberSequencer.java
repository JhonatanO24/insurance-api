package com.insurance_api.shared.siingleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class PolicyNumberSequencer {

    private static final Logger log = LoggerFactory.getLogger(PolicyNumberSequencer.class);
    private static final String PREFIX = "POL";
    private static final int SEQUENCE_DIGITS = 6;

    // Corregido el formato: %s (PREFIX), %d (AÑO), %06d (SECUENCIA)
    private static final String FORMAT = "%s-%d-%0" + SEQUENCE_DIGITS + "d";

    private final AtomicLong counter = new AtomicLong(0);

    /**
     * Genera el siguiente número de póliza único y secuencial.
     * Formato: POL-{AÑO}-{SECUENCIA_6_DÍGITOS}
     * Ejemplo: POL-2026-000001, POL-2026-000002, ...

     * AtomicLong.incrementAndGet() es atómico — thread-safe
     * sin bloqueos explícitos.
     *
     * @return número de póliza único
     */
    public String next() {
        long sequence = counter.incrementAndGet();
        int currentYear = Year.now().getValue();

        String policyNumber = String.format(FORMAT, PREFIX, currentYear, sequence);

        log.debug("[PolicyNumberSequencer] Número generado: {}", policyNumber);
        return policyNumber;
    }

    /**
     * Retorna el valor actual del contador sin incrementarlo.
     * Útil para monitoreo y diagnóstico.
     */
    public long currentSequence() {
        return counter.get();
    }
}

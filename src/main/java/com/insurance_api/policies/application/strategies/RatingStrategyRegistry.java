package com.insurance_api.policies.application.strategies;

import com.insurance_api.policies.domain.enums.RatingStrategy;
import com.insurance_api.policies.domain.exceptions.UnsupportedRatingStrategyException;
import com.insurance_api.policies.domain.ports.RatingStrategyPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Registro de estrategias de tarificación.

 * OCP en acción: agregar una 4ª estrategia = crear una nueva clase
 *  * que implemente RatingStrategyPort + anotarla con @Component.
 *  * Este registro la detecta automáticamente — CERO cambios aquí.

 *  * Sin switch — el use case llama getStrategy(name) y listo.
 */
@Component
public class RatingStrategyRegistry {

    private final Map<RatingStrategy, RatingStrategyPort> strategies;

    // Spring inyecta automáticamente TODAS las implementaciones de RatingStrategyPort
    public RatingStrategyRegistry(List<RatingStrategyPort> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        RatingStrategyPort::getName,
                        Function.identity()
                ));
    }

    public RatingStrategyPort getStrategy(RatingStrategy ratingStrategy) {
        RatingStrategyPort strategy = strategies.get(ratingStrategy);
        if (strategy == null) {
            throw new UnsupportedRatingStrategyException(ratingStrategy.name());
        }
        return strategy;
    }
}

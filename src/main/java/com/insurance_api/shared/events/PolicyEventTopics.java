package com.insurance_api.shared.events;

/**
 * Constantes de los topics de Kafka.
 * Sin strings mágicos dispersos por el código — todos aquí.
 * Si un topic cambia de nombre = un solo lugar para editar.
 */
public final class PolicyEventTopics {

    private PolicyEventTopics() {}

    public static final String POLICY_ISSUED      = "policy.issued";
    public static final String POLICY_ACTIVATED   = "policy.activated";
    public static final String POLICY_SUSPENDED   = "policy.suspended";
    public static final String POLICY_REACTIVATED = "policy.reactivated";
    public static final String POLICY_CANCELLED   = "policy.cancelled";

    // Topic comodín para consumers que escuchan todos los eventos
    public static final String POLICY_ALL = "policy.*";
}

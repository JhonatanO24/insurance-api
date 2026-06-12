CREATE TABLE policies (
    id               UUID            NOT NULL,
    policy_number    VARCHAR(20)     NOT NULL,
    customer_id      UUID            NOT NULL,
    branch           VARCHAR(10)     NOT NULL,
    rating_strategy  VARCHAR(15)     NOT NULL,
    status           VARCHAR(15)     NOT NULL,
    coverage         JSONB           NOT NULL,
    monthly_premium  NUMERIC(15, 2)  NOT NULL,
    risk_profile     JSONB,
    created_at       TIMESTAMP       NOT NULL,
    updated_at       TIMESTAMP       NOT NULL,

    CONSTRAINT pk_policies
        PRIMARY KEY (id),

    CONSTRAINT uq_policies_policy_number
        UNIQUE (policy_number),

    CONSTRAINT fk_policies_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id),

    CONSTRAINT chk_policies_branch
        CHECK (branch IN ('AUTO', 'LIFE', 'HOME', 'HEALTH')),

    CONSTRAINT chk_policies_status
        CHECK (status IN ('QUOTED', 'ISSUED', 'ACTIVE', 'SUSPENDED', 'CANCELLED')),

    CONSTRAINT chk_policies_monthly_premium
        CHECK (monthly_premium > 0)
);

-- Índices para las consultas más frecuentes
CREATE INDEX idx_policies_customer_id
    ON policies(customer_id);

CREATE INDEX idx_policies_status
    ON policies(status);

CREATE INDEX idx_policies_branch
    ON policies(branch);
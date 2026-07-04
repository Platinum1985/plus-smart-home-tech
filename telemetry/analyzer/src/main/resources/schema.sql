-- создаём таблицу scenarios
CREATE TABLE IF NOT EXISTS scenarios (
    id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    hub_id VARCHAR,
    name   VARCHAR,
    UNIQUE (hub_id, name)
);

-- создаём таблицу sensors
CREATE TABLE IF NOT EXISTS sensors (
    id     VARCHAR PRIMARY KEY,
    hub_id VARCHAR
);

-- создаём таблицу conditions
CREATE TABLE IF NOT EXISTS conditions (
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type      VARCHAR,
    operation VARCHAR,
    value     INTEGER
);

-- создаём таблицу actions
CREATE TABLE IF NOT EXISTS actions (
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sensor_id VARCHAR,
    type     VARCHAR,
    value    INTEGER
);

-- создаём таблицу scenario_conditions, связывающую сценарий, датчик и условие активации сценария
CREATE TABLE IF NOT EXISTS scenario_conditions (
    scenario_id  BIGINT REFERENCES scenarios (id),
    sensor_id    VARCHAR REFERENCES sensors (id),
    condition_id BIGINT REFERENCES conditions (id),
    PRIMARY KEY (scenario_id, sensor_id, condition_id)
);

-- создаём таблицу scenario_actions, связывающую сценарий, датчик и действие, которое нужно выполнить при активации сценария
CREATE TABLE IF NOT EXISTS scenario_actions (
    scenario_id BIGINT REFERENCES scenarios (id),
    sensor_id   VARCHAR REFERENCES sensors (id),
    action_id   BIGINT REFERENCES actions (id),
    PRIMARY KEY (scenario_id, sensor_id, action_id)
);

-- Создаём функцию с улучшенной обработкой NULL-значений
CREATE OR REPLACE FUNCTION check_hub_id()
    RETURNS TRIGGER AS $$
BEGIN
    -- Получаем hub_id из сценариев и датчиков
    DECLARE scenario_hub VARCHAR;
    DECLARE sensor_hub VARCHAR;

    -- Безопасное получение значений с обработкой возможных NULL
    SELECT hub_id INTO scenario_hub
    FROM scenarios
    WHERE id = NEW.scenario_id;

    SELECT hub_id INTO sensor_hub
    FROM sensors
    WHERE id = NEW.sensor_id;

    -- Проверяем, что оба значения найдены и совпадают
    IF scenario_hub IS NULL OR sensor_hub IS NULL THEN
        RAISE EXCEPTION 'Either scenario_id or sensor_id does not exist: scenario_id=% sensor_id=%',
            NEW.scenario_id, NEW.sensor_id;
    END IF;

    IF scenario_hub != sensor_hub THEN
        RAISE EXCEPTION 'Hub IDs do not match for scenario_id % and sensor_id %, scenario_hub % and sensor_hub %',
            NEW.scenario_id, NEW.sensor_id, scenario_hub, sensor_hub;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- затем создаём триггеры
CREATE TRIGGER tr_bi_scenario_conditions_hub_id_check
    BEFORE INSERT
    ON scenario_conditions
    FOR EACH ROW
EXECUTE FUNCTION check_hub_id();

CREATE TRIGGER tr_bi_scenario_actions_hub_id_check
    BEFORE INSERT
    ON scenario_actions
    FOR EACH ROW
EXECUTE FUNCTION check_hub_id();
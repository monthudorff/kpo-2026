package ru.hse.vyshkat.fleet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.hse.vyshkat.domain.vehicle.Bicycle;
import ru.hse.vyshkat.domain.vehicle.Vehicle;

/** Unit-тесты правила «для новичков». */
@DisplayName("Правило для новичков")
class SimplicityThresholdPolicyTest {

    private static final int STRICT_THRESHOLD = 8;

    private final BeginnerPolicy defaultPolicy = new SimplicityThresholdPolicy(
            SimplicityThresholdPolicy.DEFAULT_MIN_SIMPLICITY);

    @ParameterizedTest(name = "простота {0} -> подходит новичку: {1}")
    @CsvSource({"1, false", "5, false", "6, true", "10, true"})
    @DisplayName("По умолчанию новичкам выдают транспорт с простотой ≥ 6")
    void defaultRuleRequiresSimplicityAtLeastSix(final int simplicity,
                                                 final boolean expected) {
        assertEquals(expected, defaultPolicy.isSuitableForBeginner(
                new Bicycle("Stels", 1, simplicity)));
    }

    @Test
    @DisplayName("Порог настраивается")
    void thresholdIsConfigurable() {
        final BeginnerPolicy strict =
                new SimplicityThresholdPolicy(STRICT_THRESHOLD);

        assertFalse(strict.isSuitableForBeginner(
                new Bicycle("Stels", 1, STRICT_THRESHOLD - 1)));
        assertTrue(strict.isSuitableForBeginner(
                new Bicycle("Stels", 1, STRICT_THRESHOLD)));
    }

    @ParameterizedTest(name = "порог {0} отклоняется")
    @ValueSource(ints = {
            Vehicle.MIN_SIMPLICITY - 1, Vehicle.MAX_SIMPLICITY + 1})
    @DisplayName("Порог должен лежать в шкале простоты 1–10")
    void thresholdMustBeWithinSimplicityScale(final int threshold) {
        assertThrows(IllegalArgumentException.class,
                () -> new SimplicityThresholdPolicy(threshold));
    }

    @Test
    @DisplayName("Формулировка правила называет порог")
    void descriptionNamesTheThreshold() {
        assertEquals("простота для новичка ≥ 6", defaultPolicy.description());
    }
}

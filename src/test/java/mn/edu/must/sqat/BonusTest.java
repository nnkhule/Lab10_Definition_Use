package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BonusTest {

    @Test
    @DisplayName("allDefs_resetAndNoBonus: lines 32, 35-46, 49-56")
    void allDefsResetAndNoBonus() {
        assertEquals(0, DataFlowTargets.bonus(new int[]{1, 0, 1}, 10));
    }

    @Test
    @DisplayName("allDefs_presentAndStreakBonus: lines 32, 35-43, 50-56")
    void allDefsPresentAndStreakBonus() {
        assertEquals(8, DataFlowTargets.bonus(new int[]{1, 1, 1, 1, 1}, 0));
    }

    @Test
    @DisplayName("du_emptyArrayThresholdZero: lines 32-F, 38-F, 50-T, 53-F")
    void duEmptyArrayThresholdZero() {
        assertEquals(5, DataFlowTargets.bonus(new int[]{}, 0));
    }

    @Test
    @DisplayName("du_nullAttendance: line 32 attendance-T")
    void duNullAttendance() {
        assertThrows(IllegalArgumentException.class, () -> DataFlowTargets.bonus(null, 0));
    }

    @Test
    @DisplayName("du_negativeThreshold: line 32 threshold-T")
    void duNegativeThreshold() {
        assertThrows(IllegalArgumentException.class, () -> DataFlowTargets.bonus(new int[]{}, -1));
    }

    @Test
    @DisplayName("du_shortResetAndUnderThreshold: lines 36-46, 50-F, 53-F")
    void duShortResetAndUnderThreshold() {
        assertEquals(0, DataFlowTargets.bonus(new int[]{1, 1, 0, 1}, 10));
    }

    @Test
    @DisplayName("du_resetThenFiveAndThresholdMet: lines 36-54, 50-T, 53-T")
    void duResetThenFiveAndThresholdMet() {
        assertEquals(8, DataFlowTargets.bonus(new int[]{1, 1, 0, 1, 1, 1, 1, 1}, 5));
    }

    @Test
    @DisplayName("du_longStreakUnderThreshold: lines 49, 50-F, 53-T, 54, 56")
    void duLongStreakUnderThreshold() {
        assertEquals(3, DataFlowTargets.bonus(new int[]{1, 1, 1, 1, 1}, 6));
    }
}

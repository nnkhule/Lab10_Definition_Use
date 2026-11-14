package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BonusTest {

    @Test
    @DisplayName("allDefs_resetAndNoBonus: lines 33, 36-46, 50, 55")
    void allDefsResetAndNoBonus() {
        assertEquals(0, DataFlowTargets.bonus(new int[]{1, 0, 1}, 10));
    }

    @Test
    @DisplayName("allDefs_presentAndStreakBonus: lines 33, 36-43, 51-55")
    void allDefsPresentAndStreakBonus() {
        assertEquals(8, DataFlowTargets.bonus(new int[]{1, 1, 1, 1, 1}, 0));
    }
}

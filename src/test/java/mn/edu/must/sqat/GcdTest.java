package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GcdTest {

    @Test
    @DisplayName("gcd(7,0): lines 13 to 15-F and 20")
    void gcdZeroSecondArgument() {
        assertEquals(7, DataFlowTargets.gcd(7, 0));
    }

    @Test
    @DisplayName("gcd(0,5): line 13 definitions through lines 15-T, 16-18, then 15-F and 20")
    void gcdOneLoopIteration() {
        assertEquals(5, DataFlowTargets.gcd(0, 5));
    }

    @Test
    @DisplayName("gcd(12,8): line 18 definitions reach lines 15-T, 16, 17, then 15-F and 20")
    void gcdMultipleLoopIterations() {
        assertEquals(4, DataFlowTargets.gcd(12, 8));
    }
}

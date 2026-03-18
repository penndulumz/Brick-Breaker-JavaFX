import org.OOPproject.BrickBreakerFX.model.effects.Blink;
import org.OOPproject.BrickBreakerFX.model.Bricks.Brick;
import org.OOPproject.BrickBreakerFX.model.Bricks.BrickType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test cho lá»›p Blink.
 * Kiá»ƒm tra sá»± liÃªn káº¿t vá»›i Brick, cáº­p nháº­t hoáº¡t áº£nh Sprite vÃ  tráº¡ng thÃ¡i káº¿t thÃºc.
 */



class BlinkTest {

    private Blink blink;
    private Brick mockBrick;

    /** Má»™t lá»›p Brick giáº£ Ä‘á»ƒ test (vÃ¬ Brick lÃ  abstract). */
    static class TestBrick extends Brick {
        public TestBrick(int x, int y, int w, int h, BrickType type) {
            super(x, y, w, h, type);
            this.hitPoints = type.maxHits;
            this.scoreValue = type.ScoreValue;
        }

        @Override
        public void takeHit() {
            hitPoints--;
        }
    }

    @BeforeEach
    void setUp() {
        mockBrick = new TestBrick(50, 100, 64, 32, BrickType.BLUE);
        blink = new Blink(mockBrick);
    }

    @Test
    void testInitialStateMatchesBrick() {
        assertEquals(mockBrick.getX(), blink.getX());
        assertEquals(mockBrick.getY(), blink.getY());
        assertEquals(mockBrick.getWidth(), blink.getWidth());
        assertEquals(mockBrick.getHeight(), blink.getHeight());
        assertSame(mockBrick, blink.getAttachedBrick());
    }

    @Test
    void testUpdateKeepsPositionSynced() {
        // Di chuyá»ƒn brick
        mockBrick.setX(200);
        mockBrick.setY(300);

        blink.update(0.02);

        assertEquals(200, blink.getX());
        assertEquals(300, blink.getY());
    }

    @Test
    void testSpriteAdvancesOverTime() {
        int oldFrameX = blink.getFrameX();
        blink.update(0.05); // Sau 0.05s, frame pháº£i Ä‘á»•i

        assertTrue(blink.getFrameX() != oldFrameX || blink.getFrameY() > 0,
                "Frame of sprite must change after time update");
    }


    //TODO debug this test/
    @Test
    void testBlinkFinishesAfterEnoughTime() {
        // Blink cÃ³ Sprite 8x3, má»—i frame 0.015s => 24 frame => khoáº£ng 0.36s
        double totalTime = 0.36;
        double step = 0.015;

        for (double t = 0; t <= totalTime; t += step) {
            blink.update(step);
        }

        assertTrue(blink.isFinished(), "Blink must end after all frame");
    }

    @Test
    void testSpriteFrameLimits() {
        assertEquals(8, blink.getMaxFrameX());
        assertEquals(3, blink.getMaxFrameY());
        assertTrue(blink.getFrameX() < blink.getMaxFrameX());
        assertTrue(blink.getFrameY() < blink.getMaxFrameY());
    }
}


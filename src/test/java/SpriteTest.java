import org.OOPproject.BrickBreakerFX.model.Sprite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests cho lá»›p Sprite.
 * kiá»ƒm tra hoáº¡t Ä‘á»™ng cá»§a há»‡ thá»‘ng hoáº¡t áº£nh (update, loop, reset, finished).
 */
class SpriteTest {

    private Sprite sprite;
    private static final double FRAME_TIME = 0.015; // seconds per frame

    @BeforeEach
    void setUp() {
        sprite = new Sprite(4, 2, FRAME_TIME, false); // 4 frames ngang, 2 dá»c
    }

    @Test
    void testInitialState() {
        assertEquals(0, sprite.getFrameX());
        assertEquals(0, sprite.getFrameY());
        assertFalse(sprite.isFinished());
        assertFalse(sprite.isLoop());
    }

    @Test
    void testAdvanceFramesWithoutLoop() {
        // 4x2 => 8 frames tá»•ng cá»™ng, má»—i frame = 0.015s
        // Sau 8 láº§n update, animation pháº£i káº¿t thÃºc
        for (int i = 0; i < 8; i++) {
            sprite.update(FRAME_TIME);
        }

        assertTrue(sprite.isFinished(), "Sprite pháº£i káº¿t thÃºc sau khi Ä‘i háº¿t frame");
        assertEquals(3, sprite.getFrameX(), "Frame X pháº£i dá»«ng á»Ÿ cuá»‘i");
        assertEquals(1, sprite.getFrameY(), "Frame Y pháº£i dá»«ng á»Ÿ dÃ²ng cuá»‘i");
    }

    @Test
    void testResetRestoresInitialState() {
        sprite.update(FRAME_TIME * 10);
        sprite.reset();

        assertEquals(0, sprite.getFrameX());
        assertEquals(0, sprite.getFrameY());
        assertFalse(sprite.isFinished());
    }

    @Test
    void testLoopingAnimationResetsAfterEnd() {
        Sprite loopingSprite = new Sprite(3, 2, FRAME_TIME, true);

        int totalFrames = 3 * 2; // 6 frames
        for (int i = 0; i < totalFrames + 2; i++) {
            loopingSprite.update(FRAME_TIME);
        }

        assertFalse(loopingSprite.isFinished(), "Looping animation khÃ´ng bao giá» káº¿t thÃºc");
        assertTrue(loopingSprite.getFrameY() < loopingSprite.getMaxFrameY(),
                "Frame Y pháº£i quay láº¡i dÃ²ng Ä‘áº§u khi loop");
    }

    @Test
    void testUpdateStopsWhenFinishedAndNotLooping() {
        // Cháº¡y Ä‘áº¿n khi hoÃ n thÃ nh
        for (int i = 0; i < 8; i++) {
            sprite.update(FRAME_TIME);
        }

        int frameX = sprite.getFrameX();
        int frameY = sprite.getFrameY();

        sprite.update(FRAME_TIME * 10); // thÃªm thá»i gian, khÃ´ng Ä‘Æ°á»£c thay Ä‘á»•i gÃ¬

        assertEquals(frameX, sprite.getFrameX());
        assertEquals(frameY, sprite.getFrameY());
        assertTrue(sprite.isFinished());
    }

    @Test
    void testPartialUpdatesDoNotAdvanceFrame() {
        sprite.update(FRAME_TIME / 2); // chÆ°a Ä‘á»§ thá»i gian Ä‘á»ƒ Ä‘á»•i frame
        assertEquals(0, sprite.getFrameX());
        assertEquals(0, sprite.getFrameY());
        assertFalse(sprite.isFinished());
    }
}


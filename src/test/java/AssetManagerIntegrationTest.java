import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import org.OOPproject.BrickBreakerFX.model.Bricks.BrickType;
import org.OOPproject.BrickBreakerFX.model.PowerUps.PowerUpTypes;
import org.OOPproject.BrickBreakerFX.view.AssetManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test cho AssetManager.
 * Test nÃ y xÃ¡c minh viá»‡c load hÃ¬nh áº£nh & Ã¢m thanh tháº­t tá»« thÆ° má»¥c assets.
 *
 * YÃªu cáº§u:
 *  - ThÆ° má»¥c /assets/textures/ vÃ  /assets/sfx/ tá»“n táº¡i trong resources.
 *  - JavaFX media modules (javafx.controls, javafx.media) Ä‘Æ°á»£c báº­t.
 */
public class AssetManagerIntegrationTest {

    private static AssetManager assetManager;

    @BeforeAll
    static void setup() {
        // JavaFX cáº§n khá»Ÿi Ä‘á»™ng trÆ°á»›c khi sá»­ dá»¥ng Image hoáº·c AudioClip
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // ÄÃ£ khá»Ÿi Ä‘á»™ng rá»“i
        }

        assetManager = AssetManager.getInstance();
        assertNotNull(assetManager, "AssetManager instance must not be null");
    }

    // ðŸ§© Test singleton
    @Test
    void testSingleton() {
        AssetManager a1 = AssetManager.getInstance();
        AssetManager a2 = AssetManager.getInstance();
        assertSame(a1, a2, "AssetManager should behave as a singleton");
    }

    // ðŸ§© Test background patterns
    @Test
    void testBackgroundPatterns() {
        Image pattern1 = assetManager.getBackgroundPattern(1);
        Image pattern5 = assetManager.getBackgroundPattern(5); // test modulo logic

        assertNotNull(pattern1, "Background pattern 1 should not be null");
        assertNotNull(pattern5, "Background pattern 5 should not be null");
        assertSame(pattern1.getClass(), Image.class, "Should return valid Image objects");
    }

    // ðŸ§© Test brick images
    @Test
    void testBrickImagesLoaded() {
        for (BrickType type : BrickType.values()) {
            Image img = assetManager.getBrickImage(type);
            if (type == BrickType.NONE) {
                // NONE cÃ³ thá»ƒ khÃ´ng cÃ³ hÃ¬nh â€” há»£p lá»‡
                assertNull(img, "BrickType.NONE should return null image");
            } else {
                assertNotNull(img, "Brick image for type " + type + " should not be null");
            }
        }
    }

    // ðŸ§© Test power-up images
    @Test
    void testPowerUpSpriteMaps() {
        for (PowerUpTypes type : PowerUpTypes.values()) {
            Image img = assetManager.getPowerUpSpriteMap(type);
            assertNotNull(img, "PowerUp sprite map for " + type + " should not be null");
        }
    }

    // ðŸ§© Test AudioClip load
    @Test
    void testAudioClipsExist() {
        String[] files = {
                "ball_block.wav", "ball_hard_block.wav", "ball_paddle.wav",
                "bounce.wav", "click.wav", "explosion.wav",
                "game_over.wav", "game_start.wav", "gun.wav",
                "laserShoot.wav", "level_ready.wav", "powerUp.wav"
        };

        for (String file : files) {
            AudioClip clip = assetManager.getAudioClip(file);
            assertNotNull(clip, "AudioClip should not be null for " + file);
        }
    }

    // ðŸ§© Test missing audio file handled safely
    @Test
    void testMissingAudioClipHandledGracefully() {
        AudioClip clip = assetManager.getAudioClip("not_exists.wav");
        assertNull(clip, "Should return null when audio file not found");
    }

    // ðŸ§© Test direct image getters
    @Test
    void testDirectImageGetters() {
        assertNotNull(assetManager.getBallImg(), "Ball image should not be null");
        assertNotNull(assetManager.getHeartImg(), "Heart image should not be null");
        assertNotNull(assetManager.getEnemyMapImg(), "Enemy map image should not be null");
        assertNotNull(assetManager.getExplosionMapImg(), "Explosion map should not be null");
        assertNotNull(assetManager.getBulletImg(), "Bullet image should not be null");
    }
}


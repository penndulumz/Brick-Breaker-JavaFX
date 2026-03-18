import javafx.application.Platform;
import org.OOPproject.BrickBreakerFX.model.managers.SoundManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test cho SoundManager vá»›i AssetManager tháº­t.
 * test nÃ y sáº½ táº£i vÃ  phÃ¡t cÃ¡c file Ã¢m thanh tháº­t trong /assets/sfx/.
 * Äáº£m báº£o cÃ¡c file WAV tá»“n táº¡i á»Ÿ Ä‘Ãºng Ä‘Æ°á»ng dáº«n resources.
 */
public class SoundManagerIntegrationTest {

    private static SoundManager soundManager;

    @BeforeAll
    static void init() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
        }

        soundManager = SoundManager.getInstance();
        assertNotNull(soundManager, "SoundManager instance should not be null");
    }

    @Test
    void testPlaySound_ExistingFile() {
        assertDoesNotThrow(() -> soundManager.playSound("ball_block.wav"),
                "Should play existing sound without throwing an exception");
    }

    @Test
    void testPlaySound_NonExistingFile() {
        assertDoesNotThrow(() -> soundManager.playSound("this_file_does_not_exist.wav"),
                "Should not throw exception even if sound file is missing");
    }

    @Test
    void testGetInstance_IsSingleton() {
        SoundManager instance1 = SoundManager.getInstance();
        SoundManager instance2 = SoundManager.getInstance();
        assertSame(instance1, instance2, "SoundManager must be a singleton");
    }
}


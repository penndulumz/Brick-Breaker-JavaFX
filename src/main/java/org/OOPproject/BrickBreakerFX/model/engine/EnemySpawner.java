package org.OOPproject.BrickBreakerFX.model.engine;

import org.OOPproject.BrickBreakerFX.model.Enemy;
import org.OOPproject.BrickBreakerFX.model.MovementType;

import java.util.Iterator;
import java.util.List;

import static org.OOPproject.BrickBreakerFX.utils.Constants.ENEMY_MOVEMENT_CYCLE;
import static org.OOPproject.BrickBreakerFX.utils.Constants.ENEMY_SIZE;
import static org.OOPproject.BrickBreakerFX.utils.Constants.GAME_WIDTH;

public class EnemySpawner {
    private static final double DOOR_OPEN_TIME = 1.0;

    private boolean spawningEnemies = false;
    private double spawnTimer = 0.0;

    public boolean isSpawningEnemies() {
        return spawningEnemies;
    }

    public void updateEnemies(double deltaTime, List<Enemy> enemies, int gameHeight) {
        removeOffscreenEnemies(enemies, gameHeight);

        if (spawningEnemies) {
            spawnTimer += deltaTime;

            if (spawnTimer >= 0.3 && enemies.size() == 0) {
                enemies.add(new Enemy(GAME_WIDTH / 2 - 90, 0, ENEMY_SIZE));
            }

            if (spawnTimer >= 0.5 && enemies.size() == 1) {
                enemies.add(new Enemy(GAME_WIDTH / 2 - 90, 0, ENEMY_SIZE));
            }

            if (spawnTimer >= 0.7 && enemies.size() == 2) {
                enemies.add(new Enemy(GAME_WIDTH / 2 + 80, 0, ENEMY_SIZE));
            }

            if (spawnTimer >= DOOR_OPEN_TIME) {
                spawningEnemies = false;
                spawnTimer = 0.0;
            }
            return;
        }

        if (enemies.size() < 3) {
            startSpawning();
            return;
        }

        for (Enemy enemy : enemies) {
            double timeLeft = enemy.getTimeInCurrentCircle();
            if (timeLeft - deltaTime <= 0) {
                MovementType movementType = Enemy.randMovementType();
                enemy.setMovementType(movementType);
                enemy.setTimeInCurrentCircle(ENEMY_MOVEMENT_CYCLE);
            } else {
                enemy.setTimeInCurrentCircle(timeLeft - deltaTime);
            }
        }
    }

    private void startSpawning() {
        spawningEnemies = true;
        spawnTimer = 0.0;
    }

    private void removeOffscreenEnemies(List<Enemy> enemies, int gameHeight) {
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy.getY() > gameHeight) {
                iterator.remove();
            }
        }
    }
}


package org.OOPproject.BrickBreakerFX.model.engine;

import javafx.scene.paint.Color;
import org.OOPproject.BrickBreakerFX.model.Ball;
import org.OOPproject.BrickBreakerFX.model.Bullet;
import org.OOPproject.BrickBreakerFX.model.Enemy;
import org.OOPproject.BrickBreakerFX.model.EnemyType;
import org.OOPproject.BrickBreakerFX.model.GameEngine;
import org.OOPproject.BrickBreakerFX.model.Paddle;
import org.OOPproject.BrickBreakerFX.model.Bricks.Brick;
import org.OOPproject.BrickBreakerFX.model.Bricks.BrickType;
import org.OOPproject.BrickBreakerFX.model.Bricks.UnbreakableBrick;
import org.OOPproject.BrickBreakerFX.model.PowerUps.ExpandPaddlePowerUp;
import org.OOPproject.BrickBreakerFX.model.PowerUps.ExtraLifePowerUp;
import org.OOPproject.BrickBreakerFX.model.PowerUps.FastBallPowerUp;
import org.OOPproject.BrickBreakerFX.model.PowerUps.GunPowerUp;
import org.OOPproject.BrickBreakerFX.model.PowerUps.MultiBallPowerUp;
import org.OOPproject.BrickBreakerFX.model.PowerUps.PowerUp;
import org.OOPproject.BrickBreakerFX.model.effects.Blink;
import org.OOPproject.BrickBreakerFX.model.effects.Destroy;
import org.OOPproject.BrickBreakerFX.model.effects.ParticleSystem;
import org.OOPproject.BrickBreakerFX.model.managers.SoundManager;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CollisionHandler {
    private final GameEngine gameEngine;
    private final Random random;
    private final SoundManager soundManager;
    private final ParticleSystem particleSystem;

    public CollisionHandler(GameEngine gameEngine, Random random, SoundManager soundManager, ParticleSystem particleSystem) {
        this.gameEngine = gameEngine;
        this.random = random;
        this.soundManager = soundManager;
        this.particleSystem = particleSystem;
    }

    public int handleCollisions(
            double deltaTime,
            Paddle paddle,
            List<Ball> balls,
            List<Bullet> bullets,
            List<Brick> bricks,
            List<Enemy> enemies,
            List<Blink> blinks,
            List<Destroy> destroys,
            List<PowerUp> powerUps
    ) {
        int scoreDelta = 0;
        scoreDelta += handleBallsCollisions(deltaTime, paddle, balls, bricks, enemies, blinks, destroys, powerUps);
        scoreDelta += handleBulletsCollisions(deltaTime, bullets, bricks, enemies, blinks, destroys, powerUps);
        scoreDelta += handleEnemiesCollisions(deltaTime, paddle, bricks, enemies, destroys);
        return scoreDelta;
    }

    private int handleBallsCollisions(
            double deltaTime,
            Paddle paddle,
            List<Ball> balls,
            List<Brick> bricks,
            List<Enemy> enemies,
            List<Blink> blinks,
            List<Destroy> destroys,
            List<PowerUp> powerUps
    ) {
        int scoreDelta = 0;
        Iterator<Ball> iterator = balls.iterator();
        while (iterator.hasNext()) {
            Ball ball = iterator.next();
            scoreDelta += processBallCollision(ball, deltaTime, paddle, bricks, enemies, blinks, destroys, powerUps);
        }
        return scoreDelta;
    }

    private int handleBulletsCollisions(
            double deltaTime,
            List<Bullet> bullets,
            List<Brick> bricks,
            List<Enemy> enemies,
            List<Blink> blinks,
            List<Destroy> destroys,
            List<PowerUp> powerUps
    ) {
        int scoreDelta = 0;
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            if (bullet.getY() + deltaTime * bullet.getVelocityY() < 0) {
                bulletIterator.remove();
                continue;
            }

            boolean consumed = false;
            for (int i = bricks.size() - 1; i >= 0; i--) {
                Brick brick = bricks.get(i);
                if (bullet.willCollideBrick(brick, deltaTime)) {
                    scoreDelta += processBrickHit(brick, bricks, blinks, powerUps);
                    bulletIterator.remove();
                    consumed = true;
                    break;
                }
            }

            if (consumed) {
                continue;
            }

            for (int i = 0; i < enemies.size(); i++) {
                Enemy enemy = enemies.get(i);
                if (bullet.willCollideEnemy(enemy, deltaTime)) {
                    scoreDelta += damageEnemy(enemy, enemies, destroys);
                    bulletIterator.remove();
                    break;
                }
            }
        }
        return scoreDelta;
    }

    private int handleEnemiesCollisions(
            double deltaTime,
            Paddle paddle,
            List<Brick> bricks,
            List<Enemy> enemies,
            List<Destroy> destroys
    ) {
        int scoreDelta = 0;
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();

            for (Brick brick : bricks) {
                if (enemy.willHitBrick(brick, deltaTime)) {
                    String side = enemy.getCollisionSide(brick);
                    enemy.correctPositionAfterBrickHit(brick, side);
                    enemy.bounceOffBrick(side);
                    break;
                }
            }

            if (enemy.willHitPaddle(paddle, deltaTime)) {
                enemy.takeHit();
                if (enemy.isDestroyed()) {
                    scoreDelta += enemy.getScoreValue();
                    destroys.add(new Destroy(enemy));
                    enemyIterator.remove();
                    soundManager.playSound("explosion.wav");
                }
            }
        }
        return scoreDelta;
    }

    private int processBallCollision(
            Ball ball,
            double deltaTime,
            Paddle paddle,
            List<Brick> bricks,
            List<Enemy> enemies,
            List<Blink> blinks,
            List<Destroy> destroys,
            List<PowerUp> powerUps
    ) {
        int scoreDelta = 0;

        if (ball.collidesWith(paddle)) {
            ball.bounceOffPaddle(paddle);
            soundManager.playSound("ball_paddle.wav");
            return 0;
        }

        if (ball.getVelocityY() < 0) {
            for (int i = bricks.size() - 1; i >= 0; i--) {
                Brick brick = bricks.get(i);
                if (ball.willHitBrick(brick, deltaTime)) {
                    scoreDelta += processBrickBallCollision(brick, ball, bricks, blinks, powerUps);
                    return scoreDelta;
                }
            }
        } else {
            for (Brick brick : bricks) {
                if (ball.willHitBrick(brick, deltaTime)) {
                    scoreDelta += processBrickBallCollision(brick, ball, bricks, blinks, powerUps);
                    return scoreDelta;
                }
            }
        }

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (ball.willCollideEnemy(enemy, deltaTime)) {
                scoreDelta += processBallEnemyCollision(ball, enemy, enemies, destroys);
                return scoreDelta;
            }
        }

        return scoreDelta;
    }

    private int processBrickBallCollision(
            Brick brick,
            Ball ball,
            List<Brick> bricks,
            List<Blink> blinks,
            List<PowerUp> powerUps
    ) {
        String side = ball.getCollisionSide(brick);
        ball.correctPositionAfterBrickHit(brick, side);
        ball.bounceOffBrick(side);
        return processBrickHit(brick, bricks, blinks, powerUps);
    }

    private int processBrickHit(
            Brick brick,
            List<Brick> bricks,
            List<Blink> blinks,
                List<PowerUp> powerUps
    ) {
        int scoreDelta = 0;

        Color particleColor = getBrickColor(brick);
        particleSystem.createBurstEffect(
                brick.getX() + brick.getWidth() / 2.0,
                brick.getY() + brick.getHeight() / 2.0,
                particleColor,
                15
        );

        brick.takeHit();
        if (brick.getHitPoints() >= 1) {
            addBlinkIfMissing(blinks, brick);
        }

        if (brick.isDestroyed()) {
            scoreDelta += brick.getScoreValue();
            soundManager.playSound("ball_block.wav");
            if (!(brick instanceof UnbreakableBrick) && random.nextInt(100) < 15) {
                spawnPowerUp(powerUps, brick.getX(), brick.getY());
            }
            bricks.remove(brick);
        } else {
            soundManager.playSound("ball_hard_block.wav");
        }

        return scoreDelta;
    }

    private int processBallEnemyCollision(Ball ball, Enemy enemy, List<Enemy> enemies, List<Destroy> destroys) {
        int scoreDelta = 0;

        if (enemy.getType() == EnemyType.REFLECTOR) {
            ball.bounceOffEnemy();
            scoreDelta += damageEnemy(enemy, enemies, destroys);
            return scoreDelta;
        }

        if (enemy.getType() == EnemyType.UP_SENSITIVE && ball.getVelocityY() < 0) {
            scoreDelta += damageEnemy(enemy, enemies, destroys);
            return scoreDelta;
        }

        if (enemy.getType() == EnemyType.DOWN_SENSITIVE && ball.getVelocityY() > 0) {
            scoreDelta += damageEnemy(enemy, enemies, destroys);
        }

        return scoreDelta;
    }

    private int damageEnemy(Enemy enemy, List<Enemy> enemies, List<Destroy> destroys) {
        enemy.takeHit();
        if (!enemy.isDestroyed()) {
            return 0;
        }

        destroys.add(new Destroy(enemy));
        enemies.remove(enemy);
        soundManager.playSound("explosion.wav");
        return enemy.getScoreValue();
    }

    private void addBlinkIfMissing(List<Blink> blinks, Brick brick) {
        for (Blink blink : blinks) {
            if (blink.getAttachedBrick() == brick) {
                return;
            }
        }
        blinks.add(new Blink(brick));
    }

    private void spawnPowerUp(List<PowerUp> powerUps, int x, int y) {
        PowerUp powerUp;
        int powerUpType = random.nextInt(5);

        switch (powerUpType) {
            case 0:
                powerUp = new GunPowerUp(x, y, 20, 20);
                break;
            case 1:
                FastBallPowerUp fastBall = new FastBallPowerUp(x, y, 20, 20);
                if (!gameEngine.getBalls().isEmpty()) {
                    fastBall.setBall(gameEngine.getBalls().get(0));
                }
                powerUp = fastBall;
                break;
            case 2:
                powerUp = new MultiBallPowerUp(x, y, 20, 20, gameEngine);
                break;
            case 3:
                powerUp = new ExpandPaddlePowerUp(x, y, 20, 20);
                break;
            case 4:
            default:
                powerUp = new ExtraLifePowerUp(x, y, 20, 20, gameEngine);
                break;
        }

        powerUps.add(powerUp);
    }

    private Color getBrickColor(Brick brick) {
        return switch (brick.getType()) {
            case RUBY -> Color.RED;
            case YLLW -> Color.YELLOW;
            case BLUE -> Color.BLUE;
            case MGNT -> Color.MAGENTA;
            case LIME -> Color.LIME;
            case WHIT -> Color.WHITE;
            case ORNG -> Color.ORANGE;
            case CYAN -> Color.CYAN;
            case GRAY -> Color.GRAY;
            case GOLD -> Color.GOLD;
            default -> Color.GRAY;
        };
    }
}


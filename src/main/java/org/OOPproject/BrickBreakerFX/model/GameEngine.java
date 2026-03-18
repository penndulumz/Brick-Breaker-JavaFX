package org.OOPproject.BrickBreakerFX.model;

import org.OOPproject.BrickBreakerFX.model.Bricks.Brick;
import org.OOPproject.BrickBreakerFX.model.Bricks.UnbreakableBrick;
import org.OOPproject.BrickBreakerFX.model.PowerUps.PowerUp;
import org.OOPproject.BrickBreakerFX.model.effects.Blink;
import org.OOPproject.BrickBreakerFX.model.effects.Destroy;
import org.OOPproject.BrickBreakerFX.model.effects.ParticleSystem;
import org.OOPproject.BrickBreakerFX.model.engine.CollisionHandler;
import org.OOPproject.BrickBreakerFX.model.engine.EnemySpawner;
import org.OOPproject.BrickBreakerFX.model.managers.SoundManager;
import org.OOPproject.BrickBreakerFX.utils.Constants;
import org.OOPproject.BrickBreakerFX.utils.GameState;
import org.OOPproject.BrickBreakerFX.utils.InputSignal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static org.OOPproject.BrickBreakerFX.utils.Constants.BALL_SIZE;
import static org.OOPproject.BrickBreakerFX.utils.Constants.PADDLE_DEFAULT_WIDTH;
import static org.OOPproject.BrickBreakerFX.utils.Constants.PADDLE_HEIGHT;

public class GameEngine {
    private Paddle paddle;
    private List<Ball> balls;
    private List<Bullet> bullets;
    private List<Brick> bricks;
    private List<PowerUp> powerUps;
    private List<ActivePowerUp> activePowerUps;
    private List<Blink> blinks;
    private List<Enemy> enemies;
    private List<Destroy> destroys;

    private Level currentLevel;
    private final ParticleSystem particleSystem;

    private int score;
    private int lives;
    private int levelNumber;
    private GameState gameState;

    private final Random random;
    private final int gameWidth;
    private final int gameHeight;
    private final SoundManager soundManager;
    private final CollisionHandler collisionHandler;
    private final EnemySpawner enemySpawner;

    private static GameEngine instance = null;
    private boolean ballReleased;

    private static class ActivePowerUp {
        PowerUp powerUp;
        double remainingTime;

        ActivePowerUp(PowerUp powerUp, double duration) {
            this.powerUp = powerUp;
            this.remainingTime = duration;
        }
    }

    private GameEngine() {
        this.gameWidth = Constants.GAME_WIDTH;
        this.gameHeight = Constants.GAME_HEIGHT;
        this.random = new Random();
        this.particleSystem = new ParticleSystem();
        this.soundManager = SoundManager.getInstance();

        this.collisionHandler = new CollisionHandler(this, random, soundManager, particleSystem);
        this.enemySpawner = new EnemySpawner();

        this.balls = new ArrayList<>();
        this.bullets = new ArrayList<>();
        this.bricks = new ArrayList<>();
        this.powerUps = new ArrayList<>();
        this.activePowerUps = new ArrayList<>();
        this.blinks = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.destroys = new ArrayList<>();

        this.gameState = GameState.PLAYING;
        this.ballReleased = false;
    }

    public static GameEngine getInstance() {
        if (instance == null) {
            instance = new GameEngine();
        }
        return instance;
    }

    public void startGame() {
        score = 0;
        lives = 3;
        levelNumber = 13;
        gameState = GameState.PLAYING;
        particleSystem.clear();
        ballReleased = false;
        initializeLevel();
    }

    private void clearObjects() {
        balls.clear();
        bullets.clear();
        bricks.clear();
        powerUps.clear();
        for (ActivePowerUp activePowerUp : activePowerUps) {
            activePowerUp.powerUp.removeEffect(paddle);
        }
        activePowerUps.clear();
        blinks.clear();
        particleSystem.clear();
        enemies.clear();
        destroys.clear();
    }

    private void initializeLevel() {
        clearObjects();

        int paddleX = (gameWidth - PADDLE_DEFAULT_WIDTH) / 2;
        int paddleY = gameHeight - 50;
        paddle = new Paddle(paddleX, paddleY, PADDLE_DEFAULT_WIDTH, PADDLE_HEIGHT);

        int ballX = gameWidth / 2 - BALL_SIZE / 2;
        int ballY = gameHeight - 100;
        Ball ball = new Ball(ballX, ballY, BALL_SIZE, BALL_SIZE);
        ball.attachToPaddle(paddle);
        balls.add(ball);
        ballReleased = false;

        currentLevel = new Level(levelNumber);
        bricks = currentLevel.createBricks(gameWidth, 50);
    }

    public void updateGame(double deltaTime) {
        if (!gameState.equals(GameState.PLAYING)) {
            return;
        }

        if (!ballReleased && !balls.isEmpty() && balls.get(0).isStuckToPaddle() && paddle.getVelocityX() != 0) {
            balls.get(0).releaseFromPaddle();
            ballReleased = true;
        }

        checkCollisions(deltaTime);

        paddle.update(deltaTime);
        if (paddle.isGun()) {
            paddle.shoot(bullets, deltaTime);
        }

        for (Ball ball : balls) {
            ball.update(deltaTime);
        }

        Iterator<Ball> ballIterator = balls.iterator();
        while (ballIterator.hasNext()) {
            Ball ball = ballIterator.next();
            if (ball.getY() >= gameHeight) {
                ballIterator.remove();
            }
        }

        if (balls.isEmpty()) {
            loseLife();
        }

        for (Bullet bullet : bullets) {
            bullet.update(deltaTime);
        }

        for (Enemy enemy : enemies) {
            enemy.update(deltaTime);
        }
        updateEnemies(deltaTime);

        for (PowerUp powerUp : powerUps) {
            powerUp.update(deltaTime);
        }
        updateActivePowerUps(deltaTime);

        updateBlinks(deltaTime);
        updateDestroys(deltaTime);
        particleSystem.update(deltaTime);

        if (isLevelComplete()) {
            levelComplete();
        }
    }

    private boolean isLevelComplete() {
        for (Brick brick : bricks) {
            if (!(brick instanceof UnbreakableBrick)) {
                return false;
            }
        }
        return true;
    }

    private void updateActivePowerUps(double deltaTime) {
        Iterator<ActivePowerUp> iterator = activePowerUps.iterator();
        while (iterator.hasNext()) {
            ActivePowerUp active = iterator.next();
            active.remainingTime -= deltaTime;

            if (active.remainingTime <= 0) {
                active.powerUp.removeEffect(paddle);
                iterator.remove();
            }
        }
    }

    private void updateEnemies(double deltaTime) {
        enemySpawner.updateEnemies(deltaTime, enemies, gameHeight);
    }

    public boolean isSpawningEnemies() {
        return enemySpawner.isSpawningEnemies();
    }

    private void updateBlinks(double deltaTime) {
        Iterator<Blink> iterator = blinks.iterator();
        while (iterator.hasNext()) {
            Blink blink = iterator.next();
            blink.update(deltaTime);
            if (blink.isFinished() || !bricks.contains(blink.getAttachedBrick())) {
                iterator.remove();
            }
        }
    }

    private void updateDestroys(double deltaTime) {
        Iterator<Destroy> iterator = destroys.iterator();
        while (iterator.hasNext()) {
            Destroy destroy = iterator.next();
            destroy.update(deltaTime);
            if (destroy.isFinished()) {
                iterator.remove();
            }
        }
    }

    public void checkCollisions(double deltaTime) {
        score += collisionHandler.handleCollisions(
                deltaTime,
                paddle,
                balls,
                bullets,
                bricks,
                enemies,
                blinks,
                destroys,
                powerUps
        );
        paddleAndPowerUpCollision();
    }

    private void paddleAndPowerUpCollision() {
        Iterator<PowerUp> powerUpIterator = powerUps.iterator();
        while (powerUpIterator.hasNext()) {
            PowerUp powerUp = powerUpIterator.next();

            if (powerUp.getY() >= gameHeight) {
                powerUpIterator.remove();
                continue;
            }

            if (paddle.collidesWith(powerUp)) {
                activatePowerUp(powerUp);
                powerUpIterator.remove();
            }
        }
    }

    private void activatePowerUp(PowerUp powerUp) {
        powerUp.applyEffect(paddle);
        soundManager.playSound("powerUp.wav");
        double durationInSeconds = powerUp.getDuration() / 60.0;
        activePowerUps.add(new ActivePowerUp(powerUp, durationInSeconds));
    }

    public void spawnExtraBalls(Paddle paddle) {
        int centerX = paddle.getX() + paddle.getWidth() / 2;
        int centerY = paddle.getY() - BALL_SIZE;

        for (int i = 0; i < 2; i++) {
            Ball newBall = new Ball(centerX - BALL_SIZE / 2, centerY, BALL_SIZE, BALL_SIZE);
            newBall.setActive(true);

            double angle = (i == 0) ? Math.toRadians(-30) : Math.toRadians(-150);
            double speed = 500;
            newBall.velocityX = Math.cos(angle) * speed;
            newBall.velocityY = Math.sin(angle) * speed;

            balls.add(newBall);
        }
    }

    private void loseLife() {
        lives--;

        if (lives <= 0) {
            gameOver();
            return;
        }

        int paddleX = (gameWidth - paddle.getWidth()) / 2;
        paddle.setX(paddleX);
        paddle.stop();

        int ballX = gameWidth / 2 - BALL_SIZE / 2;
        int ballY = gameHeight - 100;

        Ball ball = new Ball(ballX, ballY, BALL_SIZE, BALL_SIZE);
        ball.attachToPaddle(paddle);
        balls.clear();
        balls.add(ball);
        ballReleased = false;

        for (ActivePowerUp activePowerUp : activePowerUps) {
            activePowerUp.powerUp.removeEffect(paddle);
        }
        activePowerUps.clear();
        powerUps.clear();
        bullets.clear();
    }

    public void resetGame() {
        gameState = GameState.PLAYING;
        score = 0;
        levelNumber = 1;
        lives = 3;
        initializeLevel();
    }

    private void levelComplete() {
        soundManager.playSound("level_ready.wav");
        levelNumber++;
        initializeLevel();
    }

    public void gameOver() {
        gameState = GameState.GAME_OVER;
        soundManager.playSound("game_over.wav");
    }

    public void handleInput(InputSignal inputSignal) {
        if (gameState.equals(GameState.PLAYING)) {
            switch (inputSignal) {
                case MOVE_LEFT -> paddle.moveLeft();
                case MOVE_RIGHT -> paddle.moveRight();
                case STOP -> paddle.stop();
                case PAUSE_RESUME -> gameState = GameState.PAUSED;
            }
        } else if (gameState.equals(GameState.PAUSED) && inputSignal.equals(InputSignal.PAUSE_RESUME)) {
            gameState = GameState.PLAYING;
        }
    }

    public void addExtraLife() {
        lives++;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }

    public List<Blink> getBlinks() {
        return blinks;
    }

    public List<Destroy> getDestroys() {
        return destroys;
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public List<Ball> getBalls() {
        return balls;
    }

    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState state) {
        this.gameState = state;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public void addLife() {
        lives++;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public boolean isBallReleased() {
        return ballReleased;
    }
}


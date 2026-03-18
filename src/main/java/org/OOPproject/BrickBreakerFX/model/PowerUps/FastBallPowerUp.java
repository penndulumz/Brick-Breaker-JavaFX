package org.OOPproject.BrickBreakerFX.model.PowerUps;

import org.OOPproject.BrickBreakerFX.model.Ball;
import org.OOPproject.BrickBreakerFX.model.Paddle;

public class FastBallPowerUp extends PowerUp {
    private Ball ball;

    public FastBallPowerUp(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.type = PowerUpTypes.FAST_BALL;
        this.duration = 300; // Duration in frames (5 seconds at 60 FPS)
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }

    @Override
    public void applyEffect(Paddle paddle) {
        if (ball != null) {
            ball.increaseSpeed();
        }
    }

    @Override
    public void removeEffect(Paddle paddle) {
        if (ball != null) {
            ball.normalSpeed();
        }
    }
}


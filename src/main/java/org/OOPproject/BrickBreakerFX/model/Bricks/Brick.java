package org.OOPproject.BrickBreakerFX.model.Bricks;

import org.OOPproject.BrickBreakerFX.model.GameObject;

public abstract class Brick extends GameObject {
    protected int hitPoints;
    protected BrickType type;
    protected int scoreValue;

    public Brick(int x, int y, int width, int height, BrickType type) {
        super(x, y, width, height);
        this.type = type;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public BrickType getType() {
        return type;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public void takeHit() {
        hitPoints--;
    }

    public boolean isDestroyed() {
        return hitPoints <= 0;
    }
}


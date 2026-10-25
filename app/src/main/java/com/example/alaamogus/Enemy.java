package com.example.alaamogus;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class Enemy {
    private float x, y;
    private float width, height;
    private float distanceFromPlayer = 400;
    private float catchDistance = 150;

    private int currentFrame = 0;
    private long frameTimer = 0;
    private long frameDelay = 100;
    private static final int FRAME_COUNT = 4;

    public Enemy(float screenWidth, float screenHeight) {
        this.width = screenWidth * 0.16f;

        if (Assets.enemySheet != null) {
            int frameW = Assets.enemySheet.getWidth() / FRAME_COUNT;
            int frameH = Assets.enemySheet.getHeight();
            if (frameW > 0 && frameH > 0) {
                this.height = this.width * frameH / frameW;
            } else {
                this.height = this.width;
            }
        } else {
            this.height = this.width;
        }

        this.x = 150 - distanceFromPlayer;
        this.y = screenHeight - 150 - height;
    }

    public void update(float playerX, float gameSpeed) {
        x += gameSpeed;

        if (playerX < 100) {
            x += gameSpeed * 0.5f;
        }

        if (x > playerX - distanceFromPlayer + 20) {
            x = playerX - distanceFromPlayer + 20;
        }
        if (x < playerX - distanceFromPlayer - 20) {
            x = playerX - distanceFromPlayer - 20;
        }

        frameTimer += 16;
        if (frameTimer >= frameDelay) {
            currentFrame = (currentFrame + 1) % FRAME_COUNT;
            frameTimer = 0;
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        if (Assets.enemySheet != null) {
            paint.setFilterBitmap(true);

            int frameWidth = Assets.enemySheet.getWidth() / FRAME_COUNT;
            Rect src = new Rect(
                    currentFrame * frameWidth, 0,
                    (currentFrame + 1) * frameWidth, Assets.enemySheet.getHeight()
            );
            RectF dest = new RectF(x, y, x + width, y + height);
            canvas.drawBitmap(Assets.enemySheet, src, dest, paint);
        } else {
            paint.setColor(android.graphics.Color.rgb(150, 0, 50));
            canvas.drawRect(x, y, x + width, y + height, paint);
        }
    }

    public boolean isCatchingPlayer(float playerX) {
        return x > playerX - catchDistance;
    }

    public void reset(float groundY) {
        x = 150 - distanceFromPlayer;
        this.y = groundY - height;
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public void setY(float y) { this.y = y; }

    public float getWidth() { return width; }

    public float getHeight() { return height; }

    public void setCatchDistance(float distance) {
        this.catchDistance = distance;
    }
}
package com.example.alaamogus;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class Player {
    private float x, y;
    private float width, height;
    private float velocityY = 0;
    private float gravity = 1.0f;
    private float jumpStrength = -25f;

    private int jumpCount = 0;
    private static final int MAX_JUMPS = 2;

    private boolean isJumping = false;
    private boolean isOnGround = false;
    private long jumpStartTime = 0;
    private float minJumpVelocity = -12f;
    private boolean shouldLimitJump = false;

    private int currentFrame = 0;
    private long frameTimer = 0;
    private long frameDelay = 100;
    private static final int FRAME_COUNT = 4;

    private float screenWidth, screenHeight;
    private float targetX = 150;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        this.width = 100;
        this.height = 100;
        this.jumpCount = 0;
        this.isOnGround = false;
    }

    public void setScreenSize(float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.targetX = screenWidth * 0.15f;
    }

    public void update() {
        x = targetX;

        velocityY += gravity;
        y += velocityY;

        if (shouldLimitJump && velocityY < minJumpVelocity) {
            velocityY = minJumpVelocity;
        }

        frameTimer += 16;
        if (frameTimer >= frameDelay) {
            currentFrame = (currentFrame + 1) % FRAME_COUNT;
            frameTimer = 0;
        }
    }

    public void startJump() {
        if (jumpCount < MAX_JUMPS) {
            velocityY = jumpStrength;
            jumpCount++;
            isJumping = true;
            isOnGround = false;
            jumpStartTime = System.currentTimeMillis();
            shouldLimitJump = false;
        }
    }

    public void endJump() {
        if (isJumping) {
            long jumpDuration = System.currentTimeMillis() - jumpStartTime;
            if (jumpDuration < 150) {
                shouldLimitJump = true;
                velocityY = minJumpVelocity;
            }
            isJumping = false;
        }
    }

    public boolean jump() {
        if (jumpCount < MAX_JUMPS) {
            velocityY = jumpStrength;
            jumpCount++;
            isOnGround = false;
            isJumping = true;
            jumpStartTime = System.currentTimeMillis();
            shouldLimitJump = false;
            return true;
        }
        return false;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void fitToSprite() {
        if (Assets.playerSheet != null) {
            int frameW = Assets.playerSheet.getWidth() / FRAME_COUNT;
            int frameH = Assets.playerSheet.getHeight();
            if (frameW > 0 && frameH > 0) {
                this.height = this.width * frameH / frameW;
            }
        }
    }

    public void land() {
        jumpCount = 0;
        isOnGround = true;
        isJumping = false;
        shouldLimitJump = false;
        velocityY = 0;
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setFilterBitmap(true);
        if (Assets.playerSheet != null) {
            int frameWidth = Assets.playerSheet.getWidth() / FRAME_COUNT;
            Rect src = new Rect(
                    currentFrame * frameWidth, 0,
                    (currentFrame + 1) * frameWidth, Assets.playerSheet.getHeight()
            );
            RectF dest = new RectF(x, y, x + width, y + height);
            canvas.drawBitmap(Assets.playerSheet, src, dest, paint);
        } else {
            paint.setColor(android.graphics.Color.rgb(0, 100, 200));
            canvas.drawRect(x, y, x + width, y + height, paint);
        }
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public float getVelocityY() { return velocityY; }
    public void setVelocityY(float velocityY) { this.velocityY = velocityY; }

    public void setOnGround(boolean onGround) { isOnGround = onGround; }
    public boolean isOnGround() { return isOnGround; }

    public Rect getBounds() {
        int marginX = (int)(width * 0.22f);
        int marginY = (int)(height * 0.08f);
        return new Rect(
                (int)x + marginX,
                (int)y + marginY,
                (int)(x + width) - marginX,
                (int)(y + height) - marginY
        );
    }

    public boolean isFallenOffScreen() {
        return y > screenHeight + 100;
    }

    public int getJumpCount() { return jumpCount; }
    public boolean canJump() { return jumpCount < MAX_JUMPS; }
}
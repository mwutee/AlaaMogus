package com.example.alaamogus;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import java.util.Random;

public class Platform {
    private float x, y;
    private float width, height;

    public Platform(float screenWidth, float screenHeight, Random random, float minY, float maxY) {
        this.width = 150 + random.nextInt(100);
        this.height = 40;
        this.x = screenWidth;
        this.y = minY + random.nextInt((int)(maxY - minY));
    }

    public Platform(float screenWidth, float screenHeight, Random random) {
        this(screenWidth, screenHeight, random, screenHeight - 300, screenHeight - 100);
    }

    public void update(float speed) {
        x -= speed;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (Assets.platform != null) {
            canvas.drawBitmap(Assets.platform, x, y, paint);
        } else {
            paint.setColor(Color.rgb(100, 150, 100));
            canvas.drawRect(x, y, x + width, y + height, paint);
        }
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }

    public Rect getBounds() {
        return new Rect((int)x, (int)y, (int)(x + width), (int)(y + height));
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setWidth(float width) { this.width = width; }
}
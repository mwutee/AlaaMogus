package com.example.alaamogus;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import java.util.Random;

public class Obstacle {
    private float x, y;
    private float width, height;
    private boolean passed = false;

    public Obstacle(float screenWidth, float groundY, Random random) {
        this.width = screenWidth * 0.045f + random.nextInt((int)(screenWidth * 0.03f));
        this.height = groundY * 0.10f + random.nextInt((int)(groundY * 0.08f));
        this.x = screenWidth;
        this.y = groundY - height;
    }

    public void update(float speed) {
        x -= speed;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (Assets.obstacle != null) {
            paint.setFilterBitmap(true);
            RectF dest = new RectF(x, y, x + width, y + height);
            canvas.drawBitmap(Assets.obstacle, null, dest, paint);
        } else {
            paint.setColor(Color.rgb(80, 60, 50));
            canvas.drawRect(x, y, x + width, y + height, paint);

            paint.setColor(Color.rgb(55, 40, 32));
            canvas.drawRect(x, y, x + width, y + height * 0.1f, paint);

            paint.setColor(Color.rgb(100, 80, 65));
            float margin = width * 0.08f;
            canvas.drawRect(x + margin, y + height * 0.15f,
                    x + width - margin, y + height - margin, paint);
        }
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }

    public Rect getBounds() {
        int marginX = (int)(width * 0.15f);
        int marginY = (int)(height * 0.10f);
        return new Rect(
                (int)x + marginX,
                (int)y + marginY,
                (int)(x + width) - marginX,
                (int)(y + height) - marginY
        );
    }

    public float getX() { return x; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
}
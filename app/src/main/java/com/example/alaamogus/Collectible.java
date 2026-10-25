package com.example.alaamogus;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Collectible {
    private float x, y;
    private float size = 32;
    private boolean collected = false;

    public Collectible(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update() {}

    public void draw(Canvas canvas, Paint paint) {
        if (!collected && Assets.collectible != null) {
            canvas.drawBitmap(Assets.collectible, x, y, paint);
        }
    }

    public Rect getBounds() {
        return new Rect((int)x, (int)y, (int)(x + size), (int)(y + size));
    }

    public boolean isCollected() { return collected; }
    public void setCollected(boolean collected) { this.collected = collected; }
}
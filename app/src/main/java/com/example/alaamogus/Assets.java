package com.example.alaamogus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class Assets {
    public static Bitmap playerSheet;
    public static Bitmap enemySheet;
    public static Bitmap obstacle;
    public static Bitmap background;
    public static Bitmap platform;
    public static Bitmap collectible;
    public static Bitmap moon;

    public static void load(Context context) {
        if (context == null) return;

        try {
            background = BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.background);

            playerSheet = BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.player);
            if (playerSheet != null) {
                playerSheet = cleanBackground(playerSheet);
                playerSheet = trimVertical(playerSheet);
            }

            enemySheet = BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.enemy);
            if (enemySheet != null) {
                enemySheet = cleanBackground(enemySheet);
                enemySheet = trimVertical(enemySheet);
            }

            int obstacleRes = context.getResources().getIdentifier(
                    "obstacle", "drawable", context.getPackageName());
            if (obstacleRes != 0) {
                obstacle = BitmapFactory.decodeResource(
                        context.getResources(), obstacleRes);
                obstacle = cleanBackground(obstacle);
            }

            platform = createPlaceholder(150, 40, 100, 150, 100);
            collectible = createPlaceholder(32, 32, 255, 215, 0);
            moon = createPlaceholder(100, 100, 200, 200, 200);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Bitmap trimVertical(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int minTop = h;
        int maxBottom = -1;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (Color.alpha(bmp.getPixel(x, y)) != 0) {
                    if (y < minTop) minTop = y;
                    if (y > maxBottom) maxBottom = y;
                }
            }
        }

        if (maxBottom < 0) return bmp;
        if (minTop == 0 && maxBottom == h - 1) return bmp;

        return Bitmap.createBitmap(bmp, 0, minTop, w, maxBottom - minTop + 1);
    }

    private static Bitmap cleanBackground(Bitmap bmp) {
        Bitmap work = bmp.copy(Bitmap.Config.ARGB_8888, true);
        int corner = work.getPixel(0, 0);

        if (Color.alpha(corner) == 0) {
            return work;
        }

        if (isMagenta(corner)) {
            for (int x = 0; x < work.getWidth(); x++) {
                for (int y = 0; y < work.getHeight(); y++) {
                    int c = work.getPixel(x, y);
                    if (isMagenta(c)) {
                        work.setPixel(x, y, Color.TRANSPARENT);
                    }
                }
            }

            for (int x = 1; x < work.getWidth() - 1; x++) {
                for (int y = 1; y < work.getHeight() - 1; y++) {
                    int c = work.getPixel(x, y);
                    if (Color.alpha(c) == 0) continue;

                    int r = Color.red(c);
                    int g = Color.green(c);
                    int b = Color.blue(c);

                    if (r > 80 && b > 80 && (r - g) > 20 && (b - g) > 20) {
                        if (isNeighborTransparent(work, x, y)) {
                            work.setPixel(x, y, Color.TRANSPARENT);
                        }
                    }
                }
            }
        }
        return work;
    }

    private static boolean isMagenta(int c) {
        int r = Color.red(c);
        int g = Color.green(c);
        int b = Color.blue(c);
        return r > 80 && b > 80 && (r - g) > 20 && (b - g) > 20;
    }

    private static boolean isNeighborTransparent(Bitmap bmp, int x, int y) {
        return Color.alpha(bmp.getPixel(x - 1, y)) == 0
                || Color.alpha(bmp.getPixel(x + 1, y)) == 0
                || Color.alpha(bmp.getPixel(x, y - 1)) == 0
                || Color.alpha(bmp.getPixel(x, y + 1)) == 0;
    }

    private static Bitmap createPlaceholder(int width, int height, int r, int g, int b) {
        try {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            int[] pixels = new int[width * height];
            int color = Color.rgb(r, g, b);
            for (int i = 0; i < pixels.length; i++) {
                pixels[i] = color;
            }
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
}
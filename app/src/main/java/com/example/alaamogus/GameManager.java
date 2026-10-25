package com.example.alaamogus;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import java.util.ArrayList;
import java.util.Random;

public class GameManager {
    private Player player;
    private Enemy enemy;
    private ArrayList<Obstacle> obstacles;

    private int score = 0;
    private int highScore = 0;
    private boolean isGameOver = false;
    private boolean isGameStarted = false;
    private boolean isPaused = false;
    private int deathReason = 0;

    private float gameSpeed = 8f;
    private float screenWidth, screenHeight;
    private float groundY;
    private Random random;

    private float spawnTimer = 600;
    private long scoreTimer = 0;
    private float obstacleSpeed = 2.2f;

    private Paint skyPaint;
    private Paint textPaint;

    public GameManager(float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.random = new Random();
        this.groundY = screenHeight - 150;

        obstacles = new ArrayList<>();

        skyPaint = new Paint();
        skyPaint.setColor(Color.rgb(20, 20, 50));

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setAntiAlias(true);

        player = new Player(150, 0);
        player.setScreenSize(screenWidth, screenHeight);
        player.setWidth(screenWidth * 0.13f);
        player.fitToSprite();
        player.setY(groundY - player.getHeight());
        player.setOnGround(true);

        enemy = new Enemy(screenWidth, screenHeight);
        enemy.setY(groundY - enemy.getHeight());
    }

    public void update() {
        if (!isGameStarted || isGameOver || isPaused) return;

        try {
            player.update();

            if (player.getY() + player.getHeight() > groundY) {
                player.setY(groundY - player.getHeight());
                if (player.getVelocityY() > 0) {
                    player.land();
                }
            }

            enemy.setY(groundY - enemy.getHeight());

            spawnTimer -= gameSpeed;
            if (spawnTimer <= 0) {
                obstacles.add(new Obstacle(screenWidth, groundY, random));
                spawnTimer = 400 + random.nextInt(500);
            }

            ArrayList<Obstacle> copy = new ArrayList<>(obstacles);
            for (Obstacle o : copy) {
                o.update(gameSpeed * obstacleSpeed);

                if (Rect.intersects(player.getBounds(), o.getBounds())) {
                    isGameOver = true;
                    deathReason = 1;
                    if (score > highScore) highScore = score;
                }

                if (o.isOffScreen()) {
                    obstacles.remove(o);
                }
            }

            scoreTimer += 16;
            if (scoreTimer >= 100) {
                score += 1;
                scoreTimer -= 100;
            }

            enemy.update(player.getX(), gameSpeed);
            if (enemy.isCatchingPlayer(player.getX())) {
                isGameOver = true;
                deathReason = 2;
                if (score > highScore) highScore = score;
            }

            gameSpeed += 0.005f;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Canvas canvas) {
        if (canvas == null) return;

        try {
            if (Assets.background != null) {
                float scale = Math.max(
                        screenWidth / Assets.background.getWidth(),
                        screenHeight / Assets.background.getHeight());
                float scaledWidth = Assets.background.getWidth() * scale;
                float scaledHeight = Assets.background.getHeight() * scale;
                float left = (screenWidth - scaledWidth) / 2;
                float top = (screenHeight - scaledHeight) / 2;
                canvas.drawBitmap(Assets.background, null,
                        new RectF(left, top, left + scaledWidth, top + scaledHeight), null);
            } else {
                canvas.drawRect(0, 0, screenWidth, screenHeight, skyPaint);
            }

            Paint groundPaint = new Paint();
            groundPaint.setColor(Color.rgb(40, 30, 55));
            canvas.drawRect(0, groundY, screenWidth, screenHeight, groundPaint);
            Paint edgePaint = new Paint();
            edgePaint.setColor(Color.rgb(110, 85, 150));
            canvas.drawRect(0, groundY, screenWidth, groundY + 6, edgePaint);

            for (Obstacle o : obstacles) {
                o.draw(canvas, new Paint());
            }

            if (player != null) player.draw(canvas, new Paint());
            if (enemy != null) enemy.draw(canvas, new Paint());

            textPaint.setColor(Color.WHITE);
            canvas.drawText("Счёт: " + score, 20, 50, textPaint);
            canvas.drawText("Рекорд: " + highScore, 20, 100, textPaint);

            if (!isGameStarted) {
                drawStartScreen(canvas);
            } else if (isGameOver) {
                drawGameOverScreen(canvas);
            } else if (isPaused) {
                drawPauseMenu(canvas);
            } else {
                Paint pauseBtnPaint = new Paint();
                pauseBtnPaint.setColor(Color.argb(150, 0, 0, 0));
                canvas.drawRect(screenWidth - 130, 30, screenWidth - 30, 130, pauseBtnPaint);
                pauseBtnPaint.setColor(Color.WHITE);
                pauseBtnPaint.setTextSize(60);
                pauseBtnPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("⏸", screenWidth - 80, 95, pauseBtnPaint);
                pauseBtnPaint.setTextAlign(Paint.Align.LEFT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawStartScreen(Canvas canvas) {
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.argb(220, 0, 0, 0));
        canvas.drawRect(0, screenHeight/2 - 150, screenWidth, screenHeight/2 + 150, bgPaint);

        textPaint.setColor(Color.rgb(100, 200, 255));
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("АЛАА МОГУС", screenWidth/2, screenHeight/2 - 50, textPaint);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(35);
        canvas.drawText("Беги от духа!", screenWidth/2, screenHeight/2, textPaint);
        canvas.drawText("Тап - прыжок через препятствия", screenWidth/2, screenHeight/2 + 50, textPaint);
        canvas.drawText("Тапни для старта", screenWidth/2, screenHeight/2 + 100, textPaint);
        textPaint.setTextAlign(Paint.Align.LEFT);
    }

    private void drawGameOverScreen(Canvas canvas) {
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.argb(220, 50, 0, 0));
        canvas.drawRect(0, screenHeight/2 - 150, screenWidth, screenHeight/2 + 150, bgPaint);

        textPaint.setColor(Color.RED);
        textPaint.setTextSize(60);
        textPaint.setTextAlign(Paint.Align.CENTER);

        if (deathReason == 1) {
            canvas.drawText("ВРЕЗАЛСЯ В ПРЕПЯТСТВИЕ!", screenWidth/2, screenHeight/2 - 30, textPaint);
        } else {
            canvas.drawText("АЛАА МОГУС ДОГНАЛ!", screenWidth/2, screenHeight/2 - 30, textPaint);
        }

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        canvas.drawText("Счёт: " + score, screenWidth/2, screenHeight/2 + 30, textPaint);
        canvas.drawText("Тапни для рестарта", screenWidth/2, screenHeight/2 + 80, textPaint);
        textPaint.setTextAlign(Paint.Align.LEFT);
    }

    private void drawPauseMenu(Canvas canvas) {
        Paint overlayPaint = new Paint();
        overlayPaint.setColor(Color.argb(180, 0, 0, 0));
        canvas.drawRect(0, 0, screenWidth, screenHeight, overlayPaint);

        float btnWidth = screenWidth * 0.6f;
        float btnHeight = screenHeight * 0.12f;
        float startX = (screenWidth - btnWidth) / 2;

        float yContinue = (screenHeight / 2f) - (btnHeight * 2.2f);
        float yRestart  = (screenHeight / 2f) - (btnHeight * 0.7f);
        float yMenu     = (screenHeight / 2f) + (btnHeight * 0.8f);

        drawButton(canvas, startX, yContinue, btnWidth, btnHeight, "ПРОДОЛЖИТЬ", Color.rgb(75, 0, 130));
        drawButton(canvas, startX, yRestart, btnWidth, btnHeight, "НОВАЯ ИГРА", Color.rgb(48, 25, 52));
        drawButton(canvas, startX, yMenu, btnWidth, btnHeight, "ГЛАВНОЕ МЕНЮ", Color.rgb(48, 25, 52));
    }

    private void drawButton(Canvas canvas, float x, float y, float w, float h, String text, int color) {
        Paint btnPaint = new Paint();
        btnPaint.setAntiAlias(true);

        btnPaint.setColor(color);
        RectF rect = new RectF(x, y, x + w, y + h);
        canvas.drawRoundRect(rect, 25, 25, btnPaint);

        btnPaint.setColor(Color.WHITE);
        btnPaint.setStyle(Paint.Style.STROKE);
        btnPaint.setStrokeWidth(4);
        canvas.drawRoundRect(rect, 25, 25, btnPaint);
        btnPaint.setStyle(Paint.Style.FILL);

        btnPaint.setColor(Color.WHITE);
        btnPaint.setTextSize(h * 0.4f);
        btnPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(text, x + (w / 2f), y + (h / 2f) + (h * 0.12f), btnPaint);
        btnPaint.setTextAlign(Paint.Align.LEFT);
    }

    public void startJump() {
        try {
            if (!isGameStarted) {
                startGame();
            } else if (isGameOver) {
                restart();
            } else {
                player.startJump();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleTap() {
        try {
            if (!isGameStarted) {
                startGame();
            } else if (isGameOver) {
                restart();
            } else {
                player.jump();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startGame() {
        isGameStarted = true;
        isGameOver = false;
        isPaused = false;
        score = 0;
        gameSpeed = 8f;
        deathReason = 0;
        obstacles.clear();
        spawnTimer = 600;
        scoreTimer = 0;
        enemy.reset(groundY);
    }

    private void restart() {
        isGameOver = false;
        isPaused = false;
        score = 0;
        gameSpeed = 8f;
        deathReason = 0;
        obstacles.clear();
        spawnTimer = 600;
        scoreTimer = 0;
        enemy.reset(groundY);

        player = new Player(150, 0);
        player.setScreenSize(screenWidth, screenHeight);
        player.setWidth(screenWidth * 0.13f);
        player.fitToSprite();
        player.setY(groundY - player.getHeight());
        player.setOnGround(true);
    }

    public void endJump() {
        if (player != null && isGameStarted && !isGameOver && !isPaused) {
            player.endJump();
        }
    }

    public void setScreenSize(float w, float h) {
        this.screenWidth = w;
        this.screenHeight = h;
        this.groundY = h - 150;
        if (player != null) {
            player.setScreenSize(w, h);
            player.setWidth(w * 0.13f);
            player.fitToSprite();
            player.setY(groundY - player.getHeight());
        }
        if (enemy != null) {
            enemy.setY(groundY - enemy.getHeight());
        }
    }

    public void togglePause() {
        isPaused = !isPaused;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean handlePauseTouch(float x, float y) {
        float btnWidth = screenWidth * 0.6f;
        float btnHeight = screenHeight * 0.12f;
        float startX = (screenWidth - btnWidth) / 2;

        float yContinue = (screenHeight / 2f) - (btnHeight * 2.2f);
        float yRestart  = (screenHeight / 2f) - (btnHeight * 0.7f);
        float yMenu     = (screenHeight / 2f) + (btnHeight * 0.8f);

        if (x >= startX && x <= startX + btnWidth) {
            if (y >= yContinue && y <= yContinue + btnHeight) {
                isPaused = false;
                return true;
            }
            if (y >= yRestart && y <= yRestart + btnHeight) {
                restart();
                return true;
            }
            if (y >= yMenu && y <= yMenu + btnHeight) {
                isGameStarted = false;
                isPaused = false;
                score = 0;
                return true;
            }
        }
        return false;
    }

    public boolean isGameStarted() { return isGameStarted; }
    public boolean isGameOver() { return isGameOver; }
}
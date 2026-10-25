package com.example.alaamogus;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread thread;
    private GameManager gameManager;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        Assets.load(context);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        gameManager = new GameManager(getWidth(), getHeight());
        thread = new GameThread(holder, gameManager);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (gameManager != null) {
            gameManager.setScreenSize(width, height);
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (thread != null) {
            thread.setRunning(false);
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (gameManager == null) return true;

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (gameManager.isPaused()) {
                    gameManager.handlePauseTouch(x, y);
                    return true;
                }
                if (gameManager.isGameStarted() && !gameManager.isGameOver()) {
                    if (x > getWidth() - 140 && y < 140) {
                        gameManager.togglePause();
                        return true;
                    }
                }
                if (gameManager.isGameStarted() && !gameManager.isGameOver()) {
                    gameManager.startJump();
                } else {
                    gameManager.handleTap();
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (gameManager.isGameStarted() && !gameManager.isGameOver() && !gameManager.isPaused()) {
                    gameManager.endJump();
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public void pause() {
        if (thread != null) thread.setRunning(false);
    }

    public void resume() {
        if (thread != null) {
            thread.setRunning(true);
            thread = new GameThread(getHolder(), gameManager);
            thread.start();
        }
    }
}
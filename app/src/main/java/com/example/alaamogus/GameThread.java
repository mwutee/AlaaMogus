package com.example.alaamogus;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    private SurfaceHolder holder;
    private GameManager gameManager;
    private boolean isRunning = false;

    public GameThread(SurfaceHolder holder, GameManager gameManager) {
        this.holder = holder;
        this.gameManager = gameManager;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    @Override
    public void run() {
        while (isRunning) {
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null && gameManager != null) {
                    gameManager.update();
                    gameManager.draw(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null && holder != null) {
                    try {
                        holder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                sleep(16);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
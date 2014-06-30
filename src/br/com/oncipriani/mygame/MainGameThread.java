/*
 * Copyright $date.year Otavio Nery Cipriani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.oncipriani.mygame;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import br.com.oncipriani.mygame.engine.GameRunningState;
import br.com.oncipriani.mygame.engine.GameStateManager;

/**
 * This class is responsible for updating the game logic and drawing the game
 * on the screen provided by {@link br.com.oncipriani.mygame.MainGameView}.
 *
 * @see android.view.SurfaceView
 */
public final class MainGameThread extends Thread {
    private static final String TAG = MainGameThread.class.getSimpleName();

    // Constants defining the various game states
    public static final int STATE_STARTING = 1;
    public static final int STATE_RUNNING = 2;
    public static final int STATE_EXITING = 3;

    // Constants for controlling the game's FPS
    private static final int MAX_FPS = 50;
    private static final int FRAME_PERIOD = 1000 / MAX_FPS;
    private static final int MAX_FRAME_SKIPS = 5;

    // Objects for controlling our game's states
    private final GameStateManager gameStateManager = new GameStateManager();

    // Handlers providing access to some important stuff
    private final SurfaceHolder surfaceHolder;
    private final Resources resources;

    // Determines if this thread should keep running or not
    private boolean keepRunning = false;

    // The screen size
    private int screenWidth;
    private int screenHeight;

    /**
     * Constructs a new main game thread.
     *
     * @param surfaceHolder The SurfaceHolder providing access and control over the screen.
     * @param resources     Handler providing access to our game's resource files.
     */
    public MainGameThread(SurfaceHolder surfaceHolder, Resources resources) {
        super();

        Log.d(TAG, "Constructing the main game thread");

        this.surfaceHolder = surfaceHolder;
        this.resources = resources;
    }

    /**
     * Signals the game thread to stop, effectively exiting the game.
     */
    public synchronized void quitGame() {
        keepRunning = false;
    }

    @Override
    public void run() {
        if (!keepRunning) {
            throw new RuntimeException("The main game thread was started without being initialized!");
        }

        // Initialize our game's states
        gameStateManager.push(new GameRunningState(gameStateManager, resources, screenWidth, screenHeight));

        // The canvas for the screen
        Canvas canvas;

        // Variable for controlling the game update speed
        long beginTime, timeDiff, sleepTime;
        int framesSkipped;

        Log.d(TAG, "Starting game thread loop");
        while (keepRunning) {
            // Lock the screen for editing
            canvas = surfaceHolder.lockCanvas();

            // If the lock was successful, update and draw the game on screen
            if (canvas != null) {
                beginTime = System.currentTimeMillis();
                framesSkipped = 0;

                gameStateManager.update();
                gameStateManager.draw(canvas);

                // Calculate how long did the cycle take and the sleep time
                timeDiff = System.currentTimeMillis() - beginTime;
                sleepTime = FRAME_PERIOD - timeDiff;

                if (sleepTime > 0) {
                    // If sleepTime > 0 we're OK and we can rest for a while...
                    try {
                        MainGameThread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "Thread interrupted while sleeping", e);
                    }
                }

                while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                    // We need to catch up! Update without rendering
                    gameStateManager.update();

                    // Pretend we spent time drawing
                    sleepTime += FRAME_PERIOD;
                    framesSkipped++;
                }

                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
        Log.d(TAG, "Game thread loop ended");
    }

    /**
     * Informs the rendering thread that the surface changed its format or size.
     * This method <b>must</b> be called at least once before starting the main game thread.
     *
     * @param width  The new screen width in pixels.
     * @param height The new screen height in pixels.
     */
    public synchronized void handleSurfaceChanged(int width, int height) {
        // TODO: Correctly handle surface changes
        screenHeight = height;
        screenWidth = width;

        keepRunning = true;
    }

    /**
     * Handles {@link android.view.MotionEvent}.ACTION_DOWN events.
     *
     * @param eventX The location of the touch on the X axis.
     * @param eventY The location of the touch on the Y axis.
     */
    public void handleActionDown(int eventX, int eventY) {
        gameStateManager.handleActionDown(eventX, eventY);
    }
}

/*
 * Copyright 2014 Otavio Nery Cipriani
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

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * This class represents the screen where we draw our game. Since every draw
 * and game update operation should not take place int the UI thread, this
 * class also creates and starts the {@link MainGameThread} and notifies if
 * of all the touch events received.
 */
public class MainGameView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = MainGameView.class.getSimpleName();

    // The thread responsible for rendering on the screen and much more
    private final MainGameThread gameThread;

    // The maximum supported screen resolution
    public static final int MAX_WIDTH = 720;
    public static final int MAX_HEIGHT = 1280;

    public MainGameView(Context context) {
        super(context);

        final SurfaceHolder surfaceHolder = getHolder();
        assert (surfaceHolder != null) : "SurfaceHolder was not provided!";

        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
        surfaceHolder.addCallback(this);

        gameThread = new MainGameThread(surfaceHolder, getResources());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "Surface created");

        // TODO: Use hardware scaling if the screen is too big

        //        Rect surfaceFrame = holder.getSurfaceFrame();
        //        if (surfaceFrame != null) {
        //            int surfaceWidth = Math.abs(surfaceFrame.width());
        //            int surfaceHeight = Math.abs(surfaceFrame.height());
        //
        //            if (surfaceWidth > MAX_WIDTH || surfaceHeight > MAX_HEIGHT) {
        //                // Since we only support portrait orientation, width is always smaller than height.
        //                final float aspectRatio = (float) surfaceHeight / (float) surfaceWidth;
        //                holder.setFixedSize(MAX_WIDTH, MAX_HEIGHT);
        //            }
        //        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "Surface changed");

        if (holder.isCreating()) {
            Log.d(TAG, "Surface is in the process of being created from Callback methods");

            // TODO: Maybe create the MainGameThread here to avoid having to call handleSurfaceChanged
            gameThread.handleSurfaceChanged(width, height);
            gameThread.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Surface is being destroyed");
        boolean retry = true;

        // Tell the game thread to shut down and wait for it to finish
        gameThread.quitGame();
        while (retry) {
            try {
                Log.d(TAG, "Waiting for the game thread to finish...");
                gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.d(TAG, "Thread interrupted while joining", e);
            }
        }
        Log.d(TAG, "Thread was shut down cleanly");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                gameThread.handleActionDown(Math.round(event.getX()), Math.round(event.getY()));
                return true;

            default:
                return false;
        }
    }
}

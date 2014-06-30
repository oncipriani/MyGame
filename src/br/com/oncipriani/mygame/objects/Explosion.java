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

package br.com.oncipriani.mygame.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import br.com.oncipriani.mygame.objects.components.Particle;

import java.util.Random;

/**
 * This class represents a collection of particles originating at a
 * single point that spread in random directions.
 */
public class Explosion {
    private static final String TAG = Explosion.class.getSimpleName();

    // Size limits for the explosion
    public static final int MIN_PARTICLES = 2;
    public static final int MAX_PARTICLES = 6;

    // The main components of the explosion
    private final Particle[] particles;
    private final int particleCount;

    // Members used to draw the particles
    private final Bitmap particleBitmap;
    private final Paint particlePaint;
    private final int particleBitmapHalfWidth;
    private final int particleBitmapHalfHeight;

    private boolean isVisible;

    /**
     * Creates a new Explosion object.
     *
     * @param bitmap The image used for the explosion's particles.
     * @param size   The number of particles of the explosion.
     * @param posX   The explosion's origin on the X axis.
     * @param posY   The explosion's origin on the Y axis.
     */
    public Explosion(Bitmap bitmap, int size, int posX, int posY) {
        if (size < MIN_PARTICLES) {
            Log.w(TAG, "Tried to create an explosion with less than the minimum number of particles!");
            particleCount = MIN_PARTICLES;
        } else if (size > MAX_PARTICLES) {
            Log.w(TAG, "Tried to create an explosion with more than the maximum number of particles!");
            particleCount = MAX_PARTICLES;
        } else {
            particleCount = size;
        }

        // Calculate the stepping to uniformly spread the particles
        final int angleStepping = 360 / particleCount;

        // We start with a random angle that will be incremented by "angleStepping"
        int angle = new Random().nextInt(360);

        particles = new Particle[particleCount];
        for (int i = 0; i < particleCount; i++) {
            particles[i] = new Particle(posX, posY, angle);
            angle += angleStepping;
        }

        // Create the Paint used to fade the particle's image
        particlePaint = new Paint();
        particlePaint.setColor(Color.WHITE);

        particleBitmap = bitmap;
        particleBitmapHalfWidth = bitmap.getWidth() / 2;
        particleBitmapHalfHeight = bitmap.getHeight() / 2;
    }

    /**
     * Returns <code>true</code> if the explosion is no longer visible,
     * that is, if there are no visible particles on screen.
     *
     * @return <code>true</code> if the explosion is no longer visible.
     */
    public boolean isVisible() {
        return isVisible;
    }

    public void update() {
        isVisible = false;

        for (int i = 0; i < particleCount; i++) {
            particles[i].update();
            isVisible = isVisible || particles[i].isVisible();
        }
    }

    public void draw(Canvas canvas) {
        for (int i = 0; i < particleCount; i++) {
            if (particles[i].isVisible()) {
                particlePaint.setAlpha(particles[i].getAlpha());
                canvas.drawBitmap(particleBitmap, particles[i].getPosX() - particleBitmapHalfWidth,
                        particles[i].getPosY() - particleBitmapHalfHeight, particlePaint);
            }
        }
    }
}

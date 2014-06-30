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

package br.com.oncipriani.mygame.objects.components;

import android.graphics.Color;

import java.util.Random;

/**
 * This class represents a single particle. May be used to create an explosion.
 */
public class Particle {
    // Rate at witch the particle fades away (alpha is decreased)
    private static final int FADING_RATE = 4;

    // Minimum and maximum particle's speed
    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 10;

    // Random shared across every particle in the game
    private static final Random RANDOM = new Random();

    // The particles attributes
    private final Movement movement;
    private int posX, posY, alpha;
    private boolean isVisible;

    /**
     * Constructs a new particle with random speed.
     *
     * @param posX  The particle's position on the X axis.
     * @param posY  The particle's position on the Y axis.
     * @param angle The angle representing the direction where the particle will go (in degrees).
     */
    public Particle(int posX, int posY, int angle) {
        this.isVisible = true;
        this.posX = posX;
        this.posY = posY;
        this.alpha = Color.alpha(Color.WHITE);

        movement = new Movement();
        movement.setMovement(RANDOM.nextInt((MAX_SPEED - MIN_SPEED) + 1) + MIN_SPEED, angle, 0, 0);
    }

    public boolean isVisible() {
        return isVisible;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getAlpha() {
        return alpha;
    }

    public void update() {
        if (isVisible) {
            alpha -= FADING_RATE;

            // If we reached full transparency, kill the particle
            if (alpha <= 0) {
                isVisible = false;
            } else {
                // If the particle is still visible, update its position
                posX += movement.speedX;
                posY += movement.speedY;
            }
        }
    }
}

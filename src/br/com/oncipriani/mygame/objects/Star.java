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
import android.util.Log;
import br.com.oncipriani.mygame.objects.components.Movement;

/**
 * This is a star that bounces every time it's clicked and still have
 * energy. The star's energy level decreases every time it's clicked.
 */
public class Star extends Actor {
    private static final String TAG = Star.class.getSimpleName();

    // Attribute limits for the star
    public static final int MIN_SPEED = 15;
    public static final int MAX_SPEED = 25;
    public static final int MIN_ENERGY = 2;
    public static final int MAX_ENERGY = 4;

    // The acceleration at which the star fall after being clicked
    private static final int GRAVITY = 2;

    // The star's attributes
    private Movement movement;
    private int energy;

    /**
     * Constructs a new star at the specified location. The star will falling at
     * the specified speed towards the indicated direction.
     *
     * @param bitmap The Bitmap representing the star's image.
     * @param posX   The star's position on the X axis.
     * @param posY   The star's position on the Y axis.
     * @param speed  The star's falling speed.
     * @param angle  Angle indicating the fall direction (in degrees).
     * @param energy The star's initial energy level.
     */
    public Star(Bitmap bitmap, int posX, int posY, int speed, int angle, int energy) {
        super(bitmap, posX, posY);

        if (speed > MAX_SPEED) {
            Log.w(TAG, "Tried to create a star faster than the maximum allowed speed!");
            speed = MAX_SPEED;
        } else if (speed < MIN_SPEED) {
            Log.w(TAG, "Tried to create a star slower than the minimum allowed speed!");
            speed = MIN_SPEED;
        }

        movement = new Movement();
        movement.setMovement(speed, angle, 0, 0);

        if (energy > MAX_ENERGY) {
            Log.w(TAG, "Tried to create a star with too much energy!");
            this.energy = MAX_ENERGY;
        } else if (energy < MIN_ENERGY) {
            Log.w(TAG, "Tried to create a star with too little energy!");
            this.energy = MIN_ENERGY;
        } else {
            this.energy = energy;
        }
    }

    public int getEnergy() {
        return energy;
    }

    @Override
    public void update() {
        posX += movement.speedX;

        if (movement.speedY < MAX_SPEED) movement.speedY += movement.accelerationY;
        posY += movement.speedY;
    }

    /**
     * Update the star's position while checking if it is going out of the
     * screen from the sides. Bounces if it is.
     *
     * @param screenWidth The screen's width in pixels.
     */
    public void update(int screenWidth) {
        update(); // Update the star's position

        // Bounce if it is going out of the screen from the sides
        if (posX + halfWidth > screenWidth) {
            posX = screenWidth;
            movement.speedX *= -1;
        } else if (posX - halfWidth < 0) {
            posX = 0;
            movement.speedX *= -1;
        }
    }

    @Override
    public boolean isActive(int screenWidth, int screenHeight) {
        // Since the stars fall, we do not check if it is above the screen
        return !(posX <= halfWidth * -1 || posX >= screenWidth + halfWidth || posY >= screenHeight + halfHeight);
    }

    /**
     * Informs the star of a touch event at the specified coordinates. If the event
     * happens on the bitmap surface then this method returns <code>true</code>.
     *
     * @param eventX The event's X coordinate.
     * @param eventY The event's Y coordinate.
     * @return <code>true</code> if the star was clicked. <code>false</code> otherwise.
     */
    public boolean handleActionDown(int eventX, int eventY) {
        // If the star is still recovering from the last bounce, do nothing
        if (movement.speedY < MIN_SPEED) return false;

        // Check if the star was clicked
        if (eventX >= (posX - halfWidth) && eventX <= (posX + halfWidth) &&
                eventY >= (posY - halfHeight) && eventY <= (posY + halfHeight)) {
            // Decrease the star's energy
            energy--;

            // Bounce if the star is still alive
            if (energy > 0) {
                movement.speedX *= -1;
                movement.speedY *= -1;

                // Gravity starts working now
                movement.accelerationY = GRAVITY;
            }

            // The user clicked on the star
            return true;
        }

        // The user did not click on the star
        return false;
    }
}

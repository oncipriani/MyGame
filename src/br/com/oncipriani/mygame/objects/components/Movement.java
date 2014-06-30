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

/**
 * This class keeps track of the bearing of an object in the 2D plane. It
 * holds the speed values on both axis and optionally an acceleration factor.
 * An object with the ability to move should contain this class and update
 * its position accordingly.
 */
public final class Movement {
    public int speedX;
    public int speedY;
    public int accelerationX;
    public int accelerationY;

    /**
     * Constructs a new Movement object that is initially stopped.
     */
    public Movement() {
        this.speedX = 0;
        this.speedY = 0;
        this.accelerationX = 0;
        this.accelerationY = 0;
    }

    /**
     * Constructs a new Movement object with the corresponding
     * starting speed and zero acceleration.
     *
     * @param speedX The speed on the X axis.
     * @param speedY The speed on the Y axis.
     */
    public Movement(int speedX, int speedY) {
        this.speedX = speedX;
        this.speedY = speedY;
        this.accelerationX = 0;
        this.accelerationY = 0;
    }

    /**
     * Constructs a new Movement object with the provided starting speed and acceleration.
     *
     * @param speedX        The initial speed on the X axis.
     * @param speedY        The initial speed on the Y axis.
     * @param accelerationX The acceleration on the X axis.
     * @param accelerationY The acceleration on the Y axis.
     */
    public Movement(int speedX, int speedY, int accelerationX, int accelerationY) {
        this.speedX = speedX;
        this.speedY = speedY;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
    }

    /**
     * Makes the object move with the specified speed and acceleration towards the
     * direction indicated by the provided angle (in degrees).
     *
     * @param speed         The speed of the object.
     * @param angle         Angle indicating the direction the object will be moving to (in degrees).
     * @param accelerationX The The acceleration on the X axis.
     * @param accelerationY The acceleration on the Y axis.
     */
    public void setMovement(int speed, int angle, int accelerationX, int accelerationY) {
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;

        // Calculate the X component of the speed
        final double radians = Math.toRadians(angle);
        this.speedX = (int) Math.round(speed * Math.cos(radians));

        // Calculate the Y component of the speed. Since the Y coordinates on the
        // screen are opposite of the traditional cartesian system (higher on the
        // device's screen means a lower Y value), we must compensate for that.
        this.speedY = (int) Math.round(speed * Math.sin(radians)) * -1;
    }
}

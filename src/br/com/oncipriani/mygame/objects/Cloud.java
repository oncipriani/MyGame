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
import br.com.oncipriani.mygame.objects.components.Movement;

/**
 * This is a cloud that floats in the background.
 */
public class Cloud extends Actor {
    // The types of cloud we have
    public static final int CLOUD_TYPE_SMALL = 0;
    public static final int CLOUD_TYPE_MEDIUM = 1;
    public static final int CLOUD_TYPE_LARGE = 2;

    public final int type;
    private final Movement movement;

    /**
     * Constructs a new Cloud object at the specified position and speed.
     *
     * @param bitmap The cloud's image.
     * @param posX   The cloud's position on the X axis.
     * @param posY   The cloud's position on the Y axis.
     * @param speedX The cloud's speed on the X axis.
     * @param type   The cloud's type.
     */
    public Cloud(Bitmap bitmap, int posX, int posY, int speedX, int type) {
        super(bitmap, posX, posY);

        this.movement = new Movement(speedX, 0);
        this.type = type;
    }

    /**
     * Checks if the provided type is a valid cloud type.
     *
     * @param type The type to check.
     * @return <code>true</code> if the provided type is a valid cloud type. <code>false</code> otherwise.
     */
    public static boolean isValidCloudType(int type) {
        switch (type) {
            case CLOUD_TYPE_SMALL:
            case CLOUD_TYPE_MEDIUM:
            case CLOUD_TYPE_LARGE:
                return true;
        }

        return false;
    }

    /**
     * Sets the cloud's speed on the X axis.
     *
     * @param speedX The cloud's speed on the X axis.
     */
    public void setSpeedX(int speedX) {
        movement.speedX = speedX;
    }

    @Override
    public void update() {
        // Clouds move only horizontally and have constant speed
        posX -= movement.speedX;
    }

    @Override
    public boolean isActive(int screenWidth, int screenHeight) {
        // Since clouds only move horizontally and from right to left, there is no
        // need to check the Y axis or the screen size (assuming they are created
        // somewhere inside the screen).
        return posX > (halfWidth * -1);
    }
}

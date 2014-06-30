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

package br.com.oncipriani.mygame.objects.factories;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import br.com.oncipriani.mygame.R;
import br.com.oncipriani.mygame.helpers.FisherYates;
import br.com.oncipriani.mygame.objects.Star;

import java.util.Random;

/**
 * This class provides methods for creating <code>Star</code> objects.
 */
public final class StarFactory {
    private static final Random RANDOM = new Random();

    // Placing grid size
    private static final int PLACING_GRID_SIZE = 10;

    // Array used to place the stars and its current index
    private static int[] starPlacingGrid;
    private static int starPlacingGridIndex;

    // The star bitmap and it's starting position on the Y axis
    private static Bitmap starBitmap = null;
    private static int starStartingPosY;

    /**
     * Creates a new Star object with random speed, energy and fall angle.
     * The star will be positioned just outside the top of the screen.
     *
     * @param screenWidth The screen's width in pixels.
     * @param resources   Handler providing access to our game's resources.
     * @return A new Star object.
     */
    public static Star createStar(Resources resources, int screenWidth) {
        int angle;

        // Check if we are being called for the first time
        if (starBitmap == null) initializeStars(resources, screenWidth);

        // Choose a new random position for the star using the positioning array
        final int posX = getRandomStarPosX();

        // If the star will be placed on the right side of the screen, make it fall towards
        // the left side of the screen. Make it fall towards the right side otherwise.
        if (posX < screenWidth / 2) {
            // The angle must be between 270 and 285 degrees
            angle = RANDOM.nextInt(285 - 270 + 1) + 270;
        } else {
            // The angle must be between 255 and 270 degrees
            angle = RANDOM.nextInt(270 - 255 + 1) + 255;
        }

        final int speed = RANDOM.nextInt((Star.MAX_SPEED - Star.MIN_SPEED) + 1) + Star.MIN_SPEED;
        final int energy = RANDOM.nextInt((Star.MAX_ENERGY - Star.MIN_ENERGY) + 1) + Star.MIN_ENERGY;

        return new Star(starBitmap, posX, starStartingPosY, speed, angle, energy);
    }

    /**
     * Loads the star's bitmap and places a single star on the screen.
     *
     * @param resources Handler providing access to our game's resources.
     */
    private static void initializeStars(Resources resources, int screenWidth) {
        // Load the bitmap for the stars
        starBitmap = BitmapFactory.decodeResource(resources, R.drawable.star_large);
        starStartingPosY = (starBitmap.getHeight() / 2) * -1;

        // Use the bitmap width as margin
        final int placingGridMargin = starBitmap.getWidth();

        // Create the grid used to position the stars on the screen
        starPlacingGrid = new int[PLACING_GRID_SIZE];
        starPlacingGridIndex = 0;

        // Initialize the grid using Fisher-Yates shuffle ("inside out" version).
        FisherYates.initialize(starPlacingGrid, placingGridMargin, screenWidth - placingGridMargin);
    }

    /**
     * Gets a random position from the star positioning array.
     *
     * @return A random position from the star positioning array.
     */
    private static int getRandomStarPosX() {
        // Check if it's time to reset the grid
        if (starPlacingGridIndex >= PLACING_GRID_SIZE) {
            FisherYates.shuffle(starPlacingGrid);
            starPlacingGridIndex = 0;
        }

        // Get the position
        final int pos = starPlacingGrid[starPlacingGridIndex];

        // Get ready for the next call
        starPlacingGridIndex++;

        return pos;
    }
}

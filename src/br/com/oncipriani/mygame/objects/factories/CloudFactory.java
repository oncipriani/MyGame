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
import br.com.oncipriani.mygame.objects.Cloud;

import java.util.Random;

/**
 * This class provides methods for creating and recycling <code>Cloud</code> objects.
 */
public final class CloudFactory {
    private static final Random RANDOM = new Random();

    // How many of each type of cloud we have
    private static final int SMALL_CLOUDS_COUNT = 2;
    private static final int MEDIUM_CLOUDS_COUNT = 2;
    private static final int LARGE_CLOUDS_COUNT = 2;

    // Minimum and maximum speed for each cloud type
    private static final int SMALL_CLOUD_MIN_SPEED = 1;
    private static final int SMALL_CLOUD_MAX_SPEED = 3;
    private static final int MEDIUM_CLOUD_MIN_SPEED = 3;
    private static final int MEDIUM_CLOUD_MAX_SPEED = 5;
    private static final int LARGE_CLOUD_MIN_SPEED = 5;
    private static final int LARGE_CLOUD_MAX_SPEED = 7;

    // Placing grids sizes
    private static final int PLACING_GRID_X_SIZE = 10;
    private static final int PLACING_GRID_Y_SIZE = 4;

    // Arrays used to place the clouds
    private static int[] cloudsPlacingGridX = null;
    private static int[] smallCloudsPlacingGridY;
    private static int[] mediumCloudsPlacingGridY;
    private static int[] largeCloudsPlacingGridY;

    // Indexes for each placing grid
    private static int cloudsPlacingGridIndexX;
    private static int smallCloudsPlacingGridIndex;
    private static int mediumCloudsPlacingGridIndex;
    private static int largeCloudsPlacingGridIndex;

    // Bitmaps for each cloud type
    private static Bitmap[] smallCloudBitmaps;
    private static Bitmap[] mediumCloudBitmaps;
    private static Bitmap[] largeCloudBitmaps;

    // Indexes for each bitmap type
    private static int smallCloudBitmapsIndex;
    private static int mediumCloudBitmapsIndex;
    private static int largeCloudBitmapsIndex;

    /**
     * Creates an array of <code>Cloud</code> objects with clouds of the specified type.
     *
     * @param type         The type of cloud that will be created.
     * @param count        The number of clouds that will be created.
     * @param screenWidth  The screen's width in pixels.
     * @param screenHeight The screen's height in pixels.
     * @param res          Handler providing access to our game's resources.
     * @return An array of <code>Cloud</code> objects with <code>count</code> objects.
     * @throws java.lang.IllegalArgumentException if <code>count</code> is <= 0.
     */
    public static Cloud[] createClouds(int type, int count, int screenWidth, int screenHeight, Resources res) {
        if (count <= 0) throw new IllegalArgumentException("Cannot create an array of 0 or less elements!");

        // Load the bitmaps and initialize the placing grids if necessary
        if (cloudsPlacingGridX == null) {
            // Create the grids and initialize their index pointers
            cloudsPlacingGridX = new int[PLACING_GRID_X_SIZE];
            smallCloudsPlacingGridY = new int[PLACING_GRID_Y_SIZE];
            mediumCloudsPlacingGridY = new int[PLACING_GRID_Y_SIZE];
            largeCloudsPlacingGridY = new int[PLACING_GRID_Y_SIZE];
            cloudsPlacingGridIndexX = 0;
            smallCloudsPlacingGridIndex = 0;
            mediumCloudsPlacingGridIndex = 0;
            largeCloudsPlacingGridIndex = 0;

            // Initialize the X axis placing grid
            FisherYates.initialize(cloudsPlacingGridX, 0, screenWidth);

            // Initialize the small clouds and layer
            int layerStartingPos = Math.round(screenHeight * 0.75f); // Small clouds start at 3/4 of the screen
            int layerHeight = loadCloudBitmaps(res, Cloud.CLOUD_TYPE_SMALL);
            FisherYates.initialize(smallCloudsPlacingGridY, layerStartingPos, layerStartingPos + layerHeight);

            // Initialize the medium clouds and layer
            layerStartingPos += layerHeight; // Medium clouds start where the small clouds end
            layerHeight = loadCloudBitmaps(res, Cloud.CLOUD_TYPE_MEDIUM);
            FisherYates.initialize(mediumCloudsPlacingGridY, layerStartingPos, layerHeight + layerStartingPos);

            // Initialize the medium clouds and layer
            layerStartingPos += layerHeight; // Large clouds start where the medium clouds end
            layerHeight = loadCloudBitmaps(res, Cloud.CLOUD_TYPE_LARGE);
            FisherYates.initialize(largeCloudsPlacingGridY, layerStartingPos, layerHeight + layerStartingPos);
        }

        Cloud[] clouds = new Cloud[count];

        // Create some clouds and place them on the screen using the grids
        switch (type) {
            case Cloud.CLOUD_TYPE_SMALL:
                for (int i = 0; i < count; i++) {
                    clouds[i] = new Cloud(getNextCloudBitmap(Cloud.CLOUD_TYPE_SMALL),
                            getRandomCloudPosX(), getRandomCloudPosY(Cloud.CLOUD_TYPE_SMALL),
                            RANDOM.nextInt((SMALL_CLOUD_MAX_SPEED - SMALL_CLOUD_MIN_SPEED) + 1) + SMALL_CLOUD_MIN_SPEED,
                            Cloud.CLOUD_TYPE_SMALL);
                }
                break;

            case Cloud.CLOUD_TYPE_MEDIUM:
                for (int i = 0; i < count; i++) {
                    clouds[i] = new Cloud(getNextCloudBitmap(Cloud.CLOUD_TYPE_MEDIUM),
                            getRandomCloudPosX(), getRandomCloudPosY(Cloud.CLOUD_TYPE_MEDIUM),
                            RANDOM.nextInt((MEDIUM_CLOUD_MAX_SPEED - MEDIUM_CLOUD_MIN_SPEED) + 1) + MEDIUM_CLOUD_MIN_SPEED,
                            Cloud.CLOUD_TYPE_MEDIUM);
                }
                break;

            case Cloud.CLOUD_TYPE_LARGE:
                for (int i = 0; i < count; i++) {
                    clouds[i] = new Cloud(getNextCloudBitmap(Cloud.CLOUD_TYPE_LARGE),
                            getRandomCloudPosX(), getRandomCloudPosY(Cloud.CLOUD_TYPE_LARGE),
                            RANDOM.nextInt((LARGE_CLOUD_MAX_SPEED - LARGE_CLOUD_MIN_SPEED) + 1) + LARGE_CLOUD_MIN_SPEED,
                            Cloud.CLOUD_TYPE_LARGE);
                }
                break;
        }

        return clouds;
    }

    /**
     * Repositions an existing Cloud object so it will be outside the right side of the screen.
     * This method also sets the speed of the cloud to a new random value.
     *
     * @param cloud       The Cloud object to be recycled.
     * @param screenWidth The screen's width in pixels.
     */
    public static void recycleCloud(Cloud cloud, int screenWidth) {
        final int posX = screenWidth + cloud.width;

        // Get a new position on the Y axis and a new speed based on the cloud type
        switch (cloud.type) {
            case Cloud.CLOUD_TYPE_SMALL:
                cloud.setPos(posX, getRandomCloudPosY(Cloud.CLOUD_TYPE_SMALL));
                cloud.setSpeedX(RANDOM.nextInt((SMALL_CLOUD_MAX_SPEED - SMALL_CLOUD_MIN_SPEED) + 1) + SMALL_CLOUD_MIN_SPEED);
                break;

            case Cloud.CLOUD_TYPE_MEDIUM:
                cloud.setPos(posX, getRandomCloudPosY(Cloud.CLOUD_TYPE_MEDIUM));
                cloud.setSpeedX(RANDOM.nextInt((MEDIUM_CLOUD_MAX_SPEED - MEDIUM_CLOUD_MIN_SPEED) + 1) + MEDIUM_CLOUD_MIN_SPEED);
                break;

            case Cloud.CLOUD_TYPE_LARGE:
                cloud.setPos(posX, getRandomCloudPosY(Cloud.CLOUD_TYPE_LARGE));
                cloud.setSpeedX(RANDOM.nextInt((LARGE_CLOUD_MAX_SPEED - LARGE_CLOUD_MIN_SPEED) + 1) + LARGE_CLOUD_MIN_SPEED);
                break;
        }
    }

    /**
     * Initializes the bitmaps array for the specified cloud type.
     *
     * @param res       Handler providing access to our game's resources.
     * @param cloudType The type of cloud to load bitmaps for.
     * @return The height of the highest cloud in the array.
     * @throws java.lang.IllegalArgumentException if the cloud type is not valid.
     */
    private static int loadCloudBitmaps(Resources res, int cloudType) {
        final Bitmap[] bitmaps;
        final int arraySize;

        // Initialize the bitmap array for the specified cloud type and reset it's index pointer
        switch (cloudType) {
            case Cloud.CLOUD_TYPE_SMALL:
                smallCloudBitmaps = new Bitmap[SMALL_CLOUDS_COUNT];
                smallCloudBitmapsIndex = 0;

                // Load the bitmaps
                smallCloudBitmaps[0] = BitmapFactory.decodeResource(res, R.drawable.cloud_s1);
                smallCloudBitmaps[1] = BitmapFactory.decodeResource(res, R.drawable.cloud_s2);

                // Update the local pointers
                bitmaps = smallCloudBitmaps;
                arraySize = SMALL_CLOUDS_COUNT;
                break;

            case Cloud.CLOUD_TYPE_MEDIUM:
                mediumCloudBitmaps = new Bitmap[MEDIUM_CLOUDS_COUNT];
                mediumCloudBitmapsIndex = 0;

                // Load the bitmaps
                mediumCloudBitmaps[0] = BitmapFactory.decodeResource(res, R.drawable.cloud_m1);
                mediumCloudBitmaps[1] = BitmapFactory.decodeResource(res, R.drawable.cloud_m2);

                // Update the local pointers
                bitmaps = mediumCloudBitmaps;
                arraySize = MEDIUM_CLOUDS_COUNT;
                break;

            case Cloud.CLOUD_TYPE_LARGE:
                largeCloudBitmaps = new Bitmap[LARGE_CLOUDS_COUNT];
                largeCloudBitmapsIndex = 0;

                // Load all the bitmaps
                largeCloudBitmaps[0] = BitmapFactory.decodeResource(res, R.drawable.cloud_l1);
                largeCloudBitmaps[1] = BitmapFactory.decodeResource(res, R.drawable.cloud_l2);

                // Update the local pointers
                bitmaps = largeCloudBitmaps;
                arraySize = LARGE_CLOUDS_COUNT;
                break;

            default:
                assert (!Cloud.isValidCloudType(cloudType));
                throw new IllegalArgumentException("Invalid cloud type.");
        }

        // Find the highest cloud in the array
        int maxHeight = 0;
        for (int i = 0; i < arraySize; i++) {
            maxHeight = Math.max(maxHeight, bitmaps[i].getHeight());
        }

        // Return the height of the highest cloud in the array
        return maxHeight;
    }

    /**
     * Returns a cloud bitmap of the specified type from the pool of bitmaps.
     *
     * @param cloudType The type of cloud to get a bitmap for.
     * @return A cloud bitmap of the specified type.
     * @throws java.lang.IllegalArgumentException if the cloud type is not valid.
     */
    private static Bitmap getNextCloudBitmap(int cloudType) {
        final Bitmap bitmap;

        // Get the bitmap for the specified cloud type and update the pointer
        switch (cloudType) {
            case Cloud.CLOUD_TYPE_SMALL:
                if (smallCloudBitmapsIndex >= SMALL_CLOUDS_COUNT) smallCloudBitmapsIndex = 0;
                bitmap = smallCloudBitmaps[smallCloudBitmapsIndex];
                smallCloudBitmapsIndex++;
                break;

            case Cloud.CLOUD_TYPE_MEDIUM:
                if (mediumCloudBitmapsIndex >= MEDIUM_CLOUDS_COUNT) mediumCloudBitmapsIndex = 0;
                bitmap = mediumCloudBitmaps[mediumCloudBitmapsIndex];
                mediumCloudBitmapsIndex++;
                break;

            case Cloud.CLOUD_TYPE_LARGE:
                if (largeCloudBitmapsIndex >= LARGE_CLOUDS_COUNT) largeCloudBitmapsIndex = 0;
                bitmap = largeCloudBitmaps[largeCloudBitmapsIndex];
                largeCloudBitmapsIndex++;
                break;

            default:
                assert (!Cloud.isValidCloudType(cloudType));
                throw new IllegalArgumentException("Invalid cloud type.");
        }

        return bitmap;
    }

    /**
     * Gets a random position from the clouds X positioning array.
     *
     * @return A random position from the clouds X positioning array.
     */
    private static int getRandomCloudPosX() {
        // Check if it's time to reset the grid
        if (cloudsPlacingGridIndexX >= PLACING_GRID_X_SIZE) {
            FisherYates.shuffle(cloudsPlacingGridX);
            cloudsPlacingGridIndexX = 0;
        }

        // Get the position
        final int pos = cloudsPlacingGridX[cloudsPlacingGridIndexX];

        // Get ready for the next call
        cloudsPlacingGridIndexX++;

        return pos;
    }

    /**
     * Gets a random position on the Y axis for a cloud of the specified type.
     *
     * @param cloudType The type of cloud to get a position on the Y axis for.
     * @return A random position on the Y axis for the cloud.
     * @throws java.lang.IllegalArgumentException if the cloud type is invalid.
     */
    private static int getRandomCloudPosY(int cloudType) {
        final int pos;

        switch (cloudType) {
            case Cloud.CLOUD_TYPE_SMALL:
                // Check if it's time to reset the grid
                if (smallCloudsPlacingGridIndex >= PLACING_GRID_Y_SIZE) {
                    FisherYates.shuffle(smallCloudsPlacingGridY);
                    smallCloudsPlacingGridIndex = 0;
                }

                // Get the position
                pos = smallCloudsPlacingGridY[smallCloudsPlacingGridIndex];

                // Get ready for the next call
                smallCloudsPlacingGridIndex++;
                break;

            case Cloud.CLOUD_TYPE_MEDIUM:
                // Check if it's time to reset the grid
                if (mediumCloudsPlacingGridIndex >= PLACING_GRID_Y_SIZE) {
                    FisherYates.shuffle(mediumCloudsPlacingGridY);
                    mediumCloudsPlacingGridIndex = 0;
                }

                // Get the position
                pos = mediumCloudsPlacingGridY[mediumCloudsPlacingGridIndex];

                // Get ready for the next call
                mediumCloudsPlacingGridIndex++;
                break;

            case Cloud.CLOUD_TYPE_LARGE:
                // Check if it's time to reset the grid
                if (largeCloudsPlacingGridIndex >= PLACING_GRID_Y_SIZE) {
                    FisherYates.shuffle(largeCloudsPlacingGridY);
                    largeCloudsPlacingGridIndex = 0;
                }

                // Get the position
                pos = largeCloudsPlacingGridY[largeCloudsPlacingGridIndex];

                // Get ready for the next call
                largeCloudsPlacingGridIndex++;
                break;

            default:
                assert (!Cloud.isValidCloudType(cloudType));
                throw new IllegalArgumentException("Invalid cloud type!");
        }

        return pos;
    }
}

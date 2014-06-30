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

package br.com.oncipriani.mygame.engine;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import br.com.oncipriani.mygame.R;
import br.com.oncipriani.mygame.helpers.FisherYates;
import br.com.oncipriani.mygame.objects.Cloud;
import br.com.oncipriani.mygame.objects.Explosion;
import br.com.oncipriani.mygame.objects.Star;
import br.com.oncipriani.mygame.objects.factories.CloudFactory;
import br.com.oncipriani.mygame.objects.factories.StarFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * This is the state where we expect to spend most of our time in.
 * It is where all the action happens.
 */
public class GameRunningState extends GameState {
    private static final Random RANDOM = new Random();
    private final int screenWidth, screenHeight;
    private final GameStateManager gameStateManager;
    private final Resources resources;

    // Maximum number of objects that can be on the screen at the same time
    private static final int MAX_STARS = 3;
    private static final int MAX_SMALL_CLOUDS = 3;
    private static final int MAX_MEDIUM_CLOUDS = 3;
    private static final int MAX_LARGE_CLOUDS = 3;

    // Objects used throughout the game
    private ArrayList<Star> stars;
    private ArrayList<Explosion> explosions;
    private ArrayList<Cloud> clouds;

    // Bitmaps for some of the objects
    private Bitmap skyBackground;
    private Bitmap explosionBitmap;

    // Minimum, maximum and current delay before spawning the next star
    private static final int MIN_STAR_SPAWN_DELAY = 100;
    private static final int MAX_STAR_SPAWN_DELAY = 200;
    private int starSpawnDelay = MIN_STAR_SPAWN_DELAY;

    /**
     * Initializes a new running game state.
     *
     * @param gameStateManager Game state manager that will be used to switch to other states.
     * @param resources        Handler providing access to our game's resources.
     * @param screenWidth      The screen's width in pixels.
     * @param screenHeight     The screen's height in pixels.
     */
    public GameRunningState(GameStateManager gameStateManager, Resources resources, int screenWidth, int screenHeight) {
        super(true); // This state assumes control of the entire screen.

        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.gameStateManager = gameStateManager;
        this.resources = resources;
    }

    @Override
    public void update() {
        int i;
        Explosion explosion;
        Star star;
        Cloud cloud;

        // Update every cloud, starting from the small (last) ones
        for (i = clouds.size() - 1; i >= 0; i--) {
            cloud = clouds.get(i);
            cloud.update();

            // Should we recycle the cloud?
            if (!cloud.isActive(screenWidth, screenHeight)) {
                CloudFactory.recycleCloud(cloud, screenWidth);
            }
        }

        // Update every active star
        final int activeStarCount = stars.size();
        for (i = 0; i < activeStarCount; i++) {
            star = stars.get(i);
            star.update(screenWidth);

//            // Check if the star fell out of the screen (game over)
//            if (!star.isActive(screenWidth, screenHeight)) {
//                // TODO: Create a "game over" state and set it here
//                return;
//            }
        }

        // Check if it's time to spawn a new star
        starSpawnDelay--;
        if (starSpawnDelay <= 0 && activeStarCount < MAX_STARS) {
            // Spawn a new star and reset the spawn delay counter
            stars.add(StarFactory.createStar(resources, screenWidth));
            starSpawnDelay = RANDOM.nextInt(((MAX_STAR_SPAWN_DELAY - MIN_STAR_SPAWN_DELAY) + 1)) + MIN_STAR_SPAWN_DELAY;
        }

        // Update every explosion, starting from the last
        for (i = explosions.size() - 1; i >= 0; i--) {
            explosion = explosions.get(i);
            explosion.update();

            // If the explosion is gone, remove it from the list
            if (!explosion.isVisible()) explosions.remove(i);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        int i; // Avoid memory thrashing

        // Draw the sky background
        canvas.drawBitmap(skyBackground, 0, 0, null);

        // Draw every cloud, starting from the last
        for (i = clouds.size() - 1; i >= 0; i--) clouds.get(i).draw(canvas);

        // Draw every active star, starting from the last
        for (i = stars.size() - 1; i >= 0; i--) stars.get(i).draw(canvas);

        // Draw every explosion, starting from the last
        for (i = explosions.size() - 1; i >= 0; i--) explosions.get(i).draw(canvas);
    }

    @Override
    public void handleActionDown(int eventX, int eventY) {
        Star star;

        // Check if any of the active stars was touched
        for (int i = stars.size() - 1; i >= 0; i--) {
            star = stars.get(i);

            if (star.handleActionDown(eventX, eventY)) {
                final int posX = star.getPosX();
                final int posY = star.getPosY();
                final int explosionSize;

                // If the star is still alive, release a few particles.
                if (star.getEnergy() > 0) {
                    explosionSize = Explosion.MIN_PARTICLES;
                } else {
                    // TODO: Update the player's score
                    // Release a lot of particles if it is dead and remove it from the list of active stars
                    explosionSize = Explosion.MAX_PARTICLES - RANDOM.nextInt(Explosion.MAX_PARTICLES / 2);
                    stars.remove(i);
                }
                explosions.add(new Explosion(explosionBitmap, explosionSize, posX, posY));
            }
        }
    }

    @Override
    public void exiting() {

    }

    @Override
    public void entered() {
        // Initialize the game objects
        stars = new ArrayList<Star>(MAX_STARS);
        explosions = new ArrayList<Explosion>(MAX_STARS);

        // The clouds must be populated from the first layer (front) to the last (back)
        clouds = new ArrayList<Cloud>(MAX_SMALL_CLOUDS + MAX_MEDIUM_CLOUDS + MAX_LARGE_CLOUDS);
        clouds.addAll(Arrays.asList(CloudFactory.createClouds(Cloud.CLOUD_TYPE_LARGE, MAX_LARGE_CLOUDS, screenWidth, screenHeight, resources)));
        clouds.addAll(Arrays.asList(CloudFactory.createClouds(Cloud.CLOUD_TYPE_MEDIUM, MAX_MEDIUM_CLOUDS, screenWidth, screenHeight, resources)));
        clouds.addAll(Arrays.asList(CloudFactory.createClouds(Cloud.CLOUD_TYPE_SMALL, MAX_SMALL_CLOUDS, screenWidth, screenHeight, resources)));

        // Load the bitmap for the explosions
        explosionBitmap = BitmapFactory.decodeResource(resources, R.drawable.star_small);

        // Load the sky background gradient from the XML ...
        GradientDrawable skyBackgroundGradient = (GradientDrawable) resources.getDrawable(R.drawable.sky_background);
        if (skyBackgroundGradient == null) throw new NullPointerException("Sky background gradient was not loaded!");
        skyBackgroundGradient.setBounds(0, 0, screenWidth, screenHeight);

        // ... and then generate a new Bitmap from the Drawable, because it's faster to draw this way.
        skyBackground = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(skyBackground);
        skyBackgroundGradient.draw(canvas);

        // TODO: Implement some kind of "get ready" game state.
    }

    @Override
    public void obscuring() {

    }

    @Override
    public void revealed() {

    }
}

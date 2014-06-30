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

/**
 * This class represents an actor in our game. An actor is any object that
 * should be updated at every game update cycle and whose display image is
 * a {@link android.graphics.Bitmap} object.
 */
public abstract class Actor {
    // We should cache frequently used values
    public final int width;
    public final int height;
    public final int halfWidth;
    public final int halfHeight;

    protected final Bitmap bitmap;

    protected int posX;
    protected int posY;

    /**
     * Constructs a new Actor at an undefined position. Since an Actor constructed
     * this way is not fully initialized, this constructor should only be called
     * from descendants of this class and only when the the starting position is
     * not known at the moment where <code>super</code> is called.
     *
     * @param bitmap The Bitmap object representing the actor's image.
     */
    protected Actor(Bitmap bitmap) {
        this.bitmap = bitmap;

        width = bitmap.getWidth();
        height = bitmap.getHeight();
        halfWidth = width / 2;
        halfHeight = height / 2;
    }

    /**
     * Constructs a new Actor at the specified position.
     *
     * @param bitmap The Bitmap object representing the actor's image.
     * @param posX   The actor's position on the X axis.
     * @param posY   The actor's position on the Y axis.
     */
    public Actor(Bitmap bitmap, int posX, int posY) {
        this.posX = posX;
        this.posY = posY;

        this.bitmap = bitmap;

        width = bitmap.getWidth();
        height = bitmap.getHeight();
        halfWidth = width / 2;
        halfHeight = height / 2;
    }

    /**
     * Returns the object's current position on the X axis.
     *
     * @return The object's current position on the X axis.
     */
    public int getPosX() {
        return posX;
    }

    /**
     * Places the object at the specified position on the X axis.
     *
     * @param posX The object's new position on the X axis.
     */
    public void setPosX(int posX) {
        this.posX = posX;
    }

    /**
     * Returns the object's current position on the Y axis.
     *
     * @return The object's current position on the Y axis.
     */
    public int getPosY() {
        return posY;
    }

    /**
     * Places the object at the specified position on the Y axis.
     *
     * @param posY The object's new position on the Y axis.
     */
    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setPos(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Method that draws the object on the provided canvas.
     *
     * @param canvas The canvas where the object will be drawn.
     */
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, posX - halfWidth, posY - halfHeight, null);
    }

    /**
     * Method which updates the object's position and internal state every tick.
     */
    public abstract void update();

    /**
     * Returns <code>true</code> if any portion of the actor is (or will be) visible on the screen.
     * This method can be used to determine if the object can be safely recycled. For example,
     * if this method returns <code>false</code>, it means that the user will never be able to see
     * or interact with it, no matter how many update cycles passes.
     *
     * @param screenWidth  The screen width in pixels.
     * @param screenHeight The screen height in pixels.
     * @return <code>true</code> if the actor is (or will be) visible, <code>false</code> otherwise.
     */
    public abstract boolean isActive(int screenWidth, int screenHeight);

    /**
     * Returns <code>true</code> if the actor is entirely visible on screen.
     *
     * @param screenWidth  The screen width in pixels.
     * @param screenHeight The screen height in pixels.
     * @return <code>true</code> if the actor is entirely visible on screen and <code>false</code> otherwise.
     */
    public boolean isFullyVisible(int screenWidth, int screenHeight) {
        return (posX + halfWidth <= screenWidth) && (posX - halfWidth >= 0) &&
                (posY + halfHeight <= screenHeight) && (posY - halfHeight >= 0);
    }
}

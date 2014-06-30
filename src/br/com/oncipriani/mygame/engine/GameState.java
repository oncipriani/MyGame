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

import android.graphics.Canvas;

/**
 * This Interface abstracts a state the game can be in.
 * <p>
 * Game states are used to avoid filling your main loop with thousands of ifs controlling
 * whether to draw the main menu, render the intro sequence, execute the single player
 * game, scroll the credits and so on. You wrap each of these actions into a state class
 * and from then on switch states simply by adding and removing these states from
 * the game state manager.
 * </p>
 * <p>
 * The game state manager is not automatically provided to the game states to encourage
 * modularity: this way, you can use game states in a game state manager of your own
 * (think special case solutions like on a phone with a back button), or you could limit
 * the interactions with the game state manager by providing states with a wrapper that
 * lets them perform only a number of actions or switching other game states by name
 * instead of creating them inside the game state.
 * </p>
 */
public abstract class GameState {
    // Stores if game state takes exclusive ownership of the screen
    private final boolean isExclusive;

    /**
     * Constructs a new <code>GameState</code>.
     *
     * @param isExclusive <code>true</code> if the state takes exclusive ownership of the screen.
     */
    protected GameState(boolean isExclusive) {
        this.isExclusive = isExclusive;
    }

    /**
     * Returns if the game state takes exclusive ownership of the screen and does not
     * require the state below it in the stack to be updated as long as it's active.
     * If not exclusive, the game state sits on top of the state below it in the stack,
     * but does not completely obscure it or requires it to continue being updated.
     *
     * @return <code>true</code> if the state takes exclusive ownership of the screen.
     */
    public boolean isExclusive() {
        return isExclusive;
    }

    /**
     * Advances the time of the game state.
     */
    public abstract void update();

    /**
     * Draws the state on the screen.
     *
     * @param canvas The canvas representing the screen.
     */
    public abstract void draw(Canvas canvas);

    /**
     * Informs the game state of a touch event.
     *
     * @param eventX The location of the touch on the X axis.
     * @param eventY The location on the touch on the Y axis.
     */
    public abstract void handleActionDown(int eventX, int eventY);

    /**
     * Notifies the game state it is about to be exited.
     * <p>
     * This happens when the game state is completely removed from the game state manager.
     * Depending on your game's design, the state may be kept somewhere (presumably some
     * state repository that's responsible for creating an storing states) or it may be
     * deleted immediately following its removal from the game state manager.
     * </p>
     * <p>
     * Upon receiving this notification, the game state should remove any nodes it has
     * added to the game's scene graph, disconnect itself from input callbacks and so
     * on. You may even want to destroy memory-intensive resources if the game state may
     * be kept alive in your game.
     * </p>
     */
    public abstract void exiting();

    /**
     * Notifies the game state that it has been entered.
     * <p>
     * This call allows the game state to add any nodes it requires to the game's scene
     * graph or to connect to the callbacks of an input manager, etc.
     * </p>
     */
    public abstract void entered();

    /**
     * Notifies the game state that it is about to be obscured by another state.
     * <p>
     * This happens when another game state has been pushed on top of this state. A typical
     * scenario would be if you leave your game's main menu on the state stack during the
     * whole game, as soon as the game play state is entered, it would always draw over
     * your main menu, thus the main menu should no longer bother drawing. It may even
     * actively remove its menu items from the game's GUI.
     * </p>
     */
    public abstract void obscuring();

    /**
     * Notifies the game state that it is no longer obscured by another state
     * <p>
     * This notification will be issued when the game state was obscured by another state
     * sitting on top of it but that state has now been removed. If the revealed state
     * was the game's main menu, for example, it should now resume drawing or perhaps
     * re-add the menu items to the game's GUI in case it removed them when it was
     * first obscured.
     * </p>
     */
    public abstract void revealed();
}

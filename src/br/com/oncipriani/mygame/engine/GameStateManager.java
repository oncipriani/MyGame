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
import android.util.Log;

import java.util.ArrayList;

/**
 * Stacked game state manager that forwards <code>draw</code> and <code>update</code> calls.
 */
public class GameStateManager {
    private static final String TAG = GameStateManager.class.getSimpleName();

    // Stores all currently active game states.
    private ArrayList<GameState> activeStates;
    // Stores all game states from the last exclusive state.
    private ArrayList<GameState> exposedStates;

    /**
     * Initializes a new game state manager.
     */
    public GameStateManager() {
        activeStates = new ArrayList<GameState>();
        exposedStates = new ArrayList<GameState>();
    }

    /**
     * Returns the currently active game state without removing it from the stack.
     *
     * @return The most recent game state on the stack.
     */
    public GameState peek() {
        if (activeStates.isEmpty()) {
            return null;
        } else {
            return activeStates.get(activeStates.size() - 1);
        }
    }

    /**
     * Appends a new game state to the stack.
     *
     * @param gameState Game state that will be pushed onto the stack.
     */
    public void push(GameState gameState) {
        activeStates.add(gameState);

        if (gameState.isExclusive()) exposedStates.clear();

        exposedStates.add(gameState);

        notifyObscuredStates();

        gameState.entered();
    }

    /**
     * Removes the most recent game state from the stack.
     *
     * @return The state that has been removed from the stack.
     */
    public GameState pop() {
        if (activeStates.isEmpty()) {
            throw new RuntimeException("Attempted to pop from an empty game state stack");
        }

        GameState popped = activeStates.remove(activeStates.size() - 1);
        popped.exiting();

        if (popped.isExclusive()) {
            rebuildExposedStates();
        } else {
            exposedStates.remove(popped);
        }

        notifyRevealedStates();

        return popped;
    }

    /**
     * Notifies all previously exposed states that they have been obscured.
     */
    private void notifyObscuredStates() {
        final int activeStatesSize = activeStates.size();

        if (activeStatesSize < 2) return;

        // Reverse scan until we hit either the beginning or find the next exclusive state
        int index = activeStatesSize - 2;
        while (index > 0) {
            if (activeStates.get(index).isExclusive()) {
                break;
            }
            --index;
        }

        // Now go forward (up until the second-to-last state) and notify the obscured states
        while (index < activeStatesSize - 1) {
            activeStates.get(index).obscuring();
            ++index;
        }
    }

    /**
     * Notifies all currently exposed states that they have been revealed.
     */
    private void notifyRevealedStates() {
        if (activeStates.isEmpty()) return;

        final int activeStatesSize = activeStates.size();

        // Reverse scan until we hit either the beginning or find the next exclusive state
        int index = activeStatesSize - 1;
        while (index > 0) {
            if (activeStates.get(index).isExclusive()) {
                break;
            }
            --index;
        }

        // Now go forward and notify all revealed state
        while (index < activeStatesSize) {
            activeStates.get(index).revealed();
            ++index;
        }
    }

    /**
     * Advances the time of the exposed game states.
     */
    public void update() {
        final int size = exposedStates.size();

        for (int i = 0; i < size; i++) {
            exposedStates.get(i).update();
        }
    }

    /**
     * Informs all exposed game states of a touch event.
     *
     * @param eventX The location of the touch on the X axis.
     * @param eventY The location of the touch on the Y axis.
     */
    public void handleActionDown(int eventX, int eventY) {
        final int size = exposedStates.size();

        for (int i = 0; i < size; i++) {
            exposedStates.get(i).handleActionDown(eventX, eventY);
        }
    }

    /**
     * Instructs the exposed game states to render themselves.
     *
     * @param canvas The screen to draw on.
     */
    public void draw(Canvas canvas) {
        final int size = exposedStates.size();

        for (int i = 0; i < size; i++) {
            exposedStates.get(i).draw(canvas);
        }
    }

    /**
     * Rebuilds the exposed states queue when an exclusive state has
     * been popped from the stack.
     */
    private void rebuildExposedStates() {
        exposedStates.clear();

        if (activeStates.isEmpty()) return;

        final int activeStatesSize = activeStates.size();

        // Reverse scan the active states until we hit either the beginning or an exclusive state
        int index = activeStatesSize - 1;
        while (index > 0) {
            if (activeStates.get(index).isExclusive()) {
                break;
            }
            --index;
        }

        // Now go forward again and fill the list of exposed states
        while (index < activeStatesSize) {
            exposedStates.add(activeStates.get(index));
            ++index;
        }
    }

    /**
     * Replaces the most recent game state on the stack. This method is mostly just syntactic
     * sugar for a call to <code>pop</code> followed by <code>push</code>, except that it will
     * also work if the game state stack is currently empty, in which case it will equal the
     * <code>push</code> method and return null.
     *
     * @param state State the most recent state on the stack will be replaced with.
     * @return <code>null</code> if the stack was empty. The replaced state otherwise.
     */
    public GameState switchState(GameState state) {
        GameState currentState = peek();

        if (currentState != null) pop();

        push(state);

        return currentState;
    }
}

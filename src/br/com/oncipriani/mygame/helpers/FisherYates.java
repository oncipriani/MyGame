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

package br.com.oncipriani.mygame.helpers;

import java.util.Random;

/**
 * This class implements methods for initializing and shuffling arrays using Fisher-Yates shuffle.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle">Fisher–Yates shuffle on Wikipedia</a>.
 */
public final class FisherYates {
    private static final Random RANDOM = new Random();

    /**
     * Initializes an array using Fisher-Yates shuffle ("inside out" version), that
     * is, an array of size <i>n</i> will have <i>n</i> unique elements on the interval
     * [<code>min</code>, <code>max</code>]. Please note that <code>max</code> will not
     * be present in the array unless <i>n</i> is a multiple of (<code>max</code> - <code>min</code>).
     *
     * @param array The array to be initialized.
     * @param max   The maximum value that will be in the array.
     * @param min   The minimum value that will be in the array.
     * @throws java.lang.IllegalArgumentException if the array is too big for the specified interval.
     */
    public static void initialize(int[] array, int min, int max) {
        final int size = array.length;
        final int stepping = (max - min) / size;

        // Check if stepping is valid
        if (stepping == 0) throw new IllegalArgumentException("Array is too big for the specified interval");

        array[0] = min;
        for (int i = 1, j; i < size; i++) {
            j = RANDOM.nextInt(i + 1);
            array[i] = array[j];
            array[j] = (i * stepping) + min;
        }
    }

    /**
     * Shuffles an array using Fisher-Yates shuffle.
     *
     * @param array The array to be shuffled.
     */
    public static void shuffle(int[] array) {
        int temp;

        for (int i = array.length - 1, j; i >= 1; i--) {
            j = RANDOM.nextInt(i + 1);
            temp = array[j];
            array[j] = array[i];
            array[i] = temp;
        }
    }
}

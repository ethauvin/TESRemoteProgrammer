/*
 * MinMaxFilter.kt
 *
 * Copyright 2016 Erik C. Thauvin (erik@thauvin.net)
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
package net.thauvin.erik.android.tesremoteprogrammer.filters

import android.text.InputFilter
import android.text.Spanned

class MinMaxFilter : InputFilter {
    private val min: Int
    private val max: Int
    private val size: Int

    constructor(min: Int, max: Int, size: Int) {
        this.min = min
        this.max = max
        this.size = size
    }

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        try {
            val input = (dest.toString() + source.toString()).toInt()
            val len = dest.length + source.length
            if ((min > 0 && size > 1 && len < size && input == 0) || input in IntRange(min, max)) {
                return null
            }
        } catch (nef: NumberFormatException) {
        }

        return ""
    }
}
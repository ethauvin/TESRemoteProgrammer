/*
 * MinMaxFilter.kt
 *
 * Copyright 2016-2017 Erik C. Thauvin (erik@thauvin.net)
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
import org.jetbrains.anko.AnkoLogger

class MinMaxFilter(private val min: Int, private val max: Int, private val size: Int, private val zeros: Boolean) : InputFilter, AnkoLogger {

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        val input = (dest.toString() + source.toString())

        if (isInRange(input, size, min, max, zeros)) {
            return null
        }
        return ""
    }

    private fun isInRange(s: String, size: Int, min: Int, max: Int, zeros: Boolean): Boolean {
        try {
            var i = s.toInt()
            val len = s.length

            if (zeros) {
                if (size > 1 && len != size) {
                    if (i == 0) {
                        return true
                    } else {
                        i = s.padEnd(size, '0').toInt()
                    }
                }
            } else {
                if (len > 1 && s.startsWith("0")) {
                    return false
                }
            }

            return i in IntRange(min, max)
        } catch (nfe: NumberFormatException) {
            return false
        }
    }
}
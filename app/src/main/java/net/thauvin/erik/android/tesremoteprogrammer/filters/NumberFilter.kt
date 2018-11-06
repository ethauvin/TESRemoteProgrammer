/*
 * NumberFilter.kt
 *
 * Copyright 2016-2018 Erik C. Thauvin (erik@thauvin.net)
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
import android.text.SpannableStringBuilder
import android.text.Spanned
import net.thauvin.erik.android.tesremoteprogrammer.util.isDigits
import org.jetbrains.anko.AnkoLogger

class NumberFilter(allowed: String, alt: String) : InputFilter, AnkoLogger {
    private val allowed: String
    private val digits = "0123456789"

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        if (source is SpannableStringBuilder) {
            for (i in end - 1 downTo start) {
                val c = source[i]
                if (!allowed.contains(c)) {
                    source.delete(i, i + 1)
                }
            }
            return source
        } else {
            val sb = StringBuilder()
            (start until end)
                .map { source[it] }
                .filter { allowed.contains(it) }
                .forEach { sb.append(it.toUpperCase()) }
            return sb.toString()
        }
    }

    init {
        this.allowed = if (allowed.isDigits()) {
            "$allowed$alt"
        } else {
            "$digits$alt"
        }
    }
}
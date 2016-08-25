/*
 * Strings.kt
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
package net.thauvin.erik.android.tesremoteprogrammer.util

fun String.plural(size: Int): String {
    val consonants = "bcdfghjklmnpqrstvwxz"

    if (size > 1 && this.length > 2) {
        if ((this.endsWith("o", true) || this.endsWith("s", true)) &&
                consonants.contains(this[this.length - 2], true)) {
            return this + "es"
        }

        if (this.endsWith("y", true) &&
                consonants.contains(this[this.length - 2], true)) {
            return this + "ies"
        }
    }

    return this + "s"
}

fun String.replaceAll(replace: Array<Pair<String, String>>): String {
    val result = StringBuilder(this)
    var offset: Int

    replace.forEach {
        with(it) {
            if (first.isNotEmpty()) {
                offset = result.indexOf(first)

                while (offset != -1) {
                    result.replace(offset, first.length + offset, second)
                    offset = result.indexOf(first, offset + second.length)
                }
            }
        }
    }

    return result.toString()
}
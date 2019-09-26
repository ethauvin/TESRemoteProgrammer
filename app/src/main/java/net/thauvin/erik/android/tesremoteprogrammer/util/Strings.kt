/*
 * Strings.kt
 *
 * Copyright 2016-2019 Erik C. Thauvin (erik@thauvin.net)
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

fun String.isDigits(): Boolean {
    if (isBlank()) {
        return false
    }

    forEach {
        if (!it.isDigit()) {
            return false
        }
    }

    return true
}

fun String.isDKS(): Boolean = equals(Dtmf.DKS, true)

fun String.isLinear(): Boolean = equals(Dtmf.LINEAR, true)

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

fun String.toDialPad() = this.replaceAll(
    arrayOf(
        Pair("*", "✱"),
        Pair("#", "＃")
    )
)

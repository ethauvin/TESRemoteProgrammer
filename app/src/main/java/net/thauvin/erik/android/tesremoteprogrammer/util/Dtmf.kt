/*
 * Dtmf.kt
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

import android.widget.EditText
import net.thauvin.erik.android.tesremoteprogrammer.MainActivity
import net.thauvin.erik.android.tesremoteprogrammer.models.Option
import java.util.*

class Dtmf {
    companion object {
        private val DTMF_MASTER = "[MASTER]"
        private val DTMF_FIELD = "[FIELD:%1\$d]"

        private fun alphaToDigits(text: String, star: String): String {
            val result = StringBuffer()

            text.toUpperCase().forEach { c ->
                when (c) {
                    'A' -> result.append("2$star${MainActivity.PAUSE}")
                    'B' -> result.append("22$star${MainActivity.PAUSE}")
                    'C' -> result.append("2222$star${MainActivity.PAUSE}")

                    'D' -> result.append("3$star${MainActivity.PAUSE}")
                    'E' -> result.append("33$star${MainActivity.PAUSE}")
                    'F' -> result.append("333$star${MainActivity.PAUSE}")

                    'G' -> result.append("4$star${MainActivity.PAUSE}")
                    'H' -> result.append("44$star${MainActivity.PAUSE}")
                    'I' -> result.append("444$star${MainActivity.PAUSE}")

                    'J' -> result.append("5$star${MainActivity.PAUSE}")
                    'K' -> result.append("55$star${MainActivity.PAUSE}")
                    'L' -> result.append("555$star${MainActivity.PAUSE}")

                    'M' -> result.append("6$star${MainActivity.PAUSE}")
                    'N' -> result.append("66$star${MainActivity.PAUSE}")
                    'O' -> result.append("666$star${MainActivity.PAUSE}")

                    'P' -> result.append("7$star${MainActivity.PAUSE}")
                    'Q' -> result.append("77$star${MainActivity.PAUSE}")
                    'R' -> result.append("777$star${MainActivity.PAUSE}")
                    'S' -> result.append("7777$star${MainActivity.PAUSE}")

                    'T' -> result.append("8$star${MainActivity.PAUSE}")
                    'U' -> result.append("88$star${MainActivity.PAUSE}")
                    'V' -> result.append("888$star${MainActivity.PAUSE}")

                    'W' -> result.append("9$star${MainActivity.PAUSE}")
                    'X' -> result.append("99$star${MainActivity.PAUSE}")
                    'Y' -> result.append("999$star${MainActivity.PAUSE}")
                    'Z' -> result.append("9999$star${MainActivity.PAUSE}")

                    '0' -> result.append("0$star${MainActivity.PAUSE}")
                    '1' -> result.append("11$star${MainActivity.PAUSE}")
                    '2' -> result.append("2222$star${MainActivity.PAUSE}")
                    '3' -> result.append("3333$star${MainActivity.PAUSE}")
                    '4' -> result.append("4444$star${MainActivity.PAUSE}")
                    '5' -> result.append("5555$star${MainActivity.PAUSE}")
                    '6' -> result.append("6666$star${MainActivity.PAUSE}")
                    '7' -> result.append("77777$star${MainActivity.PAUSE}")
                    '8' -> result.append("8888$star${MainActivity.PAUSE}")
                    '9' -> result.append("99999$star${MainActivity.PAUSE}")

                    ' ' -> result.append("1$star${MainActivity.PAUSE}")
                }
            }
            return result.toString()
        }

        fun build(master: String,
                  star: String,
                  option: Option,
                  fields: ArrayList<EditText>): String {
            val replace = arrayListOf(Pair("$DTMF_MASTER", master))

            fields.forEachIndexed { i, field ->
                replace.add(Pair(DTMF_FIELD.format(i + 1),
                        if (option.fields[i].alpha) {
                            alphaToDigits(field.text.toString(), star)
                        } else {
                            field.text.toString()
                        }))
            }

            return option.dtmf.replaceAll(replace.toTypedArray())
        }

        fun validate(dtmf: String, extra: String): Boolean {
            dtmf.forEach {
                if (!(it.isDigit() || it.equals(',') || extra.contains(it))) {
                    return false
                }
            }
            return true
        }

        fun mock(option: Option, blank: String): String {
            val replace = arrayListOf(Pair("$DTMF_MASTER", blank))

            option.fields.forEachIndexed { i, field ->
                replace.add(Pair(DTMF_FIELD.format(i + 1), blank))
            }

            return option.dtmf.replaceAll(replace.toTypedArray())
        }
    }
}
/*
 * Dtmf.kt
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

import android.widget.EditText
import net.thauvin.erik.android.tesremoteprogrammer.MainActivity
import net.thauvin.erik.android.tesremoteprogrammer.models.Option
import java.util.ArrayList
import java.util.Locale

class Dtmf private constructor() {
    companion object {
        const val DTMF_MASTER = "[MASTER]"
        const val DTMF_FIELD = "[FIELD:%1\$d]"
        const val DKS = "dks"
        const val LINEAR = "linear"
        const val DKS_EXTRAS = " "
        const val LINEAR_EXTRAS = ", -."

        private fun dksAlphaToDigits(text: String, ack: String): String {
            val result = StringBuffer()

            text.toUpperCase(Locale.getDefault()).forEach { c ->
                when (c) {
                    'A' -> result.append("2$ack${MainActivity.PAUSE}")
                    'B' -> result.append("22$ack${MainActivity.PAUSE}")
                    'C' -> result.append("222$ack${MainActivity.PAUSE}")

                    'D' -> result.append("3$ack${MainActivity.PAUSE}")
                    'E' -> result.append("33$ack${MainActivity.PAUSE}")
                    'F' -> result.append("333$ack${MainActivity.PAUSE}")

                    'G' -> result.append("4$ack${MainActivity.PAUSE}")
                    'H' -> result.append("44$ack${MainActivity.PAUSE}")
                    'I' -> result.append("444$ack${MainActivity.PAUSE}")

                    'J' -> result.append("5$ack${MainActivity.PAUSE}")
                    'K' -> result.append("55$ack${MainActivity.PAUSE}")
                    'L' -> result.append("555$ack${MainActivity.PAUSE}")

                    'M' -> result.append("6$ack${MainActivity.PAUSE}")
                    'N' -> result.append("66$ack${MainActivity.PAUSE}")
                    'O' -> result.append("666$ack${MainActivity.PAUSE}")

                    'P' -> result.append("7$ack${MainActivity.PAUSE}")
                    'Q' -> result.append("77$ack${MainActivity.PAUSE}")
                    'R' -> result.append("777$ack${MainActivity.PAUSE}")
                    'S' -> result.append("7777$ack${MainActivity.PAUSE}")

                    'T' -> result.append("8$ack${MainActivity.PAUSE}")
                    'U' -> result.append("88$ack${MainActivity.PAUSE}")
                    'V' -> result.append("888$ack${MainActivity.PAUSE}")

                    'W' -> result.append("9$ack${MainActivity.PAUSE}")
                    'X' -> result.append("99$ack${MainActivity.PAUSE}")
                    'Y' -> result.append("999$ack${MainActivity.PAUSE}")
                    'Z' -> result.append("9999$ack${MainActivity.PAUSE}")

                    '0' -> result.append("0$ack${MainActivity.PAUSE}")
                    '1' -> result.append("11$ack${MainActivity.PAUSE}")
                    '2' -> result.append("2222$ack${MainActivity.PAUSE}")
                    '3' -> result.append("3333$ack${MainActivity.PAUSE}")
                    '4' -> result.append("4444$ack${MainActivity.PAUSE}")
                    '5' -> result.append("5555$ack${MainActivity.PAUSE}")
                    '6' -> result.append("6666$ack${MainActivity.PAUSE}")
                    '7' -> result.append("77777$ack${MainActivity.PAUSE}")
                    '8' -> result.append("8888$ack${MainActivity.PAUSE}")
                    '9' -> result.append("99999$ack${MainActivity.PAUSE}")

                    ' ' -> result.append("1$ack${MainActivity.PAUSE}")
                }
            }
            return result.toString()
        }

        fun isValidType(type: String): Boolean =
            type.equals(DKS, true) || type.equals(LINEAR, true)

        private fun linearAlphaToDigits(text: String): String {
            val result = StringBuffer()

            text.toUpperCase(Locale.getDefault()).forEach { c ->
                when (c) {
                    'A' -> result.append("1${MainActivity.PAUSE}")
                    'B' -> result.append("11${MainActivity.PAUSE}")
                    'C' -> result.append("111${MainActivity.PAUSE}")

                    'D' -> result.append("2${MainActivity.PAUSE}")
                    'E' -> result.append("22${MainActivity.PAUSE}")
                    'F' -> result.append("222${MainActivity.PAUSE}")

                    'G' -> result.append("3${MainActivity.PAUSE}")
                    'H' -> result.append("33${MainActivity.PAUSE}")
                    'I' -> result.append("333${MainActivity.PAUSE}")

                    'J' -> result.append("4${MainActivity.PAUSE}")
                    'K' -> result.append("44${MainActivity.PAUSE}")
                    'L' -> result.append("444${MainActivity.PAUSE}")

                    'M' -> result.append("5${MainActivity.PAUSE}")
                    'N' -> result.append("55${MainActivity.PAUSE}")
                    'O' -> result.append("555${MainActivity.PAUSE}")

                    'P' -> result.append("6${MainActivity.PAUSE}")
                    'Q' -> result.append("66${MainActivity.PAUSE}")
                    'R' -> result.append("666${MainActivity.PAUSE}")

                    'S' -> result.append("7${MainActivity.PAUSE}")
                    'T' -> result.append("77${MainActivity.PAUSE}")
                    'U' -> result.append("777${MainActivity.PAUSE}")

                    'V' -> result.append("8${MainActivity.PAUSE}")
                    'W' -> result.append("88${MainActivity.PAUSE}")
                    'X' -> result.append("888${MainActivity.PAUSE}")

                    'Y' -> result.append("9${MainActivity.PAUSE}")
                    'Z' -> result.append("99${MainActivity.PAUSE}")
                    ',' -> result.append("999${MainActivity.PAUSE}")

                    '0' -> result.append("0000${MainActivity.PAUSE}")
                    '1' -> result.append("1111${MainActivity.PAUSE}")
                    '2' -> result.append("2222${MainActivity.PAUSE}")
                    '3' -> result.append("3333${MainActivity.PAUSE}")
                    '4' -> result.append("4444${MainActivity.PAUSE}")
                    '5' -> result.append("5555${MainActivity.PAUSE}")
                    '6' -> result.append("6666${MainActivity.PAUSE}")
                    '7' -> result.append("7777${MainActivity.PAUSE}")
                    '8' -> result.append("8888${MainActivity.PAUSE}")
                    '9' -> result.append("9999${MainActivity.PAUSE}")

                    ' ' -> result.append("0${MainActivity.PAUSE}")
                    '-' -> result.append("00${MainActivity.PAUSE}")
                    '.' -> result.append("0000${MainActivity.PAUSE}")
                }
            }
            return result.toString()
        }

        fun build(
            type: String,
            master: String,
            ack: String,
            option: Option,
            fields: ArrayList<EditText>
        ): String {
            val replace = arrayListOf(Pair(DTMF_MASTER, master))

            fields.forEachIndexed { i, field ->
                replace.add(
                    Pair(
                        DTMF_FIELD.format(i + 1),
                        if (option.fields[i]!!.alpha && type.isDKS()) {
                            dksAlphaToDigits(field.text.toString(), ack)
                        } else if (option.fields[i]!!.alpha && type.isLinear()) {
                            linearAlphaToDigits(field.text.toString())
                        } else {
                            field.text.toString()
                        }
                    )
                )
            }

            return option.dtmf.replaceAll(replace.toTypedArray())
        }

        fun validate(dtmf: String, extra: String, nodial: Boolean): Boolean {
            dtmf.split(MainActivity.PAUSE).forEach { s ->
                if (!(nodial && s.endsWith(MainActivity.QUOTE) &&
                        s.startsWith(MainActivity.QUOTE))) {
                    s.forEach { ch ->
                        if (!(ch.isDigit() || ch == ',' || extra.contains(ch))) {
                            return false
                        }
                    }
                }
            }
            return true
        }

        fun mock(option: Option, blank: String): String {
            val replace = arrayListOf(Pair(DTMF_MASTER, blank))

            option.fields.forEachIndexed { i, _ ->
                replace.add(Pair(DTMF_FIELD.format(i + 1), blank))
            }

            return option.dtmf.replaceAll(replace.toTypedArray())
        }
    }
}

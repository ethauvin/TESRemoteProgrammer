/*
 * Field.kt
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
package net.thauvin.erik.android.tesremoteprogrammer.models

import android.os.Parcel
import android.os.Parcelable
import net.thauvin.erik.android.tesremoteprogrammer.util.ifNull
import java.io.Serializable

data class Field(
    var hint: String,
    var digits: String,
    var alpha: Boolean,
    val alt: Boolean,
    val zeros: Boolean,
    var minSize: Int,
    var size: Int,
    var min: Int,
    var max: Int
) : Parcelable, Serializable {
    companion object {
        @JvmStatic
        private val serialVersionUID: Long = 1L

        @JvmField
        val CREATOR: Parcelable.Creator<Field> = object : Parcelable.Creator<Field> {
            override fun createFromParcel(source: Parcel): Field = Field(source)
            override fun newArray(size: Int): Array<Field?> = arrayOfNulls(size)
        }
    }

    @Suppress("unused")
    constructor() : this(
        hint = "",
        digits = "",
        alpha = false,
        alt = false,
        zeros = false,
        minSize = -1,
        size = -1,
        min = -1,
        max = -1
    )

    constructor(source: Parcel) : this(
        hint = source.readString().ifNull(),
        digits = source.readString().ifNull(),
        alpha = 1 == source.readInt(),
        alt = 1 == source.readInt(),
        zeros = 1 == source.readInt(),
        minSize = source.readInt(),
        size = source.readInt(),
        min = source.readInt(),
        max = source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(hint)
        dest?.writeString(digits)
        dest?.writeInt((if (alpha) 1 else 0))
        dest?.writeInt((if (alt) 1 else 0))
        dest?.writeInt((if (zeros) 1 else 0))
        dest?.writeInt(minSize)
        dest?.writeInt(size)
        dest?.writeInt(min)
        dest?.writeInt(max)
    }
}

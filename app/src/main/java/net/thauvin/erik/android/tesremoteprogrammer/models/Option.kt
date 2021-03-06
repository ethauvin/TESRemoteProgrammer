/*
 * Option.kt
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

data class Option(
    var title: String,
    var fields: List<Field?>,
    var nodial: Boolean,
    var nosteps: Boolean,
    var dtmf: String
) : Parcelable, Serializable, Comparable<Option> {
    companion object {
        @JvmStatic
        private val serialVersionUID: Long = 1L

        @JvmField
        val CREATOR: Parcelable.Creator<Option> = object : Parcelable.Creator<Option> {
            override fun createFromParcel(source: Parcel): Option = Option(source)
            override fun newArray(size: Int): Array<Option?> = arrayOfNulls(size)
        }
    }

    constructor() : this(
        title = "",
        fields = emptyList(),
        nodial = false,
        nosteps = false,
        dtmf = ""
    )

    constructor(source: Parcel) : this(
        title = source.readString().ifNull(),
        fields = source.createTypedArrayList(Field.CREATOR) ?: emptyList(),
        nodial = 1 == source.readInt(),
        nosteps = 1 == source.readInt(),
        dtmf = source.readString().ifNull()
    )

    override fun compareTo(other: Option): Int = title.compareTo(other.title)

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(title)
        dest?.writeTypedList(fields)
        dest?.writeInt((if (nodial) 1 else 0))
        dest?.writeInt((if (nosteps) 1 else 0))
        dest?.writeString(dtmf)
    }
}

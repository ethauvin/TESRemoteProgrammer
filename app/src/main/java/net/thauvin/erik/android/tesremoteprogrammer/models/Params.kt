/*
 * Params.kt
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

data class Params(
    var name: String,
    var type: String,
    var phone: String,
    var master: String,
    var size: Int,
    var ack: String,
    var alt: String,
    var begin: String,
    var end: String
) : Parcelable, Serializable {
    companion object {
        @JvmStatic
        private val serialVersionUID: Long = 1L

        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<Params> = object : Parcelable.Creator<Params> {
            override fun createFromParcel(source: Parcel): Params = Params(source)
            override fun newArray(size: Int): Array<Params?> = arrayOfNulls(size)
        }
    }

    constructor() : this(
        name = "",
        type = "",
        phone = "",
        master = "",
        size = -1,
        ack = "",
        alt = "",
        begin = "",
        end = ""
    )

    constructor(source: Parcel) : this(
        source.readString().ifNull(),
        source.readString().ifNull(),
        source.readString().ifNull(),
        source.readString().ifNull(),
        source.readInt(),
        source.readString().ifNull(),
        source.readString().ifNull(),
        source.readString().ifNull(),
        source.readString().ifNull()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(name)
        dest?.writeString(type)
        dest?.writeString(phone)
        dest?.writeString(master)
        dest?.writeInt(size)
        dest?.writeString(ack)
        dest?.writeString(alt)
        dest?.writeString(begin)
        dest?.writeString(end)
    }
}

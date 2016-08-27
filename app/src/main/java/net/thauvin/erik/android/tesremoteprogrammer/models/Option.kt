/*
 * Option.kt
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
package net.thauvin.erik.android.tesremoteprogrammer.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Option(var title: String,
                  var fields: List<Field>,
                  var nodial: Boolean,
                  var nosteps: Boolean,
                  var dtmf: String) : Parcelable, Serializable, Comparable<Option> {

    companion object {
        private @JvmStatic val serialVersionUID: Long = 1

        @JvmField val CREATOR: Parcelable.Creator<Option> = object : Parcelable.Creator<Option> {
            override fun createFromParcel(source: Parcel): Option = Option(source)
            override fun newArray(size: Int): Array<Option?> = arrayOfNulls(size)
        }
    }

    constructor() : this("", emptyList(), false, false, "")

    constructor(source: Parcel) : this(
            source.readString(),
            source.createTypedArrayList(Field.CREATOR),
            1.equals(source.readInt()),
            1.equals(source.readInt()),
            source.readString())

    override fun compareTo(other: Option): Int {
        return title.compareTo(other.title)
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(title)
        dest?.writeTypedList(fields)
        dest?.writeInt((if (nodial) 1 else 0))
        dest?.writeInt((if (nosteps) 1 else 0))
        dest?.writeString(dtmf)
    }

}
/*
 * Config.kt
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
package net.thauvin.erik.android.tesremoteprogrammer.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Config(var params: Params, var opts: List<Option>) : Parcelable, Serializable, Comparable<Config> {
    companion object {
        private @JvmStatic val serialVersionUID: Long = 1

        @JvmField val CREATOR: Parcelable.Creator<Config> = object : Parcelable.Creator<Config> {
            override fun createFromParcel(source: Parcel): Config = Config(source)
            override fun newArray(size: Int): Array<Config?> = arrayOfNulls(size)
        }
    }

    constructor() : this(Params(), emptyList<Option>())

    constructor(source: Parcel) : this(
            source.readParcelable<Params>(Params::class.java.classLoader),
            source.createTypedArrayList(Option.CREATOR))

    override fun compareTo(other: Config): Int {
        return params.name.compareTo(other.params.name)
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeParcelable(params, 0)
        dest?.writeTypedList(opts)
    }
}
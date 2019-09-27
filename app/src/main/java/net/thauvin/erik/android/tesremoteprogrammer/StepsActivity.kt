/*
 * StepsActivity.kt
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
package net.thauvin.erik.android.tesremoteprogrammer

import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.v13.app.FragmentStatePagerAdapter
import android.support.v4.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_steps.indicator
import kotlinx.android.synthetic.main.activity_steps.pager
import java.util.ArrayList

class StepsActivity : FragmentActivity() {
    companion object {
        const val EXTRA_STEPS = "steps"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)

        pager.adapter = StepsAdapter(fragmentManager, intent.extras.getStringArrayList(EXTRA_STEPS))
        indicator.setViewPager(pager, pager.currentItem - 1)
        indicator.fades = false
    }

    private inner class StepsAdapter(
        fm: FragmentManager,
        steps: ArrayList<String>
    ) : FragmentStatePagerAdapter(fm) {
        private val steps = ArrayList(steps)

        override fun getItem(position: Int): Fragment = StepsFragment.create(position, steps)

        override fun getCount(): Int = steps.size
    }
}

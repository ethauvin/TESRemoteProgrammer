/*
 * StepsActivity.kt
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
package net.thauvin.erik.android.tesremoteprogrammer

import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.v13.app.FragmentStatePagerAdapter
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import com.viewpagerindicator.UnderlinePageIndicator
import java.util.*

class StepsActivity : FragmentActivity() {
    companion object {
        val EXTRA_STEPS = "steps"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)

        val pager = findViewById(R.id.pager) as ViewPager
        pager.adapter = StepsAdapter(fragmentManager, intent.extras.getStringArrayList(EXTRA_STEPS))
        val pageIndicator = findViewById(R.id.indicator) as UnderlinePageIndicator
        pageIndicator.setViewPager(pager, pager.currentItem - 1)
        pageIndicator.fades = false
    }

    private inner class StepsAdapter(fm: FragmentManager,
                                     steps: ArrayList<String>) : FragmentStatePagerAdapter(fm) {
        private val steps = ArrayList<String>(steps)

        override fun getItem(position: Int): Fragment {
            return StepsFragment.create(position, steps)
        }

        override fun getCount(): Int {
            return steps.size
        }
    }
}
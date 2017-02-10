/*
 * StepsFragment.kt
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
package net.thauvin.erik.android.tesremoteprogrammer

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_steps.*
import java.util.*

class StepsFragment : Fragment() {
    private var pageNumber: Int = 0

    companion object {
        val ARG_PAGE = "page"
        private val steps = ArrayList<String>()

        fun create(pageNumber: Int, steps: ArrayList<String>): StepsFragment {
            val fragment = StepsFragment()
            val args = Bundle()

            args.putInt(ARG_PAGE, pageNumber)
            fragment.arguments = args

            this.steps.clear()
            this.steps.addAll(steps)

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageNumber = arguments.getInt(ARG_PAGE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_steps, container, false) as ViewGroup
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        frag_steps_title.text = getString(R.string.title_template_step, pageNumber + 1, steps.size)
        frag_steps.text = steps[pageNumber]
    }
}

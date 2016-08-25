/*
 * StepsActivity.java
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
package net.thauvin.erik.android.tesremoteprogrammer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.ArrayList;

public class StepsActivity extends FragmentActivity {

    public static final String EXTRA_STEPS = "steps";

    private final ArrayList<String> steps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        steps.clear();
        steps.addAll(getIntent().getExtras().getStringArrayList(EXTRA_STEPS));

        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter pagerAdapter = new StepsAdapter(getFragmentManager(), steps);
        pager.setAdapter(pagerAdapter);
        final UnderlinePageIndicator pageIndicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
        pageIndicator.setViewPager(pager, pager.getCurrentItem() - 1);
        pageIndicator.setFades(false);
    }

    private class StepsAdapter extends FragmentStatePagerAdapter {
        private final ArrayList<String> stepsArray = new ArrayList<>();

        public StepsAdapter(FragmentManager fm, ArrayList<String> steps) {
            super(fm);

            stepsArray.clear();
            stepsArray.addAll(steps);
        }

        @Override
        public Fragment getItem(int position) {
            return StepsFragment.create(position, stepsArray);
        }

        @Override
        public int getCount() {
            return stepsArray.size();
        }
    }
}

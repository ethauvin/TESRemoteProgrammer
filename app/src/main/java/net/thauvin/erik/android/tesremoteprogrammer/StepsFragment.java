/*
 * StepsFragment.java
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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class StepsFragment extends Fragment {
    public static final String ARG_PAGE = "page";
    private static final ArrayList<String> stepsArray = new ArrayList<>();
    private int pageNumber;

    public StepsFragment() {
    }

    public static StepsFragment create(int pageNumber, ArrayList<String> steps) {
        final StepsFragment fragment = new StepsFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);

        stepsArray.clear();
        stepsArray.addAll(steps);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_steps, container, false);

        ((TextView) rootView.findViewById(R.id.frag_steps_title)).setText(
                getString(R.string.title_template_step, pageNumber + 1, stepsArray.size()));
        ((TextView) rootView.findViewById(R.id.frag_steps)).setText(stepsArray.get(pageNumber));
        return rootView;
    }
}

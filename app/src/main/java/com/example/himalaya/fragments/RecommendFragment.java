package com.example.himalaya.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseFragment;

public class RecommendFragment extends BaseFragment {
    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {

        //Fragment在inflate layout的时候attachToRoot一定要设为false 否则之后会重复执行 mContainer.addView(view) 然后就会报错
        View rootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);
        return rootView;
    }
}

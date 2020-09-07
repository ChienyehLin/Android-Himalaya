package com.example.himalaya.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;

import com.example.himalaya.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

public class IndicatorAdaptor extends CommonNavigatorAdapter {

    private final String[] mTitles;
    private OnIndicatorTabClickListener mOnTabClickListener;

    public IndicatorAdaptor(Context context) {
        mTitles = context.getResources().getStringArray(R.array.indicator_title);

    }

    @Override
    public int getCount() {
        if (mTitles != null) {
            return mTitles.length;
        }
        return 0;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
        simplePagerTitleView.setNormalColor(Color.GRAY);
        simplePagerTitleView.setSelectedColor(Color.WHITE);
        simplePagerTitleView.setText(mTitles[index]);
        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mTitles.setCurrentItem(index);
                //TODO:
                if (mOnTabClickListener != null) {
                    mOnTabClickListener.onTabClick(index);
                }
            }
        });

        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
        linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        linePagerIndicator.setColors(Color.WHITE);
        return linePagerIndicator;
    }

    public  void setOnIndicatorTabListener(OnIndicatorTabClickListener listener){
        this.mOnTabClickListener = listener;
    }
    public interface  OnIndicatorTabClickListener {
        void onTabClick(int index);
    }
}

package com.example.himalaya;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.nfc.Tag;
import android.os.Bundle;

import com.example.himalaya.adapters.IndicatorAdaptor;
import com.example.himalaya.adapters.MainContentAdapter;
import com.example.himalaya.utils.LogUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

public class MainActivity extends FragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MagicIndicator mMagicIndicator;
    private ViewPager mContentPager;
    private MainContentAdapter mMainContentAdapter;
    private IndicatorAdaptor mIndicatorAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();
        initEvent();
/*
        Map<String, String> map = new HashMap<String, String>();
        CommonRequest.getCategories(map, new IDataCallBack<CategoryList>() {
            @Override
            public void onSuccess(CategoryList categoryList) {
                List<Category>  categories= categoryList.getCategories();
                if (categories != null) {
                    //
                    int size = categories.size();
                    Log.d(TAG,"categories size ======> "+size);
                    for (Category category : categories) {
                        LogUtil.d(TAG,"category ===>"+category.getCategoryName());
                    }
                }

            }

            @Override
            public void onError(int i, String s) {
                    Log.e(TAG,"error code ===>"+i+"error message===>"+s);
            }
        });*/
    }

    private void initEvent() {
       mIndicatorAdaptor.setOnIndicatorTabListener(new IndicatorAdaptor.OnIndicatorTabClickListener() {
           @Override
           public void onTabClick(int index) {
               LogUtil.d(TAG,"click index is ===>"+index);
               if (mContentPager != null) {
                   mContentPager.setCurrentItem(index);
               }
           }
       });
    }

    private void initView() {
        mMagicIndicator = findViewById(R.id.magic_indicator);
        mMagicIndicator.setBackgroundColor(this.getResources().getColor(R.color.mainColor));//设置indicator颜色

        //创建Indicator的适配器
        mIndicatorAdaptor = new IndicatorAdaptor(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        //自我调节平分宽度
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(mIndicatorAdaptor);
        //设置要显示的内容


        //ViewPager
        mContentPager = this.findViewById(R.id.content_pager);

        //创建ViewPager的适配器
        FragmentManager fragmentManager = getSupportFragmentManager();
        mMainContentAdapter = new MainContentAdapter(fragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContentPager.setAdapter(mMainContentAdapter);

        //把ViewPager和Indicator 绑定到一起
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mContentPager);
    }

}
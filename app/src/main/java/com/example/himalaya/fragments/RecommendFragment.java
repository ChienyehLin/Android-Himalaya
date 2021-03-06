package com.example.himalaya.fragments;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.adapters.RecommendListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.presenters.RecommendPresenter;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback {
    private static final String TAG = RecommendFragment.class.getSimpleName();
    private View mRootView;
    private RecyclerView mRecommendRv;
    private RecommendListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader mUiLoader;



    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            new PriorityQueue<Integer>(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1-o2;
                }
            });
        }
        mUiLoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return  createSuccessView(layoutInflater,container);
            }
        };

        //Fragment在inflate layout的时候attachToRoot一定要设为false 否则之后会重复执行 mContainer.addView(view) 然后就会报错



        //先拿数据 TODO：重构代码
        //获取到逻辑层对象
        mRecommendPresenter = RecommendPresenter.getInstance();
        //先要设置通知接口的注册
        mRecommendPresenter.registerViewCallback(this);

        //获取推荐列表
        mRecommendPresenter.getRecommendList();
        //解绑
        if (mUiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
        }

        //返回View 给界面显示
        return mUiLoader;
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        //View 加载完成
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);

        //recycler view的使用

        //1.找到对应的控件
        mRecommendRv = mRootView.findViewById(R.id.recommend_list);
        //2.设置布局管理器  layoutmanager 3种
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecommendRv.setLayoutManager(linearLayoutManager);
        mRecommendRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(view.getContext(),5);
                outRect.left=UIUtil.dip2px(view.getContext(),5);
                outRect.bottom=UIUtil.dip2px(view.getContext(),5);
                outRect.right=UIUtil.dip2px(view.getContext(),5);

            }
        });
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //3.设置适配器
        mRecommendListAdapter = new RecommendListAdapter();
        mRecommendRv.setAdapter(mRecommendListAdapter);

        return mRootView;
    }


    /**
     * 获取推荐内容，其实就是猜你先换
     * 这个接口：3.10.6 获取猜你喜欢专辑
     */
    private void getRecommendData() {
        //封装参数
        Map<String, String> map = new HashMap<>();
        //这个参数表示一夜数据返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT + "");
        LogUtil.d(TAG, "requesting ====>" );
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                //UI是在主线程中回调
                LogUtil.d(TAG,"thread name====>"+Thread.currentThread().getName());
                //数据获取成功
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    if (albumList != null) {
                        LogUtil.d(TAG, "size ====>" + albumList.size());
                    }
                    //数据回来以后 我们要去更新UI
                }
            }


            @Override
            public void onError(int i, String s) {
                //数据获取出错
                LogUtil.d(TAG, "error ====>" + i);
                LogUtil.d(TAG, "error message ====>" + s);
            }
        });
    }
    @Override
    public void onRecommendListLoad(List<Album> result) {
        //当我们获取到推荐内容的时候，这个方法就会被调用，（成功了）
        //数据回来以后更新UI
        //把数据设置给适配器并且更新

        mRecommendListAdapter.setData(result);
        Log.d(TAG,"onSuccess");
        mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        Log.d(TAG,"onNetworkError");
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        Log.d(TAG,"onEmpty");
        mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        Log.d(TAG,"onLoading");
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消接口的注册
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }
}

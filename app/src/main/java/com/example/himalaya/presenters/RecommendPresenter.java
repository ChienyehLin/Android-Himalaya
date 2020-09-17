package com.example.himalaya.presenters;

import com.example.himalaya.interfaces.IRecommendPresenter;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendPresenter implements IRecommendPresenter {

    private static RecommendPresenter sInstance = null;
    private String TAG = "RecommendPresenter";
    private List<IRecommendViewCallback> mCallbacks = new ArrayList<>();

    /**
     * 获取单例对象，懒汉式单例模式
     *
     * @return
     */
    public static RecommendPresenter getInstance() {
        if (sInstance == null) {      //
            synchronized (RecommendPresenter.class) {
                if (sInstance == null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    private RecommendPresenter() {

    }

    /**
     * 获取推荐内容，其实就是猜你先换
     * 这个接口：3.10.6 获取猜你喜欢专辑
     */
    @Override
    public void getRecommendList() {
        //封装参数
        updateLoading();
        Map<String, String> map = new HashMap<>();
        //这个参数表示一夜数据返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT + "");
        LogUtil.d(TAG, "requesting ====>");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                //UI是在主线程中回调
                LogUtil.d(TAG, "thread name====>" + Thread.currentThread().getName());
                //数据获取成功
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    if (albumList != null) {
                        LogUtil.d(TAG, "size ====>" + albumList.size());
                    }
                    //数据回来以后，我们要去更新Ui
                    handleRecommendResult(albumList);

                }
            }


            @Override
            public void onError(int i, String s) {
                //数据获取出错
                LogUtil.d(TAG, "error ====>" + i);
                LogUtil.d(TAG, "error message ====>" + s);
                handleError();
            }
        });
    }

    private void handleError() {

        if (mCallbacks != null) {
            for (IRecommendViewCallback callback : mCallbacks) {
                callback.onNetworkError();
            }
        }
    }


    private void handleRecommendResult(List<Album> albumList) {
        if (albumList != null) {
            if (albumList.size() == 0) {
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onEmpty();
                }
            }else {
                //通知Ui
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onRecommendListLoad(albumList);
                }
            }
        }
    }

    private void updateLoading() {
        for (IRecommendViewCallback callback : mCallbacks) {
            callback.onLoading();
        }
    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }
}

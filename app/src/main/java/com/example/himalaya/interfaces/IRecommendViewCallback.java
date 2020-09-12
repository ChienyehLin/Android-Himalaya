package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface IRecommendViewCallback {
    /**
     * 获取推荐的内容的结果
     */
    void onRecommendListLoad(List<Album> result);

    /**
     * 加载更多
     */
    void onLoadMore(List<Album> result);

    /**
     * 上接加载更多
     */
    void onRefreshMore(List<Album> result);
}

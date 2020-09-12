package com.example.himalaya.interfaces;

//presenter代表逻辑层 获取推荐内容
public interface IRecommendPresenter {
    /**
     * 获取推荐内容
     */
    void getRecommendList();

    /**
     * 下拉刷新更多内容
     */
    void Pull2RefreshMore();

    /**
     * 上拉加载更多
     **/
    void loadMore();

    /**
     * 这个方法用于注册UI的回调
     * @param callback
     */
    void registerViewCallback(IRecommendViewCallback callback);

    /**
     * 取消UI的回调注册
     * @param callback
     */
    void unRegisterViewCallback(IRecommendViewCallback callback);
}
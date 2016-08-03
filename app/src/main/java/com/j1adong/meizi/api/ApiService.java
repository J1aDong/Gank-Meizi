package com.j1adong.meizi.api;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by J1aDong on 16/7/21.
 */
public interface ApiService {

    //http://gank.io/api/search/query/listview/category/福利/count/50/page/1
    @GET("search/query/listview/category/福利/count/50/page/1")
    Observable<String> getMeizis();

    //http://gank.io/api/day/2015/08/06 每日数据
    @GET("day/{year}/{month}/{day}")
    Observable<String> getGankByDate(@Path("year") int year, @Path("month") int month, @Path("day") int day);

    //http://gank.io/api/day/history 获取历史日期
    @GET("day/history")
    Observable<String> getHistoryDate();
}

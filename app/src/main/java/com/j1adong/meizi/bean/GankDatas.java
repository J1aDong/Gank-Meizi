package com.j1adong.meizi.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J1aDong on 16/7/22.
 */
public class GankDatas {
    @SerializedName("福利")
    private List<GankData> meizis;

    @SerializedName("前端")
    private List<GankData> fronts;

    @SerializedName("休息视频")
    private List<GankData> videos;

    @SerializedName("Android")
    private List<GankData> androids;

    @SerializedName("App")
    private List<GankData> apps;

    @SerializedName("iOS")
    private List<GankData> ioss;

    private List<GankData> gankDatas = new ArrayList<>();

    public List<GankData> getMeizis() {
        return meizis;
    }

    public void setMeizis(List<GankData> meizis) {
        this.meizis = meizis;
    }

    public List<GankData> getFronts() {
        return fronts;
    }

    public void setFronts(List<GankData> fronts) {
        this.fronts = fronts;
    }

    public List<GankData> getVideos() {
        return videos;
    }

    public void setVideos(List<GankData> videos) {
        this.videos = videos;
    }

    public List<GankData> getAndroids() {
        return androids;
    }

    public void setAndroids(List<GankData> androids) {
        this.androids = androids;
    }

    public List<GankData> getApps() {
        return apps;
    }

    public void setApps(List<GankData> apps) {
        this.apps = apps;
    }

    public List<GankData> getIoss() {
        return ioss;
    }

    public void setIoss(List<GankData> ioss) {
        this.ioss = ioss;
    }

    public List<GankData> getGankDatas() {
        gankDatas.clear();
        if (null != androids) {
            gankDatas.addAll(androids);
        }
        if (null != ioss) {
            gankDatas.addAll(ioss);
        }
        if (null != meizis) {
            gankDatas.addAll(meizis);
        }
        if (null != fronts) {
            gankDatas.addAll(fronts);
        }
        if (null != videos) {
            gankDatas.addAll(videos);
        }
        if (null != apps) {
            gankDatas.addAll(apps);
        }
        return gankDatas;
    }

}

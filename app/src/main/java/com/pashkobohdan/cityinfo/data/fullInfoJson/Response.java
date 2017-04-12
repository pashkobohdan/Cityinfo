package com.pashkobohdan.cityinfo.data.fullInfoJson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bohdan on 12.04.17.
 */

public class Response {

    @SerializedName("geonames")
    @Expose
    private List<CityInfo> geonames = new LinkedList<>();

    public List<CityInfo> getGeonames() {
        return geonames;
    }

    public void setGeonames(List<CityInfo> geonames) {
        this.geonames = geonames;
    }

}
package com.homanhuang.meowfest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Homan on 2/28/2018.
 */

public interface meowApiInterface {

    // https://chex-triplebyte.herokuapp.com/api/cats?page=0
    //  /api/cats?page=0
    @GET("/api/cats")
    Call<List<Meow>> getMeowsPage(@QueryMap Map<String, String> options);

}

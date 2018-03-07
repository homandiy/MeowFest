package com.homanhuang.meowfest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    /* Log tag and shortcut */
    final static String TAG = "MYLOG SPACEX";
    public static void ltag(String message) { Log.i(TAG, message); }

    /* Toast shortcut */
    public static void msg(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 201; // any code you want.
    public void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                ltag("Permission is granted");
            } else {
                ltag("Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.INTERNET,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        ltag("Permission:" + permissions.toString());

        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                msg(this, "Permission Granted!");

                break;
        }
    }

    //=====================================================================
    /*
        Interpret the Website
     */
    public static final String BASE_URL = "https://chex-triplebyte.herokuapp.com";

    // Add the interceptor to OkHttpClient
    OkHttpClient.Builder mClient = new OkHttpClient.Builder()
            .addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request newRequest = chain.request()
                            .newBuilder()
                            .addHeader("User-Visitor", "Meow-App").build();
                    ltag("new request: "+newRequest.toString());
                    return chain.proceed(newRequest);
                }
            });

    OkHttpClient client = mClient.build();

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create();

    Retrofit visitor = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    meowApiInterface service;
    //=====================================================================

    // Request Results
    int page=0;

    /*
        Get data by page number
     */
    private void getPageData() {
        Map<String, String> data = new HashMap<>();
        data.put("page", String.valueOf(page));

        setTitle("Meow Fest Page "+ (page+1) );
        //get date
        filterLaunch(data);
    }
    /*
       Filter Request
    */
    private void filterLaunch(Map data) {
        final Call<List<Meow>> filterLaunch = service.getMeowsPage(data);
        filterLaunch.enqueue(new Callback<List<Meow>>() {
            @Override
            public void onResponse(Call<List<Meow>> call, Response<List<Meow>> response) {
                if ( response.isSuccessful() ) {

                    // request successful (status code 200, 201)
                    List<Meow> meowResults = response.body();
                    ltag("Result Size: "+meowResults.size());

                    //load recyclerview
                    loadRecycler(meowResults);

                } else {
                    //request not successful (like 400,401,403 etc)
                    //Handle errors
                    ltag("Http Error: "+response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Meow>> call, Throwable t) {
                ltag("Fail "+t.toString());
            }
        });
    }



    //=====================================================================
    /*
        Layout Variables
     */
    RecyclerView meowRV;
    MeowRecyclerAdapter meowRecyclerAdapter;
    int previousTotal = 0;
    boolean loading = true;
    int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    LinearLayoutManager vLayoutManager;
    List<Meow> recyList;
    //=====================================================================
    /*
        Recycler function
     */
    private void loadRecycler(List<Meow> newlist) {
        recyList.addAll(newlist);

        //Layout manager
        vLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        meowRV.setLayoutManager(vLayoutManager);

        //Add recycler view = RV
        meowRecyclerAdapter = new MeowRecyclerAdapter(MainActivity.this, recyList);
        meowRV.setAdapter(meowRecyclerAdapter);

        meowRV.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = meowRV.getChildCount();
                totalItemCount = vLayoutManager.getItemCount();
                firstVisibleItem = vLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached
                    ltag("Yaeye! End called");
                    page+=1;
                    // Do something
                    getPageData();

                    loading = true;
                }
            }
        });


    }

    //=====================================================================
    /*
        OnCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        meowRV = (RecyclerView) findViewById(R.id.meowRV);
        recyList = new ArrayList<Meow>();

        /*
            BSON -> JAVA
         */
        service = visitor.create(meowApiInterface.class);

        //get data by filter
        getPageData();
        /*
            END PARSING
         */

    }

}


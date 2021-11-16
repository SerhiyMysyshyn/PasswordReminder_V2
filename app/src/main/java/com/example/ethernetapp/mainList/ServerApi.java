package com.example.ethernetapp.mainList;

import com.example.ethernetapp.Program;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServerApi {

    @GET("getAllProgramsData.php")
    Single<List<Program>> getAllProgramData();

    @GET("getProgramsDataByUserID.php?")
    Single<List<Program>> getProgramsDataById(@Query("userId") String userID);

}

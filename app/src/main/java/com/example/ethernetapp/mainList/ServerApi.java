package com.example.ethernetapp.mainList;

import com.example.ethernetapp.Program;
import com.example.ethernetapp.roomDB.User;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServerApi {

    @GET("getAllProgramsData.php")
    Single<List<Program>> getAllProgramData();

    @GET("getProgramsDataByUserID.php?")
    Single<List<Program>> getProgramsDataById(@Query("userId") String userID);

    @GET("getUserByUserID.php?")
    Single<User> getUserByUserId(@Query("userId") String userID, @Query("password") String password);

    @FormUrlEncoded
    @POST("registerNewUser.php")
    Single<String> registerNewUser(@Field("userId") String userID,
                                   @Field("username") String username,
                                   @Field("email") String email,
                                   @Field("password") String password);

    @FormUrlEncoded
    @POST("addNewProgramData.php")
    Single<String> addNewProgramData(@Field("userId") String userID,
                                    @Field("programName") String programName,
                                    @Field("email") String email,
                                    @Field("password") String password,
                                    @Field("description") String description,
                                    @Field("timeData") String timeData,
                                    @Field("dateData") String dateData);



}

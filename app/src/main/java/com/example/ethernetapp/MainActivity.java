package com.example.ethernetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ethernetapp.mainList.DataManager;
import com.example.ethernetapp.mainList.RecyclerClickListener;
import com.example.ethernetapp.mainList.ServerApi;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    SearchView searchView;
    public static Program[] ProgramData = new Program[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        progressBar = (ProgressBar)findViewById(R.id.progresBar_mainList);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchView = (SearchView) findViewById(R.id.searchView);
            int text_id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            int searchImage_id = searchView.getContext().getResources().getIdentifier("android:id/search_button", null, null);
            int closeImage_id = searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);

            TextView textView = (TextView) searchView.findViewById(text_id);
            ImageView imageView = (ImageView)searchView.findViewById(searchImage_id);
            ImageView imageView2 = (ImageView)searchView.findViewById(closeImage_id);

            textView.setTextColor(Color.WHITE);
            imageView.setImageDrawable(getDrawable(android.R.drawable.ic_menu_search));
            imageView2.setImageDrawable(getDrawable(android.R.drawable.ic_menu_close_clear_cancel));

        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true); // to improve performance

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://serhiymysyshyn.zzz.com.ua/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();

        ServerApi serverApi = retrofit.create(ServerApi.class);

        //serverApi.getAllProgramData()
        serverApi.getProgramsDataById("serhiymysyshyn@gmail.com")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Program>>(){
                    @Override
                    public void onSuccess(@NonNull List<Program> programs) {
                        progressBar.setVisibility(View.INVISIBLE);
                        ProgramData = new Program[programs.size()];
                        for (int i=0; i<programs.size(); i++) {
                            ProgramData[i] = new Program(
                                    programs.get(i).get(Program.Field.ID),
                                    programs.get(i).get(Program.Field.USERID),
                                    programs.get(i).get(Program.Field.NAME),
                                    programs.get(i).get(Program.Field.IMG),
                                    programs.get(i).get(Program.Field.EMAIL),
                                    programs.get(i).get(Program.Field.PASSWORD),
                                    programs.get(i).get(Program.Field.DESCRIPTION),
                                    programs.get(i).get(Program.Field.TIME),
                                    programs.get(i).get(Program.Field.DATE));
                        }
                        recyclerView.setAdapter(new DataManager());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("[ - ] ERROR :" + e);
                    }
                });

        recyclerView.addOnItemTouchListener(new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, ShowPassword.class);
                intent.putExtra(ShowPassword.ID, ProgramData[position].getId());
                startActivity(intent);
            }
        }));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getApplicationContext(), "Refreshing page", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }

}
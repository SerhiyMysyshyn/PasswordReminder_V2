package com.example.ethernetapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ethernetapp.mainList.DataManager;
import com.example.ethernetapp.mainList.RecyclerClickListener;
import com.example.ethernetapp.mainList.ServerApi;
import com.example.ethernetapp.roomDB.UserDAO;
import com.example.ethernetapp.roomDB.UserDelegate;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity{
    public final static String USERID = "USERID";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    public static boolean fragmentState = false;
    public DataManager dataManagerAdapter;
    public List<Program> ProgramDataList;
    TextView emptyView;
    SearchView searchView;
    ImageButton accSettings, sortList, addNewRecord;
    ImageView imageView_ethernet_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// Створюю об'єкти класів для доступу до контенту
        ServerApi serverApi = ServerRetrofit.getRetrofit().create(ServerApi.class);
        String ExtraUserID = getIntent().getStringExtra(USERID);
        UserCabinetFragment userCabinetFragment = new UserCabinetFragment();

        searchView = (SearchView) findViewById(R.id.mainSearchView);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        progressBar = (ProgressBar)findViewById(R.id.progresBar_mainList);
        accSettings = (ImageButton) findViewById(R.id.account_settings_button);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        emptyView = (TextView) findViewById(R.id.empty);
        sortList = (ImageButton) findViewById(R.id.Button_sort_list);
        imageView_ethernet_error = (ImageView) findViewById(R.id.imageView_ethernet_error);
        addNewRecord = (ImageButton) findViewById(R.id.addNewRecordBTN);


// Кастомізація SearchView
        int text_id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        int searchImage_id = searchView.getContext().getResources().getIdentifier("android:id/search_button", null, null);
        int closeImage_id = searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        TextView textView = (TextView) searchView.findViewById(text_id);
        ImageView imageView = (ImageView)searchView.findViewById(searchImage_id);
        ImageView imageView2 = (ImageView)searchView.findViewById(closeImage_id);
        textView.setTextColor(Color.WHITE);
        imageView.setImageDrawable(getDrawable(android.R.drawable.ic_menu_search));
        imageView2.setImageDrawable(getDrawable(android.R.drawable.ic_menu_close_clear_cancel));

// Створюю RecyclerView
        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        buildRecyclerView(serverApi, ExtraUserID);

// Оновлення списку за допомогою свайпу вниз
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                recyclerView.getRecycledViewPool().clear();
                    buildRecyclerView(serverApi, ExtraUserID);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

// Додавання нового запису
        addNewRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNewRecord.class);
                startActivity(intent);
            }
        });

// Сортування списку
        sortList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), searchView.getQuery().toString(), Toast.LENGTH_LONG).show();
            }
        });

// Перегляд фрагменту "персональний кабінет"
        accSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentState){
                    accSettings.setBackgroundColor(getResources().getColor(R.color.colorMainBackground));
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                            .remove(userCabinetFragment)
                            .commit();
                    fragmentState = false;
                } else {
                    accSettings.setBackground(getResources().getDrawable(R.drawable.custom_search_area));
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                            .add(R.id.container, userCabinetFragment)
                            .commit();
                    fragmentState = true;
                }
            }
        });

// Реалізація натиску на елемент списку
        recyclerView.addOnItemTouchListener(new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView itemIdentificator = (TextView) view.findViewById(R.id.item_id);
                Intent intent = new Intent(MainActivity.this, ShowPassword.class);
                intent.putExtra(ShowPassword.ID, itemIdentificator.getText().toString());
                startActivity(intent);
            }
        }));

// Реалізація пошуку записів
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Виклик методу фільтрації наявних записів
                filter(newText);
                return false;
            }
        });



    }

// Метод фільтрації наявних записів
    public void filter(String text) {
        List<Program> filteredlist = new ArrayList<>();
        for (Program item : ProgramDataList) {
            if (item.getProgramName().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            dataManagerAdapter.filterList(filteredlist);
        }
    }

// Метод для побудови списку з даними із сервера
    private void buildRecyclerView(ServerApi serverApi, String ExtraUserID) {
        ProgramDataList = new ArrayList<>();
        serverApi.getProgramsDataById(ExtraUserID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Program>>(){
                    @Override
                    public void onSuccess(@NonNull List<Program> programs) {
                        recyclerView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        emptyView.setVisibility(View.INVISIBLE);
                        imageView_ethernet_error.setVisibility(View.INVISIBLE);
                        for (int i=0; i<programs.size(); i++) {
                            ProgramDataList.add(new Program(
                                    programs.get(i).get(Program.Field.ID),
                                    programs.get(i).get(Program.Field.USERID),
                                    programs.get(i).get(Program.Field.NAME),
                                    programs.get(i).get(Program.Field.EMAIL),
                                    programs.get(i).get(Program.Field.PASSWORD),
                                    programs.get(i).get(Program.Field.DESCRIPTION),
                                    programs.get(i).get(Program.Field.TIME),
                                    programs.get(i).get(Program.Field.DATE)
                            ));
                        }
                        dataManagerAdapter = new DataManager(ProgramDataList, MainActivity.this);
                        recyclerView.setAdapter(dataManagerAdapter);
                    }
                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("#### ERROR #### - " + e);
                        if (String.valueOf(e).equals("java.util.NoSuchElementException")){
                            recyclerView.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            emptyView.setVisibility(View.VISIBLE);
                        }else{
                            recyclerView.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            emptyView.setVisibility(View.VISIBLE);
                            emptyView.setText("Помилка завантаження даних");
                            imageView_ethernet_error.setVisibility(View.VISIBLE);
                        }
                    }});
    }


    /*@Override
    protected void onRestart(){
        super.onRestart();
        recyclerView.getRecycledViewPool().clear();
        buildRecyclerView(serverApi, t);
    }*/

}
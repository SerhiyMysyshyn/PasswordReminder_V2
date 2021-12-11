package com.example.ethernetapp.authorization;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.ethernetapp.R;
import com.example.ethernetapp.ServerRetrofit;
import com.example.ethernetapp.mainList.ServerApi;
import com.example.ethernetapp.roomDB.User;
import com.example.ethernetapp.roomDB.UserDAO;
import com.example.ethernetapp.roomDB.UserDelegate;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class Register extends AppCompatActivity {

    AutoCompleteTextView regUsername, regEmail, regPassword;
    Button registerUser, button_to_login;
    LinearLayout registerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regUsername = (AutoCompleteTextView) findViewById(R.id.editTextTextPersonName2);
        regEmail = (AutoCompleteTextView) findViewById(R.id.editTextTextPersonName4);
        regPassword = (AutoCompleteTextView) findViewById(R.id.editTextTextPersonName5);
        button_to_login = (Button) findViewById(R.id.button_to_login);
        registerUser = (Button) findViewById(R.id.button_register);
        registerLayout = (LinearLayout) findViewById(R.id.registerLayout);

        UserDAO userDAO = ((UserDelegate)getApplicationContext()).getUserDatabase().getUserDAO();

        ServerApi serverApi = ServerRetrofit.getRetrofit().create(ServerApi.class);

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userID = regEmail.getText().toString();
                String userName = regUsername.getText().toString();
                String userEmail = regEmail.getText().toString();
                String userPassword = regPassword.getText().toString();

                serverApi.registerNewUser(userID,userName, userEmail, userPassword)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableSingleObserver<String>() {
                            @Override
                            public void onSuccess(@NonNull String s) {
                                switch (s){
                                    case "User registered successfully":
                                        User userAfterSuccessRegistration = new User();
                                        userAfterSuccessRegistration.id = 0;
                                        userAfterSuccessRegistration.userId = userID;
                                        userAfterSuccessRegistration.username = userName;
                                        userAfterSuccessRegistration.email = userEmail;
                                        userAfterSuccessRegistration.password = userPassword;

                                        userDAO.insertUser(userAfterSuccessRegistration)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new SingleObserver<Long>() {
                                                    @Override
                                                    public void onSubscribe(@NonNull Disposable d) {

                                                    }

                                                    @Override
                                                    public void onSuccess(@NonNull Long aLong) {
                                                        System.out.println("#### SUCCESS #### - User added to Room BD after registration (if checkbox isChecked=true)");
                                                    }

                                                    @Override
                                                    public void onError(@NonNull Throwable e) {
                                                        System.out.println("#### ERROR #### - " + e);
                                                    }
                                                });

                                        Intent intent = new Intent(Register.this, login.class);
                                        Register.this.startActivity(intent);
                                        Register.this.finish();
                                        break;
                                    case "This user is already registered":
                                        registerLayout.setBackground(getResources().getDrawable(R.drawable.custom_item_background_on_error));
                                        break;
                                }

                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                System.out.println("#### ERROR #### - " + e);
                            }
                        });

            }
        });

        button_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, login.class);
                Register.this.startActivity(intent);
                Register.this.finish();
            }
        });

    }

}
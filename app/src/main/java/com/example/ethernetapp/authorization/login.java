package com.example.ethernetapp.authorization;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ethernetapp.MainActivity;
import com.example.ethernetapp.R;
import com.example.ethernetapp.ServerRetrofit;
import com.example.ethernetapp.ShowPassword;
import com.example.ethernetapp.mainList.ServerApi;
import com.example.ethernetapp.roomDB.User;
import com.example.ethernetapp.roomDB.UserDAO;
import com.example.ethernetapp.roomDB.UserDelegate;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class login extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    AutoCompleteTextView loginPassword;
    TextView loginEmail;
    Button loginButton, registerButton;
    LinearLayout loginLayout_ethernet_error, loginLayout;
    private String userID;
    private String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.login_mainLayout);
        loginEmail = (TextView) findViewById(R.id.login_email);
        loginPassword = (AutoCompleteTextView) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.button_login);
        loginLayout = (LinearLayout) findViewById(R.id.loginLayout);
        loginLayout_ethernet_error = (LinearLayout) findViewById(R.id.loginLayout_ethernet_error);
        registerButton = (Button) findViewById(R.id.button_to_register);

        UserDAO userDAO = ((UserDelegate)getApplicationContext()).getUserDatabase().getUserDAO();

        userDAO.getUserById(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull User user) {
                        loginEmail.setText(user.getUserId());
                        loginPassword.setText(user.getPassword());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        loginEmail.setText("");
                        loginPassword.setText("");
                        System.out.println("#### ERROR #### - " + e);
                    }
                });

        if (isNetworkAvailable(getApplicationContext())) {

            ServerApi serverApi = ServerRetrofit.getRetrofit().create(ServerApi.class);

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userID = loginEmail.getText().toString();
                    userPassword = loginPassword.getText().toString();
                    serverApi.getUserByUserId(userID, userPassword)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new DisposableSingleObserver<User>() {
                                @Override
                                public void onSuccess(@NonNull User user) {
                                    if (user.getUserId().equals(loginEmail.getText().toString())
                                            && user.getPassword().equals(loginPassword.getText().toString())){
                                        // Якщо авторизація успішна - записати користувача в локальну базу
                                        User inputUser = new User();
                                        inputUser.id = 0;
                                        inputUser.userId = user.getUserId();
                                        inputUser.username = user.getUsername();
                                        inputUser.email = user.getEmail();
                                        inputUser.password = user.getPassword();


                                        userDAO.insertUser(inputUser)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new SingleObserver<Long>() {
                                                    @Override
                                                    public void onSubscribe(@NonNull Disposable d) {

                                                    }

                                                    @Override
                                                    public void onSuccess(@NonNull Long aLong) {
                                                        System.out.println("#### SUCCESS #### - User added to Room DB");
                                                    }

                                                    @Override
                                                    public void onError(@NonNull Throwable e) {
                                                        System.out.println("#### ERROR #### - " + e);
                                                    }
                                                });


                                        Intent intent = new Intent(login.this, MainActivity.class);
                                        intent.putExtra(MainActivity.USERID, userID);
                                        login.this.startActivity(intent);
                                        login.this.finish();

                                    }
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    System.out.println("#### ERROR #### - " + e);
                                    if (String.valueOf(e).equals("java.util.NoSuchElementException")) {
                                        loginLayout.setBackground(getResources().getDrawable(R.drawable.custom_item_background_on_error));
                                        loginButton.setBackground(getResources().getDrawable(R.drawable.custom_item_background_on_error));
                                        Toast.makeText(getApplicationContext(), "Ви ввели неправильний логін або пароль!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            });


            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(login.this, Register.class);
                    startActivity(intent);
                }
            });
        } else {
            loginLayout_ethernet_error.setVisibility(View.VISIBLE);
            coordinatorLayout.setBackground(getResources().getDrawable(R.drawable.background_error));
        }



    }

    public static boolean isNetworkAvailable(final Context context)  {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        login.this.finish();
    }

}
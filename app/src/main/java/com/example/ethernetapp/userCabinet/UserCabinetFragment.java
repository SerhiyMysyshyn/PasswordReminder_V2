package com.example.ethernetapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ethernetapp.authorization.login;
import com.example.ethernetapp.roomDB.User;
import com.example.ethernetapp.roomDB.UserDAO;
import com.example.ethernetapp.roomDB.UserDelegate;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UserCabinetFragment extends Fragment {

    private String userID;
    private String Username;
    private String Email;
    private String Password;

    TextView userName, userEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_cabinet, container, false);

        userName = (TextView) view.findViewById(R.id.userNameFull);
        userEmail = (TextView) view.findViewById(R.id.userEmail);

        UserDAO userDAO = ((UserDelegate)getActivity().getApplicationContext()).getUserDatabase().getUserDAO();

        userDAO.getUserById(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull User user) {
                        String fullNameInDB = user.getUsername();
                        String emailInDB = user.getEmail();

                        Username = user.getUsername();
                        Email = user.getEmail();
                        Password = user.getPassword();

                        userName.setText(fullNameInDB);
                        userEmail.setText(emailInDB);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("#### ERROR #### - " + e);
                    }
                });

        Button button = (Button) view.findViewById(R.id.button_exit_profile);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Вийти з облікового запису?");
                builder.setCancelable(false);
                builder.setNegativeButton("Ні", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i){
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Так", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i){
                        User deletedUser = new User();
                        deletedUser.id = 0;
                        deletedUser.userId = userID;
                        deletedUser.username = Username;
                        deletedUser.email = Email;
                        deletedUser.password = Password;

                        userDAO.deleteUser(deletedUser)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<Integer>() {
                                    @Override
                                    public void onSubscribe(@NonNull Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(@NonNull Integer integer) {

                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Toast.makeText(getActivity().getApplicationContext(), ""+e,Toast.LENGTH_LONG).show();
                                    }
                                });

                        Toast.makeText(getActivity().getApplicationContext(), "Ви успішно вийшли із свого облікового запису!",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), login.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawableResource(R.color.colorAttention);
                alertDialog.show();

            }
        });

        return view;

    }
}
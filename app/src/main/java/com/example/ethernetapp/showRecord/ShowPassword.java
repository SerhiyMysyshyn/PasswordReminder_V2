package com.example.ethernetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ethernetapp.mainList.ServerApi;
import com.example.ethernetapp.mainList.setBackgroundColor;
import com.example.ethernetapp.mainList.setProgramImage;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ShowPassword extends AppCompatActivity{
    public final static String ID = "ID";
    public Program mProgram;

    private RelativeLayout relativeLayout;
    private TextView mName, mEmail, mPassword, mDescription, mTime, mDate;
    private ImageView mImg;
    ImageButton exit, edit_btn, delete_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_password);
        mProgram = Program.getItem(getIntent().getStringExtra(ID));
        ServerApi serverApi = ServerRetrofit.getRetrofit().create(ServerApi.class);

        relativeLayout = (RelativeLayout) findViewById(R.id.showActivity_background);
        mName = (TextView) findViewById(R.id.textView_name);
        mEmail = (TextView) findViewById(R.id.textView_email);
        mPassword = (TextView) findViewById(R.id.textView_password);
        mDescription = (TextView) findViewById(R.id.textView_description);
        mTime = (TextView) findViewById(R.id.textView_time);
        mDate = (TextView) findViewById(R.id.textView_date);
        mImg = (ImageView)findViewById(R.id.imageView);
        exit = (ImageButton) findViewById(R.id.button_exit_to_main);
        edit_btn = (ImageButton) findViewById(R.id.button_edit_record);
        delete_btn = (ImageButton) findViewById(R.id.button_delete_record);

        int id = Integer.parseInt(mProgram.get(Program.Field.ID));

        mName.setText(mProgram.get(Program.Field.NAME));
        mEmail.setText(mProgram.get(Program.Field.EMAIL));
        mPassword.setText(mProgram.get(Program.Field.PASSWORD));
        mDescription.setText(mProgram.get(Program.Field.DESCRIPTION));
        mTime.setText(mProgram.get(Program.Field.TIME));
        mDate.setText(mProgram.get(Program.Field.DATE));

        mImg.setBackgroundResource(setProgramImage.setImage(mProgram.get(Program.Field.NAME)));
        relativeLayout.setBackgroundColor(getResources().getColor(setBackgroundColor.setColor(mProgram.get(Program.Field.NAME))));

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverApi.deleteRecord(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableSingleObserver<String>() {
                            @Override
                            public void onSuccess(@NonNull String s) {
                                Intent intent = new Intent(ShowPassword.this, MainActivity.class);
                                ShowPassword.this.startActivity(intent);
                                ShowPassword.this.finish();
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

}
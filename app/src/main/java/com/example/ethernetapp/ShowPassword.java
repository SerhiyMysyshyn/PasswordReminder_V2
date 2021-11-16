package com.example.ethernetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowPassword extends AppCompatActivity {
    public final static String ID = "ID";
    public Program mProgram;

    private TextView mUserID, mId, mName, mEmail, mPassword, mDescription, mTime, mDate;
    private ImageView mImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_password);

        mProgram = Program.getItem(getIntent().getIntExtra(ID, 0));

        mUserID = (TextView) findViewById(R.id.textView_userID);
        mId = (TextView) findViewById(R.id.textView_id);
        mName = (TextView) findViewById(R.id.textView_name);
        mEmail = (TextView) findViewById(R.id.textView_email);
        mPassword = (TextView) findViewById(R.id.textView_password);
        mDescription = (TextView) findViewById(R.id.textView_description);
        mTime = (TextView) findViewById(R.id.textView_time);
        mDate = (TextView) findViewById(R.id.textView_date);

        mImg = (ImageView)findViewById(R.id.imageView);

        mUserID.setText(mProgram.get(Program.Field.USERID));
        mId.setText(mProgram.get(Program.Field.ID));
        mName.setText(mProgram.get(Program.Field.NAME));
        mEmail.setText(mProgram.get(Program.Field.EMAIL));
        mPassword.setText(mProgram.get(Program.Field.PASSWORD));
        mDescription.setText(mProgram.get(Program.Field.DESCRIPTION));
        mTime.setText(mProgram.get(Program.Field.TIME));
        mDate.setText(mProgram.get(Program.Field.DATE));

        mImg.setBackgroundResource(Integer.parseInt(mProgram.get(Program.Field.IMG)));

    }
}
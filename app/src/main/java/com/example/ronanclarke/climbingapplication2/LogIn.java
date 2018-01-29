package com.example.ronanclarke.climbingapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by francisclarke on 25/01/2018.
 */

public class LogIn extends AppCompatActivity
{
    Button logOut,uploadImage,displayImage;
    private FirebaseAuth mAuth;
    TextView userName;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        mAuth = FirebaseAuth.getInstance();
        logOut = (Button)findViewById(R.id.logOutBT);
        userName = (TextView)findViewById(R.id.homeTV);
        uploadImage = (Button)findViewById(R.id.uploadImageBT);
        displayImage = (Button)findViewById(R.id.displayImageBT);

        //check if the user is logged in
        if(mAuth.getCurrentUser() == null)
        {
            //User not logged in
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }

        //get the display name of current user
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null)
        {
            userName.setText("Welcome, "+ user.getDisplayName());
        }

        logOut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               startActivity(new Intent(getApplicationContext(),UploadImage.class));
            }
        });

        displayImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // startActivity(new Intent(getApplicationContext(),DisplayImage.class));

            }
        });
    }
}

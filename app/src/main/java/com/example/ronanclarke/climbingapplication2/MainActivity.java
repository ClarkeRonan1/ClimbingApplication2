package com.example.ronanclarke.climbingapplication2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private EditText userEmail,userPassword,userName;
    private Button login,register;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        login = (Button)findViewById(R.id.loginBT);
        register = (Button)findViewById(R.id.registerBT);
        userEmail = (EditText)findViewById(R.id.userEmailET);
        userPassword = (EditText)findViewById(R.id.userPasswordET);
        userName = (EditText)findViewById(R.id.userNameET);

        //Check if user Signed in
        if(mAuth.getCurrentUser()!= null)
        {
            //User not logged in so continue
            finish();
            startActivity(new Intent(getApplicationContext(),LogIn.class));
        }

        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String getEmail = userEmail.getText().toString().trim();
                String getPassword = userPassword.getText().toString().trim();
                signIn(getEmail,getPassword);
            }
        });
        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String getEmail = userEmail.getText().toString().trim();
                String getPassword = userPassword.getText().toString().trim();
                createAccount(getEmail,getPassword);
            }
        });

    }
    //create user account
    private void createAccount(String email,String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {

                        if (task.isSuccessful())
                        {
                            Log.d("TESTING","SIGN UP SUCCESSFUL" + task.isSuccessful());
                            Toast.makeText(MainActivity.this,"SIGN UP SUCCESSFUL",Toast.LENGTH_SHORT).show();
                        } //end if statement
                        else
                        {
                            userInformation();
                            Toast.makeText(MainActivity.this,"CREATED ACCOUNT",Toast.LENGTH_SHORT).show();
                            Log.d("TESTING","CREATING ACCOUNT");

                        }//end else
                    }
                });
    }

    //Set user information
    private void userInformation()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null)
        {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(userName.getText().toString().trim())
                    .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Log.d("TESTING","USER PROFILE UPDATED");
                        }
                    }
                });

        }

    }

    //Start sign up in Progress
    private void signIn(String userEmail,String userPassword)
    {
        mAuth.signInWithEmailAndPassword(userEmail,userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        Log.d("TESTING","SIGN IN SUCCESSFUL:" + task.isSuccessful());

                        if(!task.isSuccessful())
                        {
                            Log.w("TESTING","signInWithEmail:failed",task.getException());
                            Toast.makeText(MainActivity.this,"failed",Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Intent i = new Intent(MainActivity.this,LogIn.class);
                            finish();
                            startActivity(i);
                        }
                    }
                });
    }

}

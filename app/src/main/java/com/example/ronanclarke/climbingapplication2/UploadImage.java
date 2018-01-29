package com.example.ronanclarke.climbingapplication2;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


/**
 * Created by francisclarke on 25/01/2018.
 */

public class UploadImage extends AppCompatActivity
{
    Button selectImage,uploadButton;
    ImageView userImage;
    TextView imageTitle;
    public static final int READ_EXTERNAL_STORAGE = 0;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgressDialog;
    private Firebase mRoofRef;
    private Uri mImageUri = null;
    private DatabaseReference mdatabaseRef;
    private StorageReference mStorage;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_layout);

        Firebase.setAndroidContext(this);

        selectImage = (Button)findViewById(R.id.selectImageBT);
        uploadButton = (Button)findViewById(R.id.uploadImageBT);
        userImage = (ImageView)findViewById(R.id.user_imageIV);
        imageTitle = (TextView)findViewById(R.id.imageNameET);

        //Initialise Progress bar
        mProgressDialog = new ProgressDialog(UploadImage.this);

        //Select image from external storage
        selectImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getApplicationContext(),"Call for Permission",Toast.LENGTH_SHORT).show();
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_EXTERNAL_STORAGE);
                    }
                }
                else
                {
                    openGallery();
                }
            }
        });

        mdatabaseRef = FirebaseDatabase.getInstance().getReference();
        mRoofRef = new Firebase("https://climbingapplication2.firebaseio.com/").child("User Details").push();
        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://climbingapplication2.appspot.com/");


        uploadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String mName = imageTitle.getText().toString().trim();

                if(mName.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Fill all fields",Toast.LENGTH_SHORT).show();
                    return;
                }
                Firebase childRef_name = mRoofRef.child("Image_Title");
                childRef_name.setValue(mName);
                Toast.makeText(getApplicationContext(),"Updated image",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Check for Runtime permissions for Storage access
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch(requestCode)
        {
            case READ_EXTERNAL_STORAGE:
                //if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                if(1==1)
                {
                    openGallery();
                    return;
                }
        }
        Toast.makeText(getApplicationContext(),"....",Toast.LENGTH_SHORT).show();
    }

    private void openGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_INTENT);
    }

    //After selecting image from gallary image will directly uploaded to Firebase Database
    //and image will show in Image View
    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            mImageUri = data.getData();
            userImage.setImageURI(mImageUri);
            StorageReference filePath = mStorage.child("User_Images").child(mImageUri.getLastPathSegment());

            mProgressDialog.setMessage("Uploading Image...");
            mProgressDialog.show();

            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    mRoofRef.child("Image_URL").setValue(downloadUri.toString());

                    Glide.with(getApplicationContext())
                            .load(downloadUri)
                            .crossFade()
                            .placeholder(R.mipmap.ic_launcher)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(userImage);
                    Toast.makeText(getApplicationContext(),"Updated." , Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            });
        }
    }
}
package com.example.nenad.projekat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.UUID;

public class register extends AppCompatActivity {

    MaterialEditText mEmail,mPassword,mName,mPhone;
    Button mImage,mFinish;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private StorageReference storageProfileImagesStorageRef;
    String type;
    String downloadUrl;
    private final  static int Gallery_Pick=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mEmail=(MaterialEditText) findViewById(R.id.edtEmail);
        mPassword=(MaterialEditText) findViewById(R.id.edtPassword);
        mName=(MaterialEditText) findViewById(R.id.edtName);
        mPhone=(MaterialEditText) findViewById(R.id.edtPhone);
        mImage = (Button) findViewById(R.id.button3);
        mFinish = (Button) findViewById(R.id.button);
        type = getIntent().getStringExtra("type");
        downloadUrl = "default_profile";
        storageProfileImagesStorageRef= FirebaseStorage.getInstance().getReference().child("Profile_images");
        mAuth = FirebaseAuth.getInstance();

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_Pick);
            }
        });

       mFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                final String name = mName.getText().toString();
                final String phone = mPhone.getText().toString();

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(register.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful())
                                {
                                    Toast.makeText(register.this,"error",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    if(type.equals("drivers")) {
                                        String userid = mAuth.getCurrentUser().getUid();
                                        DatabaseReference currentuserdb = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userid);
                                        Driver driver = new Driver();
                                        driver.setEmail(email);
                                        driver.setId(userid);
                                        driver.setNegativneOcene(0);
                                        driver.setPozitivneOcene(0);
                                        driver.setUser_image(downloadUrl);
                                        driver.setName(name);
                                        driver.setPhone(phone);
                                        currentuserdb.setValue(driver);
                                    }
                                    else
                                    {
                                        String userid = mAuth.getCurrentUser().getUid();
                                        DatabaseReference currentuserdb = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userid);
                                        Customer customer = new Customer();
                                        customer.setEmail(email);
                                        customer.setId(userid);
                                        customer.setUser_image(downloadUrl);
                                        customer.setName(name);
                                        customer.setPhone(phone);
                                        currentuserdb.setValue(customer);
                                    }

                                }
                            }
                        });

            }
        });
        firebaseAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                if(user != null)
                {
                    if(type.equals("drivers")) {
                        Intent intent = new Intent(register.this, DriverMapsActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    else
                    {
                        Intent intent = new Intent(register.this, CustomerMapsActivity.class);
                        startActivity(intent);
                        finish();
                        return;

                    }
                }
            }
        };
        mAuth.addAuthStateListener(firebaseAuthListener);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {

            Uri ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                Uri resultUri = result.getUri();

                //String user_id=mAuth.getCurrentUser().getUid();
                StorageReference filePath=storageProfileImagesStorageRef.child(UUID.randomUUID().toString()+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            //Toast.makeText(SettingsActivity.this, "Saving your profile image", Toast.LENGTH_LONG).show();

                            downloadUrl=task.getResult().getDownloadUrl().toString();

                        }
                        else
                        {
                            //Toast.makeText(SettingsActivity.this, "Error occured, while uploading your pic", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    @Override
    public  void  onStart()
    {
        super.onStart();


    }
    @Override
    public void onStop()
    {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

}

package com.example.nenad.projekat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateInformationActivity extends AppCompatActivity {

    private String type;
    private MaterialEditText edtEmail,edtName,edtPhone;
    private CircleImageView civ;
    private Button Save,Change;
    private DatabaseReference getUserDataReference;
    private FirebaseAuth mAuth;
    private StorageReference storageProfileImagesStorageRef;
    private final  static int Gallery_Pick=1;
    private TextView user_name;
    private TextView user_rating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);
        type = (String) getIntent().getStringExtra("type");
        if(type.equals("drivers"))
        {
            getUserDataReference= FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        else
        {
            getUserDataReference= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        mAuth = FirebaseAuth.getInstance();
        //getUserDataReference= FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
        storageProfileImagesStorageRef= FirebaseStorage.getInstance().getReference().child("Profile_images");
        edtName=(MaterialEditText) findViewById(R.id.edtName6);
        edtPhone=(MaterialEditText) findViewById(R.id.edtPhone6);
        civ = (CircleImageView) findViewById(R.id.settings_profile_image6);
        Change = (Button) findViewById(R.id.change6);
        Save = (Button) findViewById(R.id.save6);
        user_name=(TextView) findViewById(R.id.settings_user_name);
                user_rating=(TextView) findViewById(R.id.settings_user_profile_status);
        if(type.equals("drivers"))
        {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child("Drivers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Driver d = dataSnapshot.getValue(Driver.class);
                    edtName.setText(d.getName());
                    edtPhone.setText(d.getPhone());
                    user_name.setText(d.getName());
                    user_rating.setText("(+) "+d.getPozitivneOcene()+"/(-) "+d.getNegativneOcene());
                    if(!d.getUser_image().equals("default_profile"))
                    {
                        Picasso.with(UpdateInformationActivity.this).load(d.getUser_image()).placeholder(R.drawable.default_profile).into(civ);
                    }



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else
        {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child("Customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Customer d = dataSnapshot.getValue(Customer.class);
                    user_name.setText(d.getName());
                    user_rating.setText("");
                    edtName.setText(d.getName());
                    edtPhone.setText(d.getPhone());
                    if(!d.getUser_image().equals("default_profile"))
                    {
                        Picasso.with(UpdateInformationActivity.this).load(d.getUser_image()).placeholder(R.drawable.default_profile).into(civ);
                    }



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        Change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_Pick);
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals("drivers"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("name").setValue(edtName.getText().toString());
                    FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("phone").setValue(edtPhone.getText().toString());

                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Users").child("Customers")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("name").setValue(edtName.getText().toString());
                    FirebaseDatabase.getInstance().getReference().child("Users").child("Customers")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("phone").setValue(edtPhone.getText().toString());
                }
                finish();
            }
        });




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

                String user_id=mAuth.getCurrentUser().getUid();
                StorageReference filePath=storageProfileImagesStorageRef.child(user_id+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            Toast.makeText(UpdateInformationActivity.this, "Saving your profile image", Toast.LENGTH_LONG).show();

                            String downloadUrl=task.getResult().getDownloadUrl().toString();
                            getUserDataReference.child("user_image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(UpdateInformationActivity.this, "Profile image updated successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(UpdateInformationActivity.this, "Error occured, while uploading your pic", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

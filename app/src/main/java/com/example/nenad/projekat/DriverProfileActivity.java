package com.example.nenad.projekat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DriverProfileActivity extends AppCompatActivity {

    private Button SendFriendRequest;
    private Button DeclineFriendRequest;
    private TextView ProfileName;
    private TextView ProfileStatus;
    private ImageView ProfileImage;

    private DatabaseReference UsersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_profile);

        UsersReference= FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");

        final String visit_user_id=getIntent().getExtras().get("visit_user_id").toString();

        SendFriendRequest=(Button) findViewById(R.id.profile_visit_send_req_btn);
        DeclineFriendRequest=(Button) findViewById(R.id.profile_visit_decline_req_btn);
        ProfileName=(TextView) findViewById(R.id.profile_visit_user_name);
        ProfileStatus=(TextView) findViewById(R.id.profile_visit_user_status);
        ProfileImage=(ImageView) findViewById(R.id.profile_visit_user_image);



        DeclineFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(ProfileActivity.this,visit_user_id,Toast.LENGTH_LONG).show();
                UsersReference.child(visit_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name= dataSnapshot.child("name").getValue().toString();
                       // Toast.makeText(ProfileActivity.this,name,Toast.LENGTH_LONG).show();


                        Intent chatIntent=new Intent(DriverProfileActivity.this,ChatActivity.class);
                        chatIntent.putExtra("visit_user_id",visit_user_id);
                        chatIntent.putExtra("user_name", name);
                        chatIntent.putExtra("type","customers");
                        startActivity(chatIntent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        UsersReference.child(visit_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name= dataSnapshot.child("name").getValue().toString();
                String poz=dataSnapshot.child("pozitivneOcene").getValue().toString();
                String neg=dataSnapshot.child("negativneOcene").getValue().toString();

                //viewHolder.setUser_status("rating: (+) "+poz+"/ (-) "+neg);
               // String name= dataSnapshot.child("name").getValue().toString();
                String status= "rating: (+) "+poz+"/ (-) "+neg;
               // String status= dataSnapshot.child("email").getValue().toString();
                String image= dataSnapshot.child("user_image").getValue().toString();

                ProfileName.setText(name);
                ProfileStatus.setText(status);
                Picasso.with(DriverProfileActivity.this).load(image).placeholder(R.drawable.default_profile).into(ProfileImage);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

package com.example.nenad.projekat;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private Button SendFriendRequest;
    private Button DeclineFriendRequest;
    private TextView ProfileName;
    private TextView ProfileStatus;
    private ImageView ProfileImage;

    private DatabaseReference UsersReference;
    private TrenutnoZahtevanaVoznja zahtevanaVoznja;
    private String driver_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_profile);

        UsersReference= FirebaseDatabase.getInstance().getReference().child("Users");

       // final String visit_user_id=getIntent().getExtras().get("visit_user_id").toString();

        SendFriendRequest=(Button) findViewById(R.id.profile_visit_send_req_btn);
        DeclineFriendRequest=(Button) findViewById(R.id.profile_visit_decline_req_btn);
        ProfileName=(TextView) findViewById(R.id.profile_visit_user_name);
        ProfileStatus=(TextView) findViewById(R.id.profile_visit_user_status);
        zahtevanaVoznja=(TrenutnoZahtevanaVoznja)getIntent().getSerializableExtra("zahtevanaVoznja");
        driver_id = getIntent().getStringExtra("visit_user_id");

        SendFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference referencaposebna = FirebaseDatabase.getInstance().getReference().child("Users")
                            .child("Drivers").child(driver_id).child("Zahtev");
                    referencaposebna.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                                Toast.makeText(ProfileActivity.this,"Vozac ima zahtev",Toast.LENGTH_LONG).show();
                            else
                            {
                                dataSnapshot.getRef().setValue(zahtevanaVoznja);

                            }
                            referencaposebna.removeEventListener(this);
                            Intent intent = new Intent(ProfileActivity.this,CustomerMapsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            }
        });
        ProfileImage=(ImageView) findViewById(R.id.profile_visit_user_image);



        DeclineFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(ProfileActivity.this,visit_user_id,Toast.LENGTH_LONG).show();
               // FirebaseDatabase.getInstance().getReference()
                     //   .child("Users").child("Drivers").child(driver_id);
                UsersReference.child("Drivers").child(driver_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name= dataSnapshot.child("email").getValue().toString();
                         Toast.makeText(ProfileActivity.this,name,Toast.LENGTH_LONG).show();

                        Intent chatIntent=new Intent(ProfileActivity.this,ChatActivity.class);
                        chatIntent.putExtra("visit_user_id",driver_id);
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
//
        UsersReference.child("Drivers").child(driver_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String poz=dataSnapshot.child("pozitivneOcene").getValue().toString();
                String neg=dataSnapshot.child("negativneOcene").getValue().toString();

                //viewHolder.setUser_status("rating: (+) "+poz+"/ (-) "+neg);
                String name= dataSnapshot.child("name").getValue().toString();
                String status= "rating: (+) "+poz+"/ (-) "+neg;
                String image= dataSnapshot.child("user_image").getValue().toString();

                ProfileName.setText(name);
                ProfileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_profile).into(ProfileImage);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

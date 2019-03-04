package com.example.nenad.projekat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RejtingActivity extends AppCompatActivity {
    private  Driver driver;
    private Button mLike,mDislike;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocenjivanje);
        driver =(Driver) getIntent().getSerializableExtra("driver");
        mLike=(Button) findViewById(R.id.like);
        mDislike=(Button) findViewById(R.id.dislike);

        mLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driver.dodajPozitivnuOcenu();
                SnimiVozaca(driver);

                FirebaseDatabase.getInstance().getReference().child("Users").child("Customers")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Rejtuj").removeValue();
                finish();


            }
        });
        mDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driver.dodajNegativnuOcenu();
                SnimiVozaca(driver);
                FirebaseDatabase.getInstance().getReference().child("Users").child("Customers")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Rejtuj").removeValue();
                finish();
            }
        });

    }
    private void SnimiVozaca(Driver d)
    {
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Drivers").child(d.getId()).setValue(d);
    }
}

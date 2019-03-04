package com.example.nenad.projekat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Activity2 extends AppCompatActivity {

    Button btnSignIn,btnRegister;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        btnSignIn=(Button) findViewById(R.id.btnSignIn);
        btnRegister=(Button) findViewById(R.id.btnRegister);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignIn();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegister();
            }
        });
        type = getIntent().getStringExtra("type");



    }
    public void openSignIn()
    {
        if(type.equals("customers")) {
            Intent intent = new Intent(this, CustomerLoginActivity.class);
            startActivity(intent);
        }
        else
        {
            Intent intent= new Intent(this, DriverLoginActivity.class);
            startActivity(intent);
        }
    }
    public void openRegister()
    {
        Intent intent=new Intent(this,register.class);
        intent.putExtra("type",type);
        startActivity(intent);
    }





}

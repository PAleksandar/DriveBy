package com.example.nenad.projekat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverLoginActivity extends AppCompatActivity {
    private EditText txtEmail,txtPassword;
    private Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_customer_login);
        txtEmail = (EditText) findViewById(R.id.edtEmail);
        txtPassword=(EditText) findViewById(R.id.edtPassword);
        btnLogin=(Button) findViewById(R.id.zamain);
        //btnRegister=(Button) findViewById(R.id.register);
        mAuth=FirebaseAuth.getInstance();
        firebaseAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                if(user != null)
                {
                    Intent intent = new Intent(DriverLoginActivity.this, DriverMapsActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };
//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final String email = txtEmail.getText().toString();
//                String password = txtPassword.getText().toString();
//                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(DriverLoginActivity.this,
//                        new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if(!task.isSuccessful())
//                                {
//                                    Toast.makeText(DriverLoginActivity.this,"error",
//                                            Toast.LENGTH_SHORT).show();
//                                }
//                                else
//                                {
//                                    String userid=mAuth.getCurrentUser().getUid();
//                                    DatabaseReference currentuserdb= FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userid);
//                                    Driver driver = new Driver();
//                                    driver.setEmail(email);
//                                    driver.setId(userid);
//                                    driver.setNegativneOcene(0);
//                                    driver.setPozitivneOcene(0);
//                                    driver.setUser_image("default_profile");
//                                    currentuserdb.setValue(driver);
//
//                                }
//                            }
//                        });
//
//                }
//            });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();
                if(password.equals("")||email.equals(""))
                {
                    return;
                }
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(DriverLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(DriverLoginActivity.this,"error",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                        }

                    }
                });
            }
        });








    }
    @Override
    public  void  onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);

    }
    @Override
    public void onStop()
    {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

}

package com.example.nenad.projekat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btnVozac, btnKorisnik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        btnVozac = (Button) findViewById(R.id.vozac);
        btnKorisnik = (Button) findViewById(R.id.korisnik);

        btnVozac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Activity2.class);
                intent.putExtra("type","drivers");
                startActivity(intent);
                finish();
                return;
            }
        });
        btnKorisnik.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Activity2.class);
                intent.putExtra("type","customers");

                startActivity(intent);
                finish();
                return;

            }

        });
    }
}

package com.example.nenad.projekat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class ZakazivanjeVoznjeActivity extends AppCompatActivity {
    TextView pocetak;
    TextView kraj;
    Button zakazi,mSlika;
    String driverId;
    Driver driver;
    private int mYear, mMonth, mDay, mHour, mMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_zakazivanje_voznje);
        pocetak = (EditText) findViewById(R.id.pocetak);
        kraj = (EditText) findViewById(R.id.kraj);
        zakazi = (Button) findViewById(R.id.zakazi);
        driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        driver =(Driver) getIntent().getSerializableExtra("drivers");
        Toast.makeText(ZakazivanjeVoznjeActivity.this,driver.getEmail(),Toast.LENGTH_LONG).show();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {



                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();




        zakazi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ZakazivanjeVoznjeActivity.this,""+mYear,Toast.LENGTH_LONG).show();
                ZakazanaVoznja zakazanaVoznja = new ZakazanaVoznja(driver,pocetak.getText().toString(),kraj.getText().toString(),mYear,mMonth,mDay);
                FirebaseDatabase.getInstance().getReference().child("ZakazaneVoznje").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .push().setValue(zakazanaVoznja);
            }
        });

    }
    @Override
    public  void  onBackPressed()
    {
        finish();
    }
}

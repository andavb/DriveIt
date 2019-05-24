package com.example.andrejavbelj.driveit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import dmax.dialog.SpotsDialog;

public class Login extends AppCompatActivity {

    EditText uprIme, geslo;
    ImageView slikaj;
    Button prijava;
    android.app.AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        uprIme = (EditText)findViewById(R.id.ID_userName);
        geslo = (EditText)findViewById(R.id.ID_loginPassword);

        slikaj = (ImageView)findViewById(R.id.ID_imagePhoto);
        prijava = (Button)findViewById(R.id.ID_prijaviBtn);

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Logging in")
                .setCancelable(false)
                .build();

        setOnClickListeners();


    }

    private void setOnClickListeners(){
        slikaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent opencamera = new Intent(getBaseContext(), CameraSmile.class);
                startActivity(opencamera);
            }
        });

        prijava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.show();

                if(uprIme.getText().toString().equals("") && geslo.getText().toString().equals("")){

                    dialog.dismiss();
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(i);

                }
            }
        });
    }
}

package com.example.andrejavbelj.driveit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private EditText uprIme, geslo;
    private ImageView slikaj;
    private Button prijava, registracija;
    private ProgressBar bar;
    private SharedPreferences pref;
    private SharedPreferences.Editor prefEditor;
    private final String NAME = "pref";
    private final String USERNAME = "name";

    private static String URL="http://192.168.1.72:8888/driveit/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        uprIme = (EditText)findViewById(R.id.ID_userName);
        geslo = (EditText)findViewById(R.id.ID_loginPassword);

        slikaj = (ImageView)findViewById(R.id.ID_imagePhoto);
        prijava = (Button)findViewById(R.id.ID_spremenibtn);
        registracija = (Button)findViewById(R.id.ID_registracijaBtn);
        bar = (ProgressBar) findViewById(R.id.ID_loadinglogin);
        bar.setVisibility(View.GONE);
        pref = getSharedPreferences(NAME, Context.MODE_PRIVATE);
        prefEditor = pref.edit();

        setOnClickListeners();

    }

    @Override
    protected void onResume() {
        super.onResume();

        prijava.setVisibility(View.VISIBLE);
        registracija.setVisibility(View.VISIBLE);
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

                String n, p;
                n = uprIme.getText().toString();
                p = geslo.getText().toString();


                //Intent i = new Intent(getBaseContext(), GlavniMeni.class);
                //startActivity(i);

                if(n.matches("") || p.matches("")){
                    Toast.makeText(getBaseContext(), getResources().getText(R.string.vse_podatke), Toast.LENGTH_SHORT).show();
                }
                else{
                    login(n, p);
                }
            }
        });
        registracija.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent r = new Intent(getBaseContext(), Registracija.class);
                startActivity(r);
            }
        });
    }

    private void login(final String name, final String password){
        bar.setVisibility(View.VISIBLE);
        prijava.setVisibility(View.GONE);
        registracija.setVisibility(View.GONE);

        StringRequest request = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject json = new JSONObject(response);
                            String s = json.getString("success");


                            if (s.equals("1")){

                                bar.setVisibility(View.GONE);
                                prefEditor.putString(USERNAME, uprIme.getText().toString());
                                prefEditor.apply();
                                Intent i = new Intent(getBaseContext(), GlavniMeni.class);
                                startActivity(i);
                            }
                            else if(s.equals("2")){

                                bar.setVisibility(View.GONE);
                                prijava.setVisibility(View.VISIBLE);
                                registracija.setVisibility(View.VISIBLE);
                                Toast.makeText(getBaseContext(), getResources().getText(R.string.uporabnik_ne_obstaja), Toast.LENGTH_SHORT).show();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(getBaseContext(), getResources().getText(R.string.prijava_ni_uspela), Toast.LENGTH_SHORT).show();
                            bar.setVisibility(View.GONE);
                            prijava.setVisibility(View.VISIBLE);
                            registracija.setVisibility(View.VISIBLE);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), getResources().getText(R.string.prijava_ni_uspela), Toast.LENGTH_SHORT).show();
                        bar.setVisibility(View.GONE);
                        prijava.setVisibility(View.VISIBLE);
                        registracija.setVisibility(View.VISIBLE);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametri = new HashMap<>();

                parametri.put("usrname", name);
                parametri.put("password", password);
                return parametri;
            }
        };

        RequestQueue r = Volley.newRequestQueue(this);
        r.add(request);
    }
}

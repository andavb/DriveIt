package com.example.andrejavbelj.driveit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonIOException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Registracija extends AppCompatActivity {

    private EditText usrname, password, passwordRepeat, uniqcode;
    private Button registrirajBtn;
    private ProgressBar loading;
    private static String URL="http://192.168.43.116:8888/driveit/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registracija);

        loading = (ProgressBar)findViewById(R.id.ID_loading);

        loading.setVisibility(View.GONE);
        usrname = (EditText)findViewById(R.id.ID_name);
        password = (EditText)findViewById(R.id.ID_passwrod);
        passwordRepeat = (EditText)findViewById(R.id.ID_passwrodRepeat);
        uniqcode = (EditText)findViewById(R.id.ID_uniqcode);
        registrirajBtn = (Button) findViewById(R.id.ID_registerBtnReg);

        registrirajBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n, p, pr, code;
                n = usrname.getText().toString();
                p = password.getText().toString();
                pr = passwordRepeat.getText().toString();
                code = uniqcode.getText().toString();

                if(n.matches("") || p.matches("") || pr.matches("") || code.matches("")){
                    Toast.makeText(getBaseContext(), getResources().getText(R.string.vse_podatke), Toast.LENGTH_SHORT).show();
                }
                else{
                    if(p.matches(pr)){
                        Regist(n, p, code);
                    }
                    else{
                        Toast.makeText(getBaseContext(), getResources().getText(R.string.neujemanje_gesel), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void Regist(final String name, final String pass, final String uniqueCoe){

        loading.setVisibility(View.VISIBLE);
        registrirajBtn.setVisibility(View.GONE);

        StringRequest request = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject json = new JSONObject(response);
                            String s = json.getString("success");


                            if (s.equals("1")){

                                loading.setVisibility(View.GONE);
                                Toast.makeText(getBaseContext(), getResources().getText(R.string.registracija_uspela), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else if(s.equals("2")){

                                loading.setVisibility(View.GONE);
                                registrirajBtn.setVisibility(View.VISIBLE);
                                Toast.makeText(getBaseContext(), getResources().getText(R.string.koda_se_ne_ujemaa), Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(getBaseContext(), getResources().getText(R.string.registracija_niUspela), Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            registrirajBtn.setVisibility(View.VISIBLE);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), getResources().getText(R.string.registracija_niUspela), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        registrirajBtn.setVisibility(View.VISIBLE);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametri = new HashMap<>();

                parametri.put("usrname", name);
                parametri.put("password", pass);
                parametri.put("code", uniqueCoe);
                return parametri;
            }
        };

        RequestQueue r = Volley.newRequestQueue(this);
        r.add(request);
    }

}

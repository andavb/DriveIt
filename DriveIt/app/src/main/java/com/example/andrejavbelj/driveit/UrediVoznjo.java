package com.example.andrejavbelj.driveit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.andrejavbelj.driveit.VoznjeClass.Voznje;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class UrediVoznjo extends AppCompatActivity {


    private final String URL_ID = "http://192.168.1.72:3000/api/statistika/";
    private EditText ime, kolicina, cena;
    private TextView id_uredi;
    private Button spremeni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uredi_voznjo);

        id_uredi = findViewById(R.id.ID_id);
        ime = findViewById(R.id.ID_imeUredi);
        kolicina = findViewById(R.id.ID_kolicinaUredi);
        cena = findViewById(R.id.ID_cenaUredi);
        spremeni = findViewById(R.id.ID_spremenibtn);

        spremeni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int value = extras.getInt("ID_voznje");
            System.out.println(value);
            getVoznja(value);
        }
        else{
            Toast.makeText(getBaseContext(), getResources().getText(R.string.napaka), Toast.LENGTH_SHORT).show();
        }
    }


    private void getVoznja(int id){
        RequestQueue request = Volley.newRequestQueue(getBaseContext());

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL_ID+id, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        Voznje v = gson.fromJson(response.toString(), Voznje.class);

                        System.out.println(v.getIme());


                        id_uredi.setText(Integer.toString(v.getId()));
                        ime.setText(v.getIme());
                        kolicina.setText(Integer.toString(v.getKolicina()));
                        cena.setText(Integer.toString(v.getCena()));
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error.Response" + error.toString());
                    }
                }
        );
        request.add(getRequest);
    }
}

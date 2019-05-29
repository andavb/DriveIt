package com.example.andrejavbelj.driveit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.andrejavbelj.driveit.VoznjeClass.Voznje;
import com.example.andrejavbelj.driveit.VoznjeClass.VoznjeList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class voznje extends AppCompatActivity {

    private final String URL = "http://192.168.1.72:3000/api/statistika";
    private final String URL_ID = "http://192.168.1.72:3000/api/statistika/";
    public ArrayList<Voznje> voznje;
    private RecyclerView myRecycleView;
    private VoznjeList mAdapter;
    private RecyclerView.LayoutManager mLayoutManeger;

    private EditText ime,kolicina, cena;
    private Button dodaj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voznje);

        ime = findViewById(R.id.ID_ime);
        kolicina = findViewById(R.id.ID_kolicina);
        cena = findViewById(R.id.ID_ccena);
        dodaj = findViewById(R.id.dodaj);

        voznje = new ArrayList<>();

        getVoznje();

        dodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ime1, kol1, cena1;
                ime1 = ime.getText().toString();
                kol1 = kolicina.getText().toString();
                cena1 = cena.getText().toString();



                if(ime1.matches("") || kol1.matches("") || cena1.matches("")){
                    Toast.makeText(getBaseContext(), getResources().getText(R.string.vse_podatke), Toast.LENGTH_SHORT).show();
                }
                else{
                    int k =  Integer.valueOf(kol1);
                    int c =  Integer.valueOf(cena1);

                    dodajNaseznam(ime1, k, c);
                }
            }
        });

    }

    private void getVoznje(){
        RequestQueue request = Volley.newRequestQueue(getBaseContext());

        JsonArrayRequest objectRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {

                        Gson gson = new Gson();

                        Type listType = new TypeToken<ArrayList<Voznje>>(){}.getType();

                        voznje = gson.fromJson(response.toString(), listType);

                        buildRecyView();

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
        request.add(objectRequest);
    }

    private void getVoznja(int id){
        RequestQueue request = Volley.newRequestQueue(getBaseContext());

        JsonArrayRequest objectRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_ID+id,
                null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {

                        Gson gson = new Gson();

                        Type listType = new TypeToken<ArrayList<Voznje>>(){}.getType();

                        voznje = gson.fromJson(response.toString(), listType);

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
        request.add(objectRequest);
    }

    private void deleteVoznja(int id){
        RequestQueue request = Volley.newRequestQueue(getBaseContext());

        JsonArrayRequest objectRequest = new JsonArrayRequest(
                Request.Method.DELETE,
                URL_ID+id,
                null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println("Deleted " + response);

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
        request.add(objectRequest);
    }

    private void postVoznja(Voznje voznja){

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ime", voznja.getIme());
        params.put("kolicina",Integer.toString(voznja.getKolicina()));
        params.put("cena", Integer.toString(voznja.getCena()));

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                new JSONObject(params),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Deleted " + response);

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
        RequestQueue request = Volley.newRequestQueue(getBaseContext());

        request.add(objectRequest);


    }


    public void dodajNaseznam(String ime, int kol, int cena){
        if(isNetworkAvailable()){
            postVoznja(new Voznje(ime, kol, cena));
            voznje.add(new Voznje(ime, kol, cena));
            mAdapter.notifyDataSetChanged();
        }
        else{
            Toast.makeText(getBaseContext(), getResources().getText(R.string.ni_povezave), Toast.LENGTH_SHORT).show();
        }
    }

    public void odstraniItem(int pos){
        if(isNetworkAvailable()){
            deleteVoznja(voznje.get(pos).getId());

            voznje.remove(pos);
            mAdapter.notifyDataSetChanged();
        }
        else{
            Toast.makeText(getBaseContext(), getResources().getText(R.string.ni_povezave), Toast.LENGTH_SHORT).show();
        }
    }

    public void odpriMeni(int position){
        String ime = voznje.get(position).getIme();
        int st = voznje.get(position).getKolicina();
        int selekcija = voznje.get(position).getCena();


        Toast.makeText(getApplicationContext(), ime + " " + st + " " + selekcija, Toast.LENGTH_SHORT).show();
    }

    public void buildRecyView(){

        myRecycleView = findViewById(R.id.recyclerView);
        myRecycleView.setHasFixedSize(true);
        mLayoutManeger = new LinearLayoutManager(this);
        mAdapter = new VoznjeList(voznje);

        myRecycleView.setLayoutManager(mLayoutManeger);
        myRecycleView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new VoznjeList.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                odpriMeni(position);
            }

            @Override
            public void onDelete(int position) {
                odstraniItem(position);
            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

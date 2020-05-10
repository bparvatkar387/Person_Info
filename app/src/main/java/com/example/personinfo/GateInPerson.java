package com.example.personinfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GateInPerson extends AppCompatActivity implements PersonRecyler.OnPersonListener{
    ArrayList<PersonAdapter> personAdapter;
    PersonRecyler personRecyler;
    String getData = "http://codehunt.co/android/harsh/get.php";
    ProgressDialog progressDialog;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_in_person);

        personAdapter = new ArrayList<>();

        progressDialog = new ProgressDialog(GateInPerson.this);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            if(response.equals("0")) {
                                Toast.makeText(GateInPerson.this, "No Person is outside.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(GateInPerson.this, MainActivity.class));
                                finish();
                            }

                            JSONArray jsonArray = new JSONArray(response);
                            System.out.println("json" + jsonArray.toString());

                            personAdapter.clear();
                            for(int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String sid = object.getString("id");
                                int id = Integer.valueOf(sid);
                                String obFname = object.getString("fname");
                                String obLname = object.getString("lname");
                                personAdapter.add(new PersonAdapter(id, obFname, obLname));

                            }

                            RecyclerView recyclerView = findViewById(R.id.showOutPerson);
                            personRecyler = new PersonRecyler(GateInPerson.this, personAdapter, GateInPerson.this);
                            recyclerView.setAdapter(personRecyler);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(GateInPerson.this, "Error Occurred", Toast.LENGTH_SHORT).show();

                    }
                });

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(GateInPerson.this);

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);


    }

    @Override
    public void OnPersonClickListener(int position) {
        int i = personRecyler.getClickedPersonID(position);
        Toast.makeText(this, String.valueOf(i), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, GateOutPerson.class);
        intent.putExtra("id", i);
        startActivity(intent);

    }
}

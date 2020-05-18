package com.example.personinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GateOutPerson extends AppCompatActivity {
    Button gateOutSubmit;
    EditText fNameET, lNameET, ageET, gateInHrET, gateInMinET, gateOutHrET, gateOutMinET, phoneET, reasonET, empNoET;
    EditText gateOutTempET, gateInTempET;
    String fName, lName, age, gateInHr, gateInMin, gateOutHr, gateOutMin, phone, reason, isEmp, empID, gender;
    String gateOutDT, gateInDT, gateOutTemp, gateInTemp;
    Spinner genderSp;
    RadioGroup rgEmp;
    RadioButton rbEmp;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    String insertDataURL = "http://codehunt.co/android/harsh/insert.php";
    String updateDataURL = "http://codehunt.co/android/harsh/update.php";
    String getOnePersonURL = "http://codehunt.co/android/harsh/oneperson.php";
    ProgressDialog progressDialog;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_out_person);

        gateOutSubmit = findViewById(R.id.gateOutSubmit);

        fNameET = findViewById(R.id.fName);
        lNameET = findViewById(R.id.lName);
        ageET = findViewById(R.id.age);
        gateInHrET = findViewById(R.id.gateInHr);
        gateInMinET = findViewById(R.id.gateInMin);
        gateOutHrET = findViewById(R.id.gateOutHr);
        gateOutMinET = findViewById(R.id.gateOutMin);
        phoneET = findViewById(R.id.phone);
        reasonET = findViewById(R.id.reason);
        empNoET = findViewById(R.id.empNo);
        empNoET.setVisibility(View.GONE);
        gateOutTempET = findViewById(R.id.gateOutTemp);
        gateInTempET = findViewById(R.id.gateInTemp);

        genderSp = findViewById(R.id.gender);

        rgEmp = findViewById(R.id.empGroup);
        rbEmp = findViewById(R.id.rbEmp);
        rbEmp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    empNoET.setVisibility(View.VISIBLE);
                } else {
                    empNoET.setVisibility(View.GONE);
                }
            }
        });

        progressDialog = new ProgressDialog(GateOutPerson.this);


        Intent intent = getIntent();
        final int userid = intent.getIntExtra("id", -1);
        if(userid != -1) {
            fillDetails(userid);
        }

        gateOutSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(verifyDetails()) {
                    storeDetails();
                    fixDetails();

                    if(userid == -1) {
                        insertData(insertDataURL, false, 0);
                    } else {
                        insertData(updateDataURL, true, userid);
                    }

                    };
            }
        });
    }

    private void insertData(String url, final boolean update, final int id) {
        progressDialog.show();

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(GateOutPerson.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();

                        if(response.equals("1")) {
                            System.out.println("json: " + response);
                            Toast.makeText(GateOutPerson.this, "Detail Entered Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(GateOutPerson.this, MainActivity.class));
                        } else {
                            Toast.makeText(GateOutPerson.this, "Failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();

                        // Showing error message if something goes wrong.
                        Toast.makeText(GateOutPerson.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                if(update) {
                    params.put("id", String.valueOf(id));
                }
                params.put("fName", fName);
                params.put("lName", lName);
                params.put("age", age);
                params.put("gender", gender);
                params.put("gateOutDT", gateOutDT);
                params.put("gateInDT", gateInDT);
                params.put("gateOutTemp", gateOutTemp);
                params.put("gateInTemp", gateInTemp);
                params.put("phone", phone);
                params.put("reason", reason);
                params.put("emp", isEmp);
                params.put("empID", empID);


                return params;
            }
        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(GateOutPerson.this);

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

        System.out.println(fName + "\t" + lName + "\t" + age + "\t" + gender + "\t" + gateOutDT + "\t" + gateInDT + "\t" + phone + "\t" + isEmp + "\t" + empID + "\t" + reason + "\t" );

    }

    private void fillDetails(final int userid) {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getOnePersonURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            System.out.println(response);
                            JSONArray jsonArray = new JSONArray(response);
                            System.out.println("json" + jsonArray.toString());

                            JSONObject object = jsonArray.getJSONObject(0);
                            String test = object.getString("fname");
                            System.out.println(test);
                            fNameET.setText(object.getString("fname"));
                            lNameET.setText(object.getString("lname"));
                            ageET.setText(object.getString("age"));
                            phoneET.setText(object.getString("phone"));
                            reasonET.setText(object.getString("reason"));
                            gateOutTempET.setText(object.getString("gate_out_temp"));

                            System.out.println("temp: " + object.getString("gate_in_temp"));
                            if(object.getString("gate_in_temp").equals("0")) {
                                gateInTempET.setText("");
                            } else {
                                gateInTempET.setText(object.getString("gate_in_temp"));
                            }


                            //setting gate out time
                            String obGateOutTime = object.getString("gate_out_time");
                            Date obDateOut = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(obGateOutTime);
                            Calendar calendarOut = Calendar.getInstance();
                            calendarOut.setTime(obDateOut);

                            gateOutHrET.setText(String.valueOf(calendarOut.get(Calendar.HOUR_OF_DAY)));
                            gateOutMinET.setText(String.valueOf(calendarOut.get(Calendar.MINUTE)));

                            //setting gate in time
                            String obGateInTime = object.getString("gate_in_time");
                            Date obDateIn = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(obGateInTime);
                            Calendar calendarIn = Calendar.getInstance();
                            calendarIn.setTime(obDateIn);

                            if(calendarIn.get(Calendar.HOUR_OF_DAY) == 0) {
                                gateInHrET.setText("");
                            } else {
                                gateInHrET.setText(String.valueOf(calendarIn.get(Calendar.HOUR_OF_DAY)));
                            }
                            if(calendarIn.get(Calendar.MINUTE) == 0) {
                                gateInHrET.setText("");
                            } else {
                                gateInMinET.setText(String.valueOf(calendarIn.get(Calendar.MINUTE)));;
                            }



                            if(object.getString("emp").equals("y")) {
                                rbEmp.setChecked(true);
                                empNoET.setText(object.getString("emp_id"));
                            }


                            String obGender = object.getString("gender");
                            switch (obGender){
                                case "m":
                                    genderSp.setSelection(1);
                                    break;
                                case "f":
                                    genderSp.setSelection(2);
                                    break;
                                case "o":
                                    genderSp.setSelection(3);
                                    break;
                                default:
                                    genderSp.setSelection(0);
                            }



                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(GateOutPerson.this, "Error while fetchiing", Toast.LENGTH_SHORT).show();
            }
        }) {
        @Override
        protected Map<String, String> getParams() {

            // Creating Map String Params.
            Map<String, String> params = new HashMap<String, String>();

            // Adding All values to Params.
            params.put("id", String.valueOf(userid));

            return params;
        }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(GateOutPerson.this);

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);


    }

    private void fixDetails() {

        switch (gender){
            case "Male":
                gender = "m";
                break;
            case "Female":
                gender = "f";
                break;
            case "Other":
                gender = "o";
                break;
            default:
                gender = "x";
        }

        // if gate out data is filled and gate in data is not filled
        // it means its gate out time
        String customDate = "2000-01-01 00:00:00";
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if((!gateOutHr.equals("") && !gateOutMin.equals("")) && (gateInHr.equals("") && gateInMin.equals(""))) {

            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(gateOutHr));
            calendar.set(Calendar.MINUTE, Integer.parseInt(gateOutMin));
            gateOutDT = dateFormat.format(calendar.getTime());


            try {
                Date dateCustom = dateFormat.parse(customDate);
                if (dateCustom != null) {
                    calendar.setTime(dateCustom);
                    gateInDT = dateFormat.format(dateCustom);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        //if gate out is empty and gate in is filled
        //is means it gate in time
        if ((gateOutHr.equals("") && gateOutMin.equals("")) && (!gateInHr.equals("") && !gateInMin.equals(""))) {
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(gateInHr));
            calendar.set(Calendar.MINUTE, Integer.parseInt(gateInMin));

            gateInDT = dateFormat.format(calendar.getTime());

            try {
                Date dateCustom = dateFormat.parse(customDate);
                if (dateCustom != null) {
                    calendar.setTime(dateCustom);
                    gateOutDT = dateFormat.format(dateCustom);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        // if both gate out and gate in is filled
        if((!gateOutHr.equals("") && !gateOutMin.equals("")) && (!gateInHr.equals("") && !gateInMin.equals(""))) {

            calendar = Calendar.getInstance();

            //setting gate out time
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(gateOutHr));
            calendar.set(Calendar.MINUTE, Integer.parseInt(gateOutMin));
            gateOutDT = dateFormat.format(calendar.getTime());

            //setting gate in time
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(gateInHr));
            calendar.set(Calendar.MINUTE, Integer.parseInt(gateInMin));
            gateInDT = dateFormat.format(calendar.getTime());
        }

        System.out.println("gateout: " + gateOutDT);
        System.out.println("register: " + gateInDT);
    }

    private void storeDetails() {
        fName = fNameET.getText().toString().trim();
        lName = lNameET.getText().toString().trim();
        gender = genderSp.getSelectedItem().toString().trim();
        age = ageET.getText().toString().trim();
        gateOutHr = gateOutHrET.getText().toString().trim();
        gateOutMin = gateOutMinET.getText().toString().trim();
        gateInHr = gateInHrET.getText().toString().trim();
        gateInMin = gateInMinET.getText().toString().trim();
        gateOutTemp = gateOutTempET.getText().toString().trim();
        gateInTemp = gateInTempET.getText().toString().trim();
        phone = phoneET.getText().toString().trim();
        reason = reasonET.getText().toString().trim();


        if(rbEmp.isChecked()) {
            isEmp = "y";
            empID = empNoET.getText().toString().trim();
        } else {
            isEmp = "n";
            empID = "NA";
        }
    }

    private boolean verifyDetails() {

        if(fNameET.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Enter First Name", Toast.LENGTH_SHORT).show();
            //fixDetails();
            return false;
        }
        if(lNameET.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Enter Last Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(ageET.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Enter Age", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(genderSp.getSelectedItemPosition() == 0){
            Toast.makeText(getApplicationContext(), "Please Choose Gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        if((gateOutHrET.getText().toString().equals("") && gateOutMinET.getText().toString().equals("")) && (gateInHrET.getText().toString().equals("") && gateInMinET.getText().toString().equals(""))) {
            Toast.makeText(getApplicationContext(), "Enter either Gate Out Time or Gate In Time", Toast.LENGTH_SHORT).show();
            return false;
        }
        if((!gateOutHrET.getText().toString().equals("") && gateOutMinET.getText().toString().equals("")) || (gateOutHrET.getText().toString().equals("") && !gateOutMinET.getText().toString().equals(""))){
            Toast.makeText(getApplicationContext(), "Fill both hours and min in Gate Out Time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if((!gateInHrET.getText().toString().equals("") && gateInMinET.getText().toString().equals("")) || (gateInHrET.getText().toString().equals("") && !gateInMinET.getText().toString().equals(""))){
            Toast.makeText(getApplicationContext(), "Fill both hours and min in Gate In Time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!gateOutHrET.getText().toString().equals("") && gateOutTempET.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Enter Gate Out Temperature", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!gateInHrET.getText().toString().equals("") && gateInTempET.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Enter Gate In Temperature", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(phoneET.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Enter Phone Number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(reasonET.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Enter Reason", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(rgEmp.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getApplicationContext(), "Select Person Type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(rbEmp.isChecked()) {
            if(empNoET.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "Enter Employee Number", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }
}

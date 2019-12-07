package com.example.test;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.test.ds.User;
import com.example.test.helpers.TinkloKontrolleris;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Intent dabar = this.getIntent();
        User prisijunges = (User)dabar.getSerializableExtra("user");
        Toast.makeText(ListActivity.this, "Dabar prisijunges "+prisijunges.getLogin(), Toast.LENGTH_LONG).show();
        GetUserList prisijungti = new GetUserList();
        prisijungti.execute();
    }

    private final class GetUserList extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            /*
            String url ="http://192.168.6.52:8080/lab5/full.htm";
            try {
                return TinkloKontrolleris.sendGet(url);
            } catch (Exception e) {
                e.printStackTrace();
                return "Nepavyko gauti duomenu is web";
            }*/
            return "I do not know what it is";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("GAUTA: " + result);
            if (result != null) {
                Gson parseris = new Gson();
                try {
                    Type listType = new TypeToken<ArrayList<User>>(){}.getType();
                    List<User> vartotojai = new Gson().fromJson(result, listType);
                    ListView sar = findViewById(R.id.sarasas);
                    ArrayAdapter<User> arrayAdapter = new ArrayAdapter<User>
                            (ListActivity.this, android.R.layout.simple_list_item_1, vartotojai);
                    sar.setAdapter(arrayAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                   // Toast.makeText(ListActivity.this, "Neteisingi duomenys", Toast.LENGTH_LONG).show();
                }
            } else {
                //Toast.makeText(ListActivity.this, "Neteisingi duomenys", Toast.LENGTH_LONG).show();
            }
        }
    }
}
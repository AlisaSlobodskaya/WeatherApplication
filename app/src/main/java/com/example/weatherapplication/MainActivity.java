package com.example.weatherapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText userField;
    private Button mainBtn;
    private TextView resultInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userField = findViewById(R.id.user_field);
        mainBtn = findViewById(R.id.main_btn);
        resultInfo = findViewById(R.id.result_info);

        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userField.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this,
                            R.string.no_user_input, Toast.LENGTH_LONG).show();
                } else {
                    String city = userField.getText().toString();
                    String key = "5e425908d9989e7fd44f508e7e8867d2";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q="
                            + city + "&appid=" + key + "&units=metric&lang=ru";

                    new GetURLData().execute(url);
                }
            }
        });
    }

    private class GetURLData extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            resultInfo.setText("Ожидайте...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                return getURLInfo(reader, connection);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeResources(connection, reader);
            }
            return "Ошибка";
        }

        private String getURLInfo(BufferedReader reader, HttpURLConnection connection) {
            StringBuffer stringBuffer = null;
            try {
                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                stringBuffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine()) != null) {
                    stringBuffer.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (stringBuffer != null) {
                return stringBuffer.toString();
            } else {
                return "Ошибка";
            }
        }

        private void closeResources(HttpURLConnection connection, BufferedReader reader) {
            if (connection != null) {
                connection.disconnect();
            }

            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            resultInfo.setText(parseJson(result));
        }

        private String parseJson(String result) {
            String message = null;
            try {
                JSONObject jsonObject = new JSONObject(result);
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                double temperature = jsonObject.getJSONObject("main").getDouble("temp");
                double feelsLike = jsonObject.getJSONObject("main").getDouble("feels_like");
                message = "Погода: " + description
                        + "\nТемпература: " + temperature
                        + "\nОщущается как: " + feelsLike;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (message != null) {
                return message;
            } else {
                return "Ошибка";
            }
        }
    }
}
package com.example.evgeny.htc_test;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class ParseTask extends AsyncTask<Void, Void, String> {

    private Activity mActivity;
    private ArrayList<String> employeesInfoArray = null;

    private String resultJson = "";

    public ParseTask(Activity activity) {
        mActivity = activity;
        employeesInfoArray = new ArrayList<>();
    }

    @Override
    protected String doInBackground(Void... params) {

        HttpURLConnection urlConnection;
        BufferedReader reader;

        try {
            URL url = new URL("http://www.mocky.io/v2/56fa31e0110000f920a72134");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            resultJson = buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJson;
    }

    @Override
    protected void onPostExecute(String strJson) {
        super.onPostExecute(strJson);

        JSONObject dataJsonObj;

        try {
            dataJsonObj = new JSONObject(strJson);
            JSONObject company = dataJsonObj.getJSONObject("company");
            JSONArray employees = company.getJSONArray("employees");

            for (int i = 0; i < employees.length(); i++) {
                JSONObject firstEmp = employees.getJSONObject(i);
                String name = firstEmp.getString("name");
                String phone = firstEmp.getString("phone_number");

                JSONArray skillsArray = firstEmp.getJSONArray("skills");
                String skills = skillsArray.getString(0);
                for (int j = 1; j < skillsArray.length(); j++) {
                    skills += ", " + skillsArray.getString(j);
                }

                String employeeInfo = String.format("Имя: %s; № тел: %s; Навыки: %s.", name,
                        phone, skills);
                employeesInfoArray.add(employeeInfo);

                Collections.sort(employeesInfoArray);
                String[] stockArr = employeesInfoArray.toArray(new String[employeesInfoArray.size()]);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,
                        android.R.layout.simple_list_item_1, stockArr);
                MainActivity.listView.setAdapter(adapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
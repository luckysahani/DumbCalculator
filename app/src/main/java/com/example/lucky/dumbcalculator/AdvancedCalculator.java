package com.example.lucky.dumbcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AdvancedCalculator extends AppCompatActivity implements View.OnClickListener{

    private Button normalCalculator;
    private EditText number1;
    private Button sin;
    private Button cos;
    private Button tan;
    private Button exp;
    private Button log;
    private Button sqrt;
    private Button clear;
    private Button advancedCalculator;

    private TextView resultBox;
    private String resultText;

    private String serverURL = "http://52.26.129.180:8080/";
    private String sinURL = serverURL+"sin";
    private String cosURL = serverURL+"cos";
    private String expURL = serverURL+"exp";
    private String tanURL = serverURL+"tan";
    private String logURL = serverURL+"log";
    private String sqrtURL = serverURL+"sqrt";
    private String charset = "UTF-8";

    private String num1;

    private String finalResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_calculator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        number1 = (EditText) findViewById(R.id.number1);

        sin = (Button) findViewById(R.id.sin);
        cos = (Button) findViewById(R.id.cos);
        tan = (Button) findViewById(R.id.tan);
        exp = (Button) findViewById(R.id.exp);
        log = (Button) findViewById(R.id.log);
        sqrt = (Button) findViewById(R.id.sqrt);
        clear = (Button) findViewById(R.id.clear);
        resultBox = (TextView)findViewById(R.id.resultBox);
        sin.setOnClickListener(this);
        cos.setOnClickListener(this);
        tan.setOnClickListener(this);
        exp.setOnClickListener(this);
        log.setOnClickListener(this);
        sqrt.setOnClickListener(this);
        clear.setOnClickListener(this);

        normalCalculator = (Button)findViewById(R.id.normalcalculator);
        normalCalculator.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        num1 = number1.getText().toString();
        if(v==normalCalculator){
            this.finish();
        }
        else if(v==sin){
            getResultFromServer(num1, "sin",sinURL);
        }
        else if(v==cos){
            getResultFromServer(num1, "cos",cosURL);
        }
        else if(v==tan){
            getResultFromServer(num1, "tan",tanURL);
        }
        else if(v==log){
            getResultFromServer(num1, "log",logURL);
        }
        else if(v==exp){
            getResultFromServer(num1, "exp", expURL);
        }
        else if(v==sqrt){
            getResultFromServer(num1, "sqrt", sqrtURL);
        }
        else if(v==clear){
            number1.setText("");
            resultBox.setText("Result will be displayed here");
        }
    }

    private void getResultFromServer(String num1, final String type, final String finalURL){
        try {
            final String query = String.format("num1=%s",
                    URLEncoder.encode(num1, charset));

            Thread thread = new Thread(){
                @Override
                public void run() {

                    final String finalResultTemp =  excutePost(finalURL, query);
                    try {
                        JSONObject jsonObject = new JSONObject(finalResultTemp);
                        if(jsonObject.get("status").equals("true")){
                            finalResult = jsonObject.get(type).toString();
                            AdvancedCalculator.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    resultText = finalResult;
//                                    resultText = "The "+type+" of " + number1.getText().toString()+ " and " + number2.getText().toString()+ " is " + finalResult;
                                    resultBox.setText(resultText);
                                }
                            });
                        }
                        else{
                            finalResult = "Trying to hack !!! Huh :?";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            thread.start();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    private String excutePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
            String line;
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }
}

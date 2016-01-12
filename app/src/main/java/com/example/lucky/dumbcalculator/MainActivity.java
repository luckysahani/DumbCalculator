package com.example.lucky.dumbcalculator;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText number1;
    private EditText number2;
    private Button add;
    private Button subtract;
    private Button multiply;
    private Button divide;
    private Button clear;
    private Button advancedCalculator;

    private TextView resultBox;
    private String resultText;

    private String serverURL = "http://52.26.129.180:8080/";
    private String addURL = serverURL+"add";
    private String multiplyURL = serverURL+"multiply";
    private String subtractURL = serverURL+"subtract";
    private String divideURL = serverURL+"divide";
    private String charset = "UTF-8";

    private String num1;
    private String num2;

    private String finalResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        number1 = (EditText) findViewById(R.id.number1);
        number2 = (EditText) findViewById(R.id.number2);
        add = (Button) findViewById(R.id.add);
        subtract = (Button) findViewById(R.id.subtract);
        multiply = (Button) findViewById(R.id.multiply);
        divide = (Button) findViewById(R.id.divide);
        clear = (Button) findViewById(R.id.clear);
        advancedCalculator = (Button)findViewById(R.id.advancedcalculator);
        advancedCalculator.setOnClickListener(this);
        resultBox = (TextView)findViewById(R.id.resultBox);
        add.setOnClickListener(this);
        subtract.setOnClickListener(this);
        multiply.setOnClickListener(this);
        divide.setOnClickListener(this);
        clear.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        num1 = number1.getText().toString();
        num2 = number2.getText().toString();

        if (v == clear) {
            number1.setText("");
            number2.setText("");
            resultBox.setText("Result will be displayed here");
        } else if (v == advancedCalculator) {
            Intent intent = new Intent(MainActivity.this, AdvancedCalculator.class);
            startActivity(intent);
        }
        else{
            if( num1.length() == 0 ) {
                number1.requestFocus();
                number1.setError("First number is required!");
            }
            else if( num2.length() == 0 ) {
                number2.requestFocus();
                number2.setError("Second number is required!");
            }
            else {
                if (v == add) {
                    getResultFromServer(num1, num2, "add", addURL);
                } else if (v == multiply) {
                    getResultFromServer(num1, num2, "multiply", multiplyURL);
                } else if (v == subtract) {
                    getResultFromServer(num1, num2, "subtract", subtractURL);
                } else if (v == divide) {
                    getResultFromServer(num1, num2, "divide", divideURL);
                }
            }
        }
    }


    private void getResultFromServer(String num1, String num2, final String type, final String finalURL){
        try {
            final String query = String.format("num1=%s&num2=%s",
                    URLEncoder.encode(num1, charset),
                    URLEncoder.encode(num2, charset));

            Thread thread = new Thread(){
                @Override
                public void run() {

                    final String finalResultTemp =  excutePost(finalURL, query);
                    try {
                        JSONObject jsonObject = new JSONObject(finalResultTemp);
                        if(jsonObject.get("status").equals("true")){
                            finalResult = jsonObject.get(type).toString();
                            MainActivity.this.runOnUiThread(new Runnable() {
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

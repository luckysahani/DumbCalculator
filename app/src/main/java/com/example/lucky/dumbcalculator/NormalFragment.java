package com.example.lucky.dumbcalculator;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NormalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NormalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NormalFragment extends Fragment implements View.OnClickListener{

    private EditText number1;
    private EditText number2;
    private ImageView add;
    private ImageView subtract;
    private ImageView multiply;
    private ImageView divide;
    private Button clear;
    private Button advancedCalculator;

    private TextView resultBox;
    private String resultText;
    private String num1;
    private String num2;

    private String serverURL = "http://52.26.129.180:8080/";
    private String addURL = serverURL+"add";
    private String multiplyURL = serverURL+"multiply";
    private String subtractURL = serverURL+"subtract";
    private String divideURL = serverURL+"divide";
    private String charset = "UTF-8";

    private String finalResult;
    private Dialog progressDialog;

    public NormalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NormalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NormalFragment newInstance() {
        NormalFragment fragment = new NormalFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_normal, container, false);

        number1 = (EditText) view.findViewById(R.id.number1);
        number2 = (EditText) view.findViewById(R.id.number2);
        add = (ImageView) view.findViewById(R.id.add);
        subtract = (ImageView) view.findViewById(R.id.subtract);
        multiply = (ImageView) view.findViewById(R.id.multiply);
        divide = (ImageView) view.findViewById(R.id.divide);
        clear = (Button) view.findViewById(R.id.clear);
        resultBox = (TextView)view.findViewById(R.id.resultBox);
        add.setOnClickListener(this);
        subtract.setOnClickListener(this);
        multiply.setOnClickListener(this);
        divide.setOnClickListener(this);
        clear.setOnClickListener(this);

        return view;
    }




    @Override
    public void onClick(View v) {
        num1 = number1.getText().toString();
        num2 = number2.getText().toString();

        if (v == clear) {
            number1.setText("");
            number2.setText("");
            resultBox.setText("Result will be displayed here");
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
                hideKeyboard();
                progressDialog = MainActivity.showProgressLoader(getActivity());
                progressDialog.show();
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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    resultText = finalResult;
                                    progressDialog.hide();
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

    public static String excutePost(String targetURL, String urlParameters) {
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

    private void hideKeyboard(){

        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

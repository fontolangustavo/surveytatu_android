package br.com.fontolan.surveytatu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SurveyActivity extends AppCompatActivity {
    private String api_url = "https://APIURL/questions";
    private String token = "";
    private Button btn;
    private JSONArray questions;

    private ArrayList<EditText> questions_text = new ArrayList<EditText>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        Bundle b = getIntent().getExtras();
        this.token = b.getString("token");
        this.btn = (Button) findViewById(R.id.btn_enviar);

        try {
            this.getQuestions();

        } catch (Exception e) {
            Log.d("getQuestions Exception", e.getMessage());
        }
    }

    private void getQuestions() {
        StringRequest getRequest = new StringRequest(Request.Method.GET, this.api_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            Log.d("onResponse", "response => " + response);
                            SurveyActivity.this.questions = new JSONArray(response);
                            SurveyActivity.this.listQuestions();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR", "error => " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + SurveyActivity.this.token);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(getRequest);
    }

    private void updateQuestions(){
        StringRequest getRequest = new StringRequest(Request.Method.POST, this.api_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            Log.d("Response success", "response obj => " + response);
                        } catch (Exception e) {

                            e.printStackTrace();
                        }

                        SurveyActivity.this.btn.setEnabled(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR", "error => " + error.toString());
                        SurveyActivity.this.btn.setEnabled(true);
                    }
                }
        ) {

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                Log.d("questions json", SurveyActivity.this.toStringArray(SurveyActivity.this.questions));
                params.put("data", SurveyActivity.this.toStringArray(SurveyActivity.this.questions));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + SurveyActivity.this.token);
//                params.put("Content-Type", "application/json; charset=utf-8");

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(getRequest);
    }


    private void listQuestions() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_principal);

        for (int i = 0; i < this.questions.length(); i++) {
            try {
                JSONObject obj = (JSONObject) questions.get(i);


                EditText editText = new EditText(SurveyActivity.this.getApplication());

                editText.setId(obj.get("id") != null ? obj.getInt("id") : 0);
                editText.setText(obj.getString("value") != "null" ? obj.getString("value") : "");
                editText.setHint(obj.getString("title") != null ? obj.getString("title") : "");

                editText.setHeight(120);

                TableLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 20, 0, 0);

                editText.setLayoutParams(params);

                questions_text.add(editText);
                layout.addView(editText);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void salvarDados(View v) {
        this.btn.setEnabled(false);
        for (int i = 0; i < questions_text.size(); i++) {
            try {
                JSONObject obj = (JSONObject) questions.get(i);

                obj.put("value", questions_text.get(i).getText().toString() != ""? questions_text.get(i).getText().toString() : null);

                questions.put(i, obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        this.updateQuestions();

        Log.d("Questions", questions.toString());
    }

    public  String toStringArray(JSONArray array) {
        if(array==null)
            return null;

        String arr = "[";
        for(int i=0; i < array.length(); i++) {
            try {
                if(i < array.length() - 1){
                    arr += array.getJSONObject(i).toString() + ",";
                }else{
                    arr += array.getJSONObject(i).toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return arr + "]";
    }

}

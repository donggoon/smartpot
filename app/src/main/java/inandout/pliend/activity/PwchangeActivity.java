package inandout.pliend.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import inandout.pliend.R;
import inandout.pliend.app.AppConfig;
import inandout.pliend.app.AppController;
import inandout.pliend.helper.SQLiteHandler;
import inandout.pliend.helper.SessionManager;

public class PwchangeActivity extends AppCompatActivity{
    private static final String TAG = PwchangeActivity.class.getSimpleName();
    private Button btnComplete;
    private EditText inputCurrentPw;
    private EditText inputChangePw;
    private EditText inputCheckPw;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    String name;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwchange);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4CAF50")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);

        if(Build.VERSION.SDK_INT>=21){
            getWindow().setStatusBarColor(Color.parseColor("#43A047"));
        }
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        db = new SQLiteHandler(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        name = user.get("name");
        email = user.get("email");

        inputCurrentPw = (EditText) findViewById(R.id.password);
        inputChangePw = (EditText) findViewById(R.id.new_pw);
        inputCheckPw = (EditText) findViewById(R.id.check_pw);

        btnComplete=(Button) findViewById(R.id.btn_change);

        btnComplete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String currentPw = inputCurrentPw.getText().toString().trim();
                String changePw = inputChangePw.getText().toString().trim();
                if (!validate()) {
                    onSignupFailed();
                    return;
                }
                btnComplete.setEnabled(false);

                registerUser(name, email, changePw);
            }
        });
    }
    private void registerUser(final String name, final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_change";

        pDialog.setMessage("Loading ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PW_CHANGE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Change Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(name, email, uid, created_at);

                        Toast.makeText(getApplicationContext(), "비밀번호 변경에 성공하였습니다!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                PwchangeActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Password Change Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams()
                    throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "비밀번호 변경에 실패하였습니다", Toast.LENGTH_LONG).show();
        btnComplete.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String changePw = inputChangePw.getText().toString();
        String checkPw = inputCheckPw.getText().toString();

        if (changePw.isEmpty() || changePw.length() < 4 || changePw.length() > 15) {
            inputChangePw.setError("4 - 15자리의 비밀번호를 입력하세요");
            valid = false;
        } else {
            if (changePw == checkPw) {
                inputChangePw.setError("비밀번호를 확인하세요");
                valid = false;
            }
            else inputChangePw.setError(null);
        }
        return valid;
    }

    public void pwchange_clear(View v){
        Intent i=new Intent(PwchangeActivity.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }
}
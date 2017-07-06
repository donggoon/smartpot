package inandout.pliend.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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

public class AddPlantActivity extends AppCompatActivity {
    private static final String TAG = AddPlantActivity.class.getSimpleName();
    AppController appController;
    Button addPlantBtn;
    Button linkToMainBtn;
    EditText editName;
    EditText editBirth;
    EditText editType;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        appController = (AppController)getApplicationContext();

        if(Build.VERSION.SDK_INT>=21){
            getWindow().setStatusBarColor(Color.parseColor("#FFBB00"));
        }
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        email = user.get("email");

        addPlantBtn = (Button) findViewById(R.id.btn_add_plant);
        linkToMainBtn = (Button) findViewById(R.id.btn_link_to_no_plant);

        editName = (EditText) findViewById(R.id.plant_name);
        editBirth = (EditText) findViewById(R.id.plant_birth);
        editType = (EditText) findViewById(R.id.plant_type);


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        addPlantBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String name = editName.getText().toString().trim();
                String birth = editBirth.getText().toString().trim();
                String type = editType.getText().toString().trim();

                if(!validate()) {
                    onRegisterFailed();
                    return;
                }

                addPlantBtn.setEnabled(false);

                addPlant(email, name, birth, type);

                Intent i = new Intent(getApplicationContext(),
                        StartActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void addPlant(final String email, final String name, final String birth, final String type) {
        // Tag used to cancel the request
        String tag_string_req = "req_add";

        pDialog.setMessage("Loading ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ADD_PLANT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "식물 등록에 성공했습니다!", Toast.LENGTH_LONG).show();
                        db.updatePlant(email);
                        Log.d("등록 성공", "1");

                        // Inserting row in users table
                        AppController.getInstance().setIsPlantRegister(true);
                        // Launch login activity
                        Intent intent = new Intent(AddPlantActivity.this, MainActivity.class);
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
                Log.e(TAG, "Add Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams()
                    throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("name", name);
                params.put("birth", birth);
                params.put("type", type);

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

    public void onRegisterFailed() {
        Toast.makeText(getBaseContext(), "등록에 실패하였습니다", Toast.LENGTH_LONG).show();

        addPlantBtn.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = editName.getText().toString().trim();
        String birth = editBirth.getText().toString().trim();
        String type = editType.getText().toString().trim();

        if (name.isEmpty()) {
            editName.setError("이름을 입력하세요");
            valid = false;
        } else {
            editName.setError(null);
        }

        if (birth.isEmpty()) {
            editBirth.setError("생년월일을 입력하세요");
            valid = false;
        } else {
            editBirth.setError(null);
        }

        if (type.isEmpty()) {
            editType.setError("종류를 입력하세요");
            valid = false;
        } else {
            editType.setError(null);
        }
        return valid;
    }
}
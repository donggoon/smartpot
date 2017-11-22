package inandout.pliend.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import inandout.pliend.R;
import inandout.pliend.app.AppConfig;
import inandout.pliend.helper.SQLiteHandler;
import inandout.pliend.helper.SessionManager;
import inandout.pliend.store.DataPlant;

/**
 * Created by DK on 2017-08-03.
 */

public class AddPlantActivity extends AppCompatActivity {
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private Button btnSave;

    private ImageButton btnName;
    private ImageButton btnBirth;
    private ImageButton btnType;
    private ImageButton btnGrowth;

    private TextView currentName;
    private TextView currentBirth;
    private TextView currentType;
    private TextView currentGrowth;
    private TextView currentPerson;

    private SessionManager session;
    private SQLiteHandler db;

    private String email;
    private String name;
    private String yearOfBirth;
    private String monthOfBirth;
    private String dayOfBirth;
    private String type;
    private int level = 0;

    private boolean isNameCompleted = false;
    private boolean isBirthCompleted = false;
    private boolean isTypeCompleted = false;
    private boolean isGrowthCompleted = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        Intent intent = getIntent();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4CAF50")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#000000"));
        }

        currentName = (TextView) findViewById(R.id.currentName);
        currentBirth = (TextView) findViewById(R.id.currentBirth);
        currentType = (TextView) findViewById(R.id.currentType);
        currentGrowth = (TextView) findViewById(R.id.currentGrowth);
        currentPerson = (TextView) findViewById(R.id.currentPerson);

        btnName = (ImageButton) findViewById(R.id.editName);
        btnBirth = (ImageButton) findViewById(R.id.editBirth);
        btnType = (ImageButton) findViewById(R.id.editType);
        btnGrowth = (ImageButton) findViewById(R.id.editGrowth);

        btnSave = (Button) findViewById(R.id.btn_save);

        currentName.setText(intent.getStringExtra("name"));
        currentBirth.setText(intent.getStringExtra("birth"));
        currentType.setText(intent.getStringExtra("type"));
        currentGrowth.setText(intent.getStringExtra("growth"));
        currentPerson.setText(intent.getStringExtra("owner"));

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        email = user.get("email");

        // Register Button Click event

        btnName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final EditText editText = new EditText(AddPlantActivity.this);
                editText.setLines(1);
                editText.setMaxLines(1);

                try {
                    DialogInterface.OnClickListener commitListener = new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            name = editText.getText().toString();
                            currentName.setText(name);
                            isNameCompleted = true;
                        }
                    };
                    DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    };
                    new AlertDialog.Builder(AddPlantActivity.this, R.style.MyAlertDialogStyle)
                            .setTitle("식물 이름 입력")
                            .setView(editText, 100, 50, 100, 0)
                            .setPositiveButton("확인", commitListener)
                            .setNegativeButton("취소", cancelListener)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnBirth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(AddPlantActivity.this, listener, year, month, day);
                dialog.show();
            }
        });

        btnType.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final EditText editText = new EditText(AddPlantActivity.this);
                editText.setLines(1);
                editText.setMaxLines(1);

                try {
                    DialogInterface.OnClickListener commitListener = new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            type = editText.getText().toString();
                            currentType.setText(type);
                            isTypeCompleted = true;
                        }
                    };
                    DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    };
                    new AlertDialog.Builder(AddPlantActivity.this, R.style.MyAlertDialogStyle)
                            .setTitle("식물 종류 입력")
                            .setView(editText, 100, 50, 100, 0)
                            .setPositiveButton("확인", commitListener)
                            .setNegativeButton("취소", cancelListener)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnGrowth.setOnClickListener(new View.OnClickListener() {
            String[] growths = {"씨앗", "새싹", "꽃"};
            int checkedItem = -1;

            public void onClick(View view) {
                try {
                    DialogInterface.OnClickListener commitListener = new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            currentGrowth.setText(growths[level]);
                            isGrowthCompleted = true;
                        }
                    };
                    DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    };
                    new AlertDialog.Builder(AddPlantActivity.this, R.style.MyAlertDialogStyle)
                            .setTitle("성장 정도 선택")
                            .setSingleChoiceItems(growths, checkedItem, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    level = which;
                                }
                            })
                            .setPositiveButton("확인", commitListener)
                            .setNegativeButton("취소", cancelListener)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(!isNameCompleted) {
                    Toast.makeText(getApplicationContext(), "이름을 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if(!isBirthCompleted) {
                    Toast.makeText(getApplicationContext(), "생일을 등록하세요!", Toast.LENGTH_SHORT).show();
                } else if(!isTypeCompleted) {
                    Toast.makeText(getApplicationContext(), "종류를 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if(!isGrowthCompleted) {
                    Toast.makeText(getApplicationContext(), "성장도를 선택하세요!", Toast.LENGTH_SHORT).show();
                } else {
                    new AsyncFetch(email, name, yearOfBirth, monthOfBirth, dayOfBirth, type, level, getApplicationContext()).execute();
                }
            }
        });
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            yearOfBirth = String.valueOf(year);
            monthOfBirth = String.valueOf(monthOfYear + 1);
            dayOfBirth = String.valueOf(dayOfMonth);
            String birth = String.valueOf(year) + "년 " + String.valueOf(monthOfYear + 1) + "월 " + String.valueOf(dayOfMonth) + "일";
            currentBirth.setText(birth);
            isBirthCompleted = true;
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }

    // Create class AsyncFetch
    private class AsyncFetch extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        String email;
        String name;
        String birthYear;
        String birthMonth;
        String birthDay;
        String type;
        int level;
        private Context context;

        public AsyncFetch(String email, String name, String birthYear, String birthMonth, String birthDay, String type, int level, Context context) {
            this.email = email;
            this.name = name;
            this.birthYear = birthYear;
            this.birthMonth = birthMonth;
            this.birthDay = birthDay;
            this.type = type;
            this.level = level;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL(AppConfig.URL_REG_PLANT);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput to true as we send and recieve data
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // add parameter to our above url
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("email", email)
                        .appendQueryParameter("name", name)
                        .appendQueryParameter("birthYear", birthYear)
                        .appendQueryParameter("birthMonth", birthMonth)
                        .appendQueryParameter("birthDay", birthDay)
                        .appendQueryParameter("type", type)
                        .appendQueryParameter("level", String.valueOf(level));
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }

            try {
                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {
                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {
                    return ("Connection error");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("success")) {
                Toast.makeText(getApplicationContext(), "식물 등록에 성공했습니다!", Toast.LENGTH_LONG).show();
                // db.updatePlant(email);
                Log.d("등록 성공", "1");
                // Launch login activity
                Intent intent = new Intent(AddPlantActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "식물 등록에 실패했습니다!", Toast.LENGTH_LONG).show();
                // db.updatePlant(email);
                Log.d("등록 실패", "1");
            }
        }
    }
}

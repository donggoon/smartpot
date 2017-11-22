package inandout.pliend.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import inandout.pliend.R;
import inandout.pliend.app.AppConfig;
import inandout.pliend.app.AppController;
import inandout.pliend.helper.SQLiteHandler;
import inandout.pliend.helper.SessionManager;
import inandout.pliend.store.DataPlant;
import inandout.pliend.store.TabPagerAdapter;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by DK on 2016-10-17.
 */
public class MainActivity extends AppCompatActivity {
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String name;
    private String email;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private SQLiteHandler db;
    private SessionManager session;
    private TimerTask tt;

    private Intent intent;
    // private RestartService restartService;

    Button btnMypage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btnMypage = (Button) findViewById(R.id.btn_mypage);

        if(Build.VERSION.SDK_INT>=21){
            getWindow().setStatusBarColor(Color.parseColor("#000000"));
        }

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        final String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("token", token);
        if(session.isLoggedIn()) {
            HashMap<String, String> user = db.getUserDetails();
            name = user.get("name");
            email = user.get("email");

            new Thread() {
                public void run() {
                    Log.d("thread check", token);
                    regEmailToPushServer(email, token);
                    // savePreferences("complete");
                }
            }.start();

            btnMypage.setText(name.substring(1));

            tt = new TimerTask() {
                @Override
                public void run() {
                    Log.d(String.valueOf(AppController.getInstance().getStatusType()), "thread check");
                    if(AppController.getInstance().getStatusType() == 4) {
                        AppController.getInstance().setStatusType(1);
                        if(AppController.getInstance().getIsBluetooth()) {
                            ((AddBluetoothActivity) AddBluetoothActivity.mContext).sendData("1");
                        }
                    }
                    new MainActivity.AsyncFetch(email, getApplicationContext()).execute();
                }
            };

            Timer timer = new Timer();
            timer.schedule(tt, 0, 70000);
        } else {
            logoutUser();
        }

        btnMypage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MypageActivity.class);
                startActivity(intent);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //탭 구현 코드
        // Initializing the TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("식물").setIcon(R.drawable.plant_tab));
        tabLayout.setTabTextColors(getResources().getColor(R.color.tabNo), getResources().getColor(R.color.tabYes));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("기기").setIcon(R.drawable.case_tab));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("퀘스트").setIcon(R.drawable.quest_tab));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("상태").setIcon(R.drawable.tamagotchi_tab));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Initializing ViewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        // Creating TabPagerAdapter adapter
        final TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // Set TabSelectedListener

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), false);
                pagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), false);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Log.i("MainActivity","onDestroy");
        //브로드 캐스트 해제
        // unregisterReceiver(restartService);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        session.setLogin(false);
        mAuth.signOut();
        db.deleteUsers();
        if(tt != null) {

            tt.cancel();
        }


        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void regEmailToPushServer(String email, String token) {
        // Add custom implementation, as needed.
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("token", token)
                .build();

        //request
        Request request = new Request.Builder()
                .url(AppConfig.URL_REG_TOKEN)
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class AsyncFetch extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        String email;
        private Context context;

        public AsyncFetch(String email, Context context) {
            this.email = email;
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
                url = new URL(AppConfig.URL_LOAD_SENSOR);

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
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("email", email);
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

            //this method will be running on UI thread
            // pdLoading.dismiss();
            List<DataPlant> data = new ArrayList<>();

            if (!result.equals("no rows")) {
                try {
                    JSONArray jArray = new JSONArray(result);

                    JSONObject user = jArray.getJSONObject(0);

                    int humi = Integer.parseInt(user.getString("humi"));
                    int light = Integer.parseInt(user.getString("light"));
                    int temp = Integer.parseInt(user.getString("temp"));

                    Log.d(user.getString("humi"), user.getString("temp"));

                    AppController.getInstance().setCurrentHumi(humi);
                    AppController.getInstance().setCurrentLight(light);
                    AppController.getInstance().setCurrentTemp(temp);

                    if(humi < 780 && light < 600 && temp > 20) {
                        Log.d("good", "check");
                       if(AppController.getInstance().getStatusType() != 1) {
                           Log.d("quest", "check");
                           Log.d(String.valueOf(AppController.getInstance().getStatusType()), "status check");
                           Toast.makeText(getApplicationContext(), "퀘스트를 완료했습니다!", Toast.LENGTH_LONG).show();
                           AppController.getInstance().setStatusType(4);
                           if (AppController.getInstance().getIsBluetooth()) {
                               ((AddBluetoothActivity) AddBluetoothActivity.mContext).sendData("4");
                           }
                       }
                       else {
                           Log.d("noquest", "check");
                           AppController.getInstance().setStatusType(1);
                           if (AppController.getInstance().getIsBluetooth()) {
                               ((AddBluetoothActivity) AddBluetoothActivity.mContext).sendData("1");
                           }
                       }
                    }


                    else {
                        if (humi >= 780) { // 목마를 때
                            AppController.getInstance().setStatusType(3);
                            if (AppController.getInstance().getIsBluetooth()) {
                                ((AddBluetoothActivity) AddBluetoothActivity.mContext).sendData("3");
                            }
                        }
                        if (light >= 600) { // 어두울 때
                            AppController.getInstance().setStatusType(5);
                            if (AppController.getInstance().getIsBluetooth()) {
                                ((AddBluetoothActivity) AddBluetoothActivity.mContext).sendData("5");
                            }
                        }
                        if (temp <= 20) { // 추울 때
                            AppController.getInstance().setStatusType(7);
                            if (AppController.getInstance().getIsBluetooth()) {
                                ((AddBluetoothActivity) AddBluetoothActivity.mContext).sendData("7");
                            }
                        }
                        new MainActivity.AsyncFetchForPush(humi, light, temp, getApplicationContext()).execute();
                    }

                    // db.updatePlant(email);
                    Log.d("값 측정 성공", "1");
                } catch (JSONException e) {
                    // You to understand what actually error is and handle it appropriately
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "값 측정에 실패했습니다!", Toast.LENGTH_LONG).show();
                // db.updatePlant(email);
                Log.d("로그인 실패", "1");
            }
        }
    }

    private class AsyncFetchForPush extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        int humi;
        int light;
        int temp;
        private Context context;

        public AsyncFetchForPush(int humi, int light, int temp, Context context) {
            this.humi = humi;
            this.light = light;
            this.temp = temp;
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
                url = new URL(AppConfig.URL_REG_SENSOR);

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
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("humi", String.valueOf(humi))
                        .appendQueryParameter("light", String.valueOf(light))
                        .appendQueryParameter("temp", String.valueOf(temp));
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

            //this method will be running on UI thread
            // pdLoading.dismiss();
            List<DataPlant> data = new ArrayList<>();

            if (result.equals("success")) {
                // Toast.makeText(getApplicationContext(), "회원가입에 성공했습니다!", Toast.LENGTH_LONG).show();
                // db.updatePlant(email);
                Log.d("푸시 성공", "1");
                // Launch login activity
            }
            else if(result.equals("delete")) {
                Toast.makeText(getApplicationContext(), "퀘스트를 완료했습니다!", Toast.LENGTH_LONG).show();
                // db.updatePlant(email);
                Log.d("퀘스트 성공", "1");
            }
            else {
                // Toast.makeText(getApplicationContext(), "푸시에 실패했습니다!", Toast.LENGTH_LONG).show();
                // db.updatePlant(email);
                Log.d("푸시 실패", "1");
            }
        }
    }
}
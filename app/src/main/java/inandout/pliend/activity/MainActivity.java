package inandout.pliend.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import inandout.pliend.R;
import inandout.pliend.app.AppConfig;
import inandout.pliend.app.AppController;
import inandout.pliend.helper.SQLiteHandler;
import inandout.pliend.helper.SessionManager;
import inandout.pliend.service.PersistentService;
import inandout.pliend.service.RestartService;
import inandout.pliend.store.TabPagerAdapter;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by DK on 2016-10-17.
 */
public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String name;
    private String email;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private SQLiteHandler db;
    private SessionManager session;

    private Intent intent;
    private RestartService restartService;

    Button btnMypage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btnMypage = (Button) findViewById(R.id.btn_mypage);

        if(Build.VERSION.SDK_INT>=21){
            getWindow().setStatusBarColor(Color.parseColor("#43A047"));
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
        tabLayout.addTab(tabLayout.newTab().setText("성장도").setIcon(R.drawable.analyze_tab));
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

        //  initData();
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

    private void initData(){
        //리스타트 서비스 생성
        restartService = new RestartService();
        intent = new Intent(MainActivity.this, PersistentService.class);

        IntentFilter intentFilter = new IntentFilter("inandout.pliend.service.PersistentService");
        //브로드 캐스트에 등록
        registerReceiver(restartService, intentFilter);
        // 서비스 시작
        startService(intent);
    }
}
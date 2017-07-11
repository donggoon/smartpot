package inandout.pliend.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

import inandout.pliend.R;
import inandout.pliend.helper.SQLiteHandler;
import inandout.pliend.helper.SessionManager;

public class MypageActivity extends AppCompatActivity{
    private SQLiteHandler db;
    TextView textName;
    TextView textEmail;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4CAF50")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);

        // session manager
        session = new SessionManager(getApplicationContext());

        if(Build.VERSION.SDK_INT>=21){
            getWindow().setStatusBarColor(Color.parseColor("#43A047"));
        }

        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        String name = user.get("name");
        String email = user.get("email");

        textName = (TextView) findViewById(R.id.textName);
        textEmail= (TextView) findViewById(R.id.textEmail);

        textName.setText(name);
        textEmail.setText(email);
    }
    public void pwchange_onclick(View v){
        Intent i=new Intent(MypageActivity.this, PwchangeActivity.class);
        startActivity(i);
    }
    /*
    public void changeplant_onclick(View v){
        Intent i=new Intent(MypageActivity.this, ChangePlantActivity.class);
        startActivity(i);
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }
}

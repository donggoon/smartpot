package inandout.pliend.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
import java.util.List;

import inandout.pliend.R;
import inandout.pliend.activity.AddPlantActivity;
import inandout.pliend.app.AppConfig;
import inandout.pliend.app.AppController;
import inandout.pliend.helper.SQLiteHandler;
import inandout.pliend.store.AdapterPlant;
import inandout.pliend.store.DataPlant;


public class PlantMainFragment extends Fragment {
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    Intent intent;
    private SQLiteHandler db;
    String plant;
    String email;
    int level;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    ImageView plantImg;

    private RecyclerView mRVPlant;
    private AdapterPlant mAdapter;

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_recycler_plant, null);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // SqLite database handler
        db = new SQLiteHandler(getActivity());

        HashMap<String, String> user = db.getUserDetails();
        // plantImg = (ImageView)view.findViewById(R.id.main_leaf);

        FloatingActionButton addPlantBtn = (FloatingActionButton) view.findViewById(R.id.btn_add);
        addPlantBtn.setBackgroundColor(Color.parseColor("#4CAF50"));
        addPlantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getActivity(), AddPlantActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        email = user.get("email");
        // email = mUser.getEmail();

        new AsyncFetch(email, getActivity()).execute();

        return view;
    }

    // Create class AsyncFetch
    private class AsyncFetch extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        String email;
        private Context context;

        public AsyncFetch(String param, Context context) {
            this.email = param;
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
                url = new URL(AppConfig.URL_LOAD_PLANT);

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

            if (result.equals("[]")) {
                DataPlant noData = new DataPlant();
                noData.plantName = "식물을 등록해주세요";
                noData.plantBirth = " ";
                noData.plantBirth = " ";
                noData.plantLevel = " ";
                data.add(noData);

                mRVPlant = (RecyclerView) view.findViewById(R.id.list);
                mAdapter = new AdapterPlant(getActivity(), data);
                mRVPlant.setAdapter(mAdapter);
                mRVPlant.setLayoutManager(new LinearLayoutManager(getActivity()));

            } else {
                try {
                    JSONArray jArray = new JSONArray(result);

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        DataPlant storeData = new DataPlant();
                        storeData.plantName = json_data.getString("name");
                        storeData.plantBirth = json_data.getString("birth");
                        storeData.plantType = json_data.getString("type");
                        storeData.plantLevel = json_data.getString("level");

                        if (Integer.parseInt(storeData.plantLevel) == 1) {
                            storeData.plantLevel = "씨앗";
                        }
                        data.add(storeData);

                        AppController.getInstance().setPlantName(storeData.plantName);
                        AppController.getInstance().setPlantBirth(storeData.plantBirth);
                        AppController.getInstance().setPlantType(storeData.plantType);
                        AppController.getInstance().setPlantLevel(storeData.plantLevel);

                    /*
                    textName.setText("제 이름은 " + storeData.plantName + "입니다.");
                    textBirth.setText("제 생일은 " + storeData.plantBirth + "입니다.");
                    textType.setText("저는 " + storeData.plantType + "입니다.");
                    String current = "새싹";
                    if(Integer.parseInt(storeData.plantLevel) == 1) {
                        plantImg.setImageResource(R.drawable.seed);
                        current = "씨앗";
                    }
                    textLevel.setText("저는 지금 " + current + "입니다.");
                    */

                    }
                    // Setup and Handover data to recyclerview
                    mRVPlant = (RecyclerView) view.findViewById(R.id.list);
                    mAdapter = new AdapterPlant(getActivity(), data);
                    mRVPlant.setAdapter(mAdapter);
                    mRVPlant.setLayoutManager(new LinearLayoutManager(getActivity()));

                } catch (JSONException e) {
                    // You to understand what actually error is and handle it appropriately
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
package inandout.pliend.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

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

import inandout.pliend.R;
import inandout.pliend.activity.AddPlantActivity;
import inandout.pliend.app.AppConfig;
import inandout.pliend.app.AppController;
import inandout.pliend.helper.SQLiteHandler;
import inandout.pliend.store.DataStore;


public class PlantMainFragment extends Fragment {
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    Intent intent;
    private SQLiteHandler db;
    String plant;
    String email;
    int plantReg;

    TextView textName;
    TextView textBirth;
    TextView textType;
    TextView textLevel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plant_main, null);

        // SqLite database handler
        db = new SQLiteHandler(getActivity());

        HashMap<String, String> user = db.getUserDetails();
        plant = user.get("plant");

        if(Integer.parseInt(plant) == 0) {
            view = inflater.inflate(R.layout.fragment_no_plant_main, null);

            ImageButton addPlantImageBtn = (ImageButton)view.findViewById(R.id.btn_add_plant_image);
            Button addPlantTextBtn = (Button)view.findViewById(R.id.btn_add_plant_text);

            addPlantImageBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    try {
                        intent = new Intent(getActivity(), AddPlantActivity.class);
                        startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            addPlantTextBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    intent = new Intent(getActivity(), AddPlantActivity.class);
                    startActivity(intent);
                }
            });
        }
        else {
            // session manager
            // Fetching user details from sqlite
            email = user.get("email");
            textName = (TextView) view.findViewById(R.id.text_name);
            textBirth = (TextView) view.findViewById(R.id.text_birth);
            textType = (TextView) view.findViewById(R.id.text_type);
            textLevel = (TextView) view.findViewById(R.id.text_level);

            new AsyncFetch(email, getActivity()).execute();
        }
        return view;
    }
    // Create class AsyncFetch
    private class AsyncFetch extends AsyncTask<String, String, String> {

        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        HttpURLConnection conn;
        URL url = null;
        String searchQuery;
        private Context context;

        public AsyncFetch(String searchQuery, Context context){
            this.searchQuery=searchQuery;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

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
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("searchQuery", searchQuery);
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
                    return("Connection error");
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
            pdLoading.dismiss();

            if(result.equals("no rows")) {
            } else{

                try {
                    JSONArray jArray = new JSONArray(result);

                    JSONObject json_data = jArray.getJSONObject(0);
                    DataStore storeData = new DataStore();
                    storeData.storeName = json_data.getString("name");
                    storeData.storeBirth = json_data.getString("birth");
                    storeData.storeType = json_data.getString("type");
                    storeData.storeLevel = json_data.getString("level");

                    textName.setText(storeData.storeName);
                    textBirth.setText(storeData.storeBirth);
                    textType.setText(storeData.storeType);
                    textLevel.setText(storeData.storeLevel);

                } catch (JSONException e) {
                    // You to understand what actually error is and handle it appropriately
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}

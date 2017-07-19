package inandout.pliend.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import inandout.pliend.R;
import inandout.pliend.app.AppConfig;
import inandout.pliend.helper.SQLiteHandler;
import inandout.pliend.store.AdapterQuest;
import inandout.pliend.store.DataQuest;

/**
 * Created by DK on 2017-07-13.
 */

public class QuestFragment extends Fragment {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private RecyclerView mRVQuest;
    private AdapterQuest mAdapter;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private SQLiteHandler db;
    String email;

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_recycler_quest, null);

        db = new SQLiteHandler(getActivity());
        HashMap<String, String> user  = db.getUserDetails();
        email = user.get("email");
        /*mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        email = mUser.getEmail();
*/
        new QuestFragment.AsyncFetch(email, getActivity()).execute();

        return view;
    }

    // Create class AsyncFetch
    private class AsyncFetch extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        String email;
        private Context context;

        public AsyncFetch(String param, Context context){
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
                url = new URL(AppConfig.URL_LOAD_QUEST);

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
            List<DataQuest> data = new ArrayList<>();

            if(result.equals("[]")) {
                DataQuest noData = new DataQuest();
                noData.questContent = "퀘스트가 없습니다";
                noData.questDate = " ";
                data.add(noData);

                mRVQuest = (RecyclerView) view.findViewById(R.id.list);
                mAdapter = new AdapterQuest(getActivity(), data);
                mRVQuest.setAdapter(mAdapter);
                mRVQuest.setLayoutManager(new LinearLayoutManager(getActivity()));
            } else {
                try {
                    Calendar c = Calendar.getInstance();

                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    JSONArray jArray = new JSONArray(result);

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        DataQuest questData = new DataQuest();
                        questData.questContent = json_data.getString("content");
                        questData.questYear = Integer.parseInt(json_data.getString("year"));
                        questData.questMonth = Integer.parseInt(json_data.getString("month"));
                        questData.questDay = Integer.parseInt(json_data.getString("day"));
                        questData.questHour = Integer.parseInt(json_data.getString("hour"));
                        questData.questMinute = Integer.parseInt(json_data.getString("minute"));
                        questData.questAm_pm = Integer.parseInt(json_data.getString("am_pm"));

                        if (questData.questYear < year) {
                            questData.questDate = String.valueOf(questData.questYear) + "/" +
                                    String.valueOf(questData.questMonth) + "/" + String.valueOf(questData.questDay);
                        } else if (questData.questMonth < month) {
                            questData.questDate = String.valueOf(questData.questYear) + "/" +
                                    String.valueOf(questData.questMonth) + "/" + String.valueOf(questData.questDay);
                        } else if (questData.questDay < day) {
                            questData.questDate = String.valueOf(questData.questYear) + "/" +
                                    String.valueOf(questData.questMonth) + "/" + String.valueOf(questData.questDay);
                        } else {
                            if (questData.questAm_pm == 1) {
                                questData.questDate = "오후 " + String.valueOf(questData.questHour) + ":" + String.valueOf(questData.questMinute);
                            } else {
                                questData.questDate = "오전 " + String.valueOf(questData.questHour) + ":" + String.valueOf(questData.questMinute);
                            }
                        }
                        data.add(questData);
                        Log.d(data.get(i).questContent.toString(), "check");
                    }

                    // Setup and Handover data to recyclerview
                    mRVQuest = (RecyclerView) view.findViewById(R.id.list);
                    mAdapter = new AdapterQuest(getActivity(), data);
                    mRVQuest.setAdapter(mAdapter);
                    mRVQuest.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    // You to understand what actually error is and handle it appropriately
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), result.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}

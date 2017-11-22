package inandout.pliend.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.List;

import inandout.pliend.R;
import inandout.pliend.activity.AddPlantActivity;
import inandout.pliend.activity.ModifyPlantActivity;
import inandout.pliend.app.AppConfig;
import inandout.pliend.app.AppController;
import inandout.pliend.helper.SQLiteHandler;
import inandout.pliend.store.DataPlant;

import static android.content.Context.MODE_PRIVATE;


public class PlantFragment extends Fragment {
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private SQLiteHandler db;
    private String email;
    private String name;
    private String birth;
    private String type;
    private String growth;
    private String plant;

    private TextView textName;
    private TextView textBirth;
    private TextView textType;
    private TextView textLevel;
    private TextView textOwner;

    private List<DataPlant> data;
    private DataPlant storeData;

    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_CROP = 2;

    private Uri photoURI = null;

    private ImageView ivProfile;
    private ImageView ivCover;

    private Button detailInfoBtn;

    private SharedPreferences sharedPreferences;
    private SharedPreferences getProfileSharedPreferences;
    private SharedPreferences getCoverSharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean isProfile = false;

    View view;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new SQLiteHandler(getActivity());
        HashMap<String, String> user = db.getUserDetails();
        email = user.get("email");
        name = user.get("name");

        data = new ArrayList<>();
        storeData = new DataPlant();

        new AsyncFetch(email, getActivity()).execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_plant, null);

        FloatingActionButton seeStatusBtn = (FloatingActionButton) view.findViewById(R.id.btn_bluetooth);
        seeStatusBtn.setImageResource(R.drawable.ic_show_chart_black_24dp);
        seeStatusBtn.setBackgroundColor(Color.parseColor("#4CAF50"));
        seeStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getActivity(), AddPlantActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        textName = (TextView) view.findViewById(R.id.textName);
        textBirth = (TextView) view.findViewById(R.id.textBirth);
        textType = (TextView) view.findViewById(R.id.textType);
        textLevel = (TextView) view.findViewById(R.id.textLevel);
        textLevel = (TextView) view.findViewById(R.id.textLevel);
        textOwner = (TextView) view.findViewById(R.id.textOwner);

        ivProfile = (ImageView) view.findViewById(R.id.img_profile);
        ivCover = (ImageView) view.findViewById(R.id.img_cover);

        detailInfoBtn = (Button) view.findViewById(R.id.btnSetting);

        getProfileSharedPreferences = getActivity().getSharedPreferences("profile", MODE_PRIVATE);
        String profileImage = getProfileSharedPreferences.getString("profileImage", "");
        Bitmap profileBitmap = StringToBitMap(profileImage);
        Drawable profileDrawable = getDrawableFromBitmap(profileBitmap);

        getCoverSharedPreferences = getActivity().getSharedPreferences("cover", MODE_PRIVATE);
        String coverImage = getCoverSharedPreferences.getString("coverImage", "");
        Bitmap coverBit = StringToBitMap(coverImage);
        Drawable coverDrawable = getDrawableFromBitmap(coverBit);

        // ivProfile.setImageResource(0);
        // ivProfile.setImageDrawable(profileDrawable);

        // ivCover.setBackground(null);
        // ivCover.setBackground(coverDrawable);

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isProfile = true;
                try {
                    DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            dispatchTakePictureIntent();
                        }
                    };
                    DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            doTakeAlbumAction();
                        }
                    };
                    DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    };
                    new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
                            .setTitle("프로필 사진 업로드")
                            .setNeutralButton("사진 촬영", cameraListener)
                            .setPositiveButton("앨범 선택", albumListener)
                            .setNegativeButton("취소", cancelListener)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ivCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    isProfile = false;
                    DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dispatchTakePictureIntent();
                        }
                    };
                    DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            doTakeAlbumAction();
                        }
                    };
                    DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    };
                    new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
                            .setTitle("커버 사진 업로드")
                            .setNeutralButton("사진 촬영",cameraListener)
                            .setPositiveButton("앨범 선택", albumListener)
                            .setNegativeButton("취소",cancelListener)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        detailInfoBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ModifyPlantActivity.class);
                intent.putExtra("name", plant)
                        .putExtra("birth", birth)
                        .putExtra("birthYear", storeData.birthYear)
                        .putExtra("birthMonth", storeData.birthMonth)
                        .putExtra("birthDay", storeData.birthDay)
                        .putExtra("type", type)
                        .putExtra("growth", growth)
                        .putExtra("level", storeData.level)
                        .putExtra("owner", name);
                startActivity(intent);
            }
        });

        return view;
    }

    public void onStart() {
        super.onStart();
    }

    public void onPause() {
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != getActivity().RESULT_OK) {
            Toast.makeText(getActivity(), "onActivityResult : RESULT_NOT_OK", Toast.LENGTH_LONG).show();
        }
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO: // 앨범이미지 가져오기
                if(data.getData() == null) break;
                photoURI = data.getData();
                try {
                    Bitmap ib = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoURI);
                    /*if(isProfile) {
                        ib = Bitmap.createScaledBitmap(ib, 156, 156, true);
                    } else {
                        ib = Bitmap.createScaledBitmap(ib, getActivity().getResources().getDisplayMetrics().widthPixels, 156, true);
                    }*/
                    Drawable d = getDrawableFromBitmap(ib);

                    if(isProfile) {
                        ivProfile.setImageResource(0);
                        ivProfile.setImageDrawable(d);
                    } else {
                        ivCover.setBackground(null);
                        ivCover.setBackground(d);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Bitmap save = null;

                try {
                    save = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoURI);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(isProfile) {
                    String image = BitMapToString(save);
                    sharedPreferences = getActivity().getSharedPreferences("profile", MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putString("profileImage", image);

                    editor.commit();
                } else {
                    String image = BitMapToString(save);
                    sharedPreferences = getActivity().getSharedPreferences("cover", MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putString("coverImage", image);

                    editor.commit();
                }

                break;

            case REQUEST_IMAGE_CAPTURE:
                Bitmap bmp = BitmapFactory.decodeFile(photoURI.getPath());

                if(isProfile) {
                    ivProfile.setBackground(null);
                    Drawable d = getDrawableFromBitmap(bmp);
                    ivProfile.setBackground(d);
                } else {
                    ivCover.setBackground(null);
                    Drawable d = getDrawableFromBitmap(bmp);
                    ivCover.setBackground(d);
                }
                break;

            case REQUEST_IMAGE_CROP:
                Bitmap photo = BitmapFactory.decodeFile(photoURI.getPath());
                if(isProfile) ivProfile.setImageBitmap(photo);
                else ivCover.setImageBitmap(photo);

                break;
        }
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
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

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
            if (result.equals("[]") || result.equals("no rows")) {
                storeData.plantName = "이름을 등록해주세요.";
                storeData.plantBirth = "생일을 등록해주세요.";
                storeData.plantType = "종류를 등록해주세요.";
                storeData.plantLevel = "성장도를 등록해주세요.";
                data.add(storeData);

                Intent intent = new Intent(getActivity(), AddPlantActivity.class);
                intent.putExtra("name", storeData.plantName)
                        .putExtra("birth", storeData.plantBirth)
                        .putExtra("type", storeData.plantType)
                        .putExtra("growth", storeData.plantLevel)
                        .putExtra("owner", name);
                startActivity(intent);
            } else {
                try {
                    JSONArray jArray = new JSONArray(result);

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        storeData.plantName = json_data.getString("name");
                        storeData.birthYear = json_data.getString("year_of_birth");
                        storeData.birthMonth = json_data.getString("month_of_birth");
                        storeData.birthDay = json_data.getString("day_of_birth");

                        storeData.plantBirth = storeData.birthYear + "년 " + storeData.birthMonth + "월 " + storeData.birthDay + "일";
                        storeData.plantType = json_data.getString("type");
                        storeData.plantLevel = json_data.getString("level");

                        if(Integer.parseInt(storeData.plantLevel) == 0) {
                            storeData.plantLevel = "씨앗";
                        } else if(Integer.parseInt(storeData.plantLevel) == 1) {
                            storeData.plantLevel = "새싹";
                        } else if(Integer.parseInt(storeData.plantLevel) == 2) {
                            storeData.plantLevel = "꽃";
                        }
                        data.add(storeData);
                    }

                    plant = data.get(0).plantName;
                    birth = data.get(0).plantBirth;
                    type = data.get(0).plantType;
                    growth = data.get(0).plantLevel;

                    textName.setText(data.get(0).plantName);
                    textBirth.setText(data.get(0).plantBirth);
                    textType.setText(data.get(0).plantType);
                    textLevel.setText(data.get(0).plantLevel);
                    textOwner.setText(name);

                    AppController.getInstance().setPlantName(plant);
                    AppController.getInstance().setPlantBirth(birth);
                    AppController.getInstance().setPlantType(type);
                    AppController.getInstance().setPlantLevel(growth);

                } catch (JSONException e) {
                    // You to understand what actually error is and handle it appropriately
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(); // 사진찍은 후 저장할 임시 파일(껍데기)
            } catch (IOException ex) {
                Toast.makeText(getActivity(), "createImageFile Failed", Toast.LENGTH_LONG).show();
            }

            if (photoFile != null) {
                Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE ); // 동기화
                photoURI = Uri.fromFile(photoFile); // 임시 파일의 위치,경로 가져옴
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI); // 임시 파일 위치에 저장
                mediaScanIntent.setData(photoURI); // 동기화
                getActivity().sendBroadcast(mediaScanIntent); // 동기화
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "tmp_" + String.valueOf(System.currentTimeMillis());
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "FloMate/");
        File file = File.createTempFile(imageFileName, ".jpg", storageDir);

        return file;
    }

    private void doTakeAlbumAction()
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    private void cropImage() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setDataAndType(photoURI, "image/*");
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", photoURI); // 크랍된 이미지를 해당 경로에 저장
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    public Drawable getDrawableFromBitmap(Bitmap bitmap){
        Drawable d = new BitmapDrawable(bitmap);
        return d;
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public static Bitmap getResizedBitmap(Resources resources, int id, int size, int width, int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = size;
        Bitmap src = BitmapFactory.decodeResource(resources, id, options);
        Bitmap resized = Bitmap.createScaledBitmap(src, width, height, true);
        return resized;
    }
}
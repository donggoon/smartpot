package inandout.pliend.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    Button addPlantImage;
    Button linkToMainBtn;

    EditText editName;
    EditText editBirth;
    EditText editType;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    String email;

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;

    private Uri mImageCaptureUri;
    private ImageView iv_UserPhoto;
    private int id_view;
    private String absoultePath;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        appController = (AppController)getApplicationContext();

        if(Build.VERSION.SDK_INT>=21){
            getWindow().setStatusBarColor(Color.parseColor("#43A047"));
        }
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        email = user.get("email");

        addPlantBtn = (Button) findViewById(R.id.btn_add_plant);
        linkToMainBtn = (Button) findViewById(R.id.btn_link_to_no_plant);
        addPlantImage = (Button) findViewById(R.id.btn_add_plant_image);

        editName = (EditText) findViewById(R.id.plant_name);
        editBirth = (EditText) findViewById(R.id.plant_birth);
        editType = (EditText) findViewById(R.id.plant_type);


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //식물 이미지 넣기
        //디비에 저장해야함
        addPlantImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                  /* if(v.getId()==R.id.btn_signupfinish){
            SharedPreferences prefs = getSharedPreferences("login",0);

            String user_name = prefs.getString("USER_NAME","");
            db_manager.selectPhoto(user_name,mImageCaptureUri,absoultePath);

            Intent mainIntent = new Intent(SingUpPhotoActivity.this, LoginActivity.class);
            SignUpPhotoActivity.this.startActivity(mainIntent);
            SignUpPhotoActivity.this.finish();
        }*/
                if(v.getId() == R.id.btn_add_plant_image){
                    DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            doTakePhotoAction();
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
                    new AlertDialog.Builder(context).setTitle("업로드할 이미지 선택")
                            .setPositiveButton("사진촬영",cameraListener)
                            .setNeutralButton("앨범선택", albumListener)
                            .setNegativeButton("취소",cancelListener)
                            .show();
                }

            }
        });

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

    public void doTakePhotoAction(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //임시로 사용할 파일의 경로를 생성
        String url="tmp_"+ String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(MediaStore.EXTRA_OUTPUT,mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    //앨범에서 이미지 가져오기
    public void doTakeAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode!=RESULT_OK)
            return;

        switch (requestCode){
            case PICK_FROM_ALBUM: {
                mImageCaptureUri = data.getData();
                Log.d("SmartWheel",mImageCaptureUri.getPath().toString());
            }

            case PICK_FROM_CAMERA: {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri,"image/*");

                intent.putExtra("outputX",200); // x축 크기
                intent.putExtra("outputY",200); // y축 크기
                intent.putExtra("aspectX",1); // crop 박스의 x 축 비율
                intent.putExtra("aspectY",1); // crop 박스의 y 축 비율
                intent.putExtra("scale",true);
                intent.putExtra("return-data",true);
                startActivityForResult(intent,CROP_FROM_IMAGE);
                break;
            }

            case CROP_FROM_IMAGE: {
                if(resultCode != RESULT_OK){return;}
                final Bundle extras = data.getExtras();

                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartWheel"+ System.currentTimeMillis()+".jpg";

                if(extras!= null){
                    Bitmap photo = extras.getParcelable("data");
                    iv_UserPhoto.setImageBitmap(photo);

                    storeCropImage(photo,filePath);
                    absoultePath = filePath;
                    break;
                }
                File f = new File(mImageCaptureUri.getPath());
                if(f.exists()){f.delete();}
            }

        }
    }

    private void storeCropImage(Bitmap bitmap, String filePath){
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartWheel";
        File directory_SmartWheel = new File(dirPath);

        if(!directory_SmartWheel.exists()){directory_SmartWheel.mkdir();}
        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try{
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));

            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

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
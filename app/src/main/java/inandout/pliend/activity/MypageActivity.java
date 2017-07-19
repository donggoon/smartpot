package inandout.pliend.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import inandout.pliend.R;
import inandout.pliend.helper.SQLiteHandler;
import inandout.pliend.helper.SessionManager;

public class MypageActivity extends AppCompatActivity{
    private SQLiteHandler db;
    TextView textName;
    TextView textEmail;
    private SessionManager session;

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;

    private Uri mImageCaptureUri;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private ImageView iv_UserPhoto;
    private int id_view;
    private String absoultePath;

    final Context context = this;

    ImageView profile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4CAF50")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);

        session = new SessionManager(getApplicationContext());

        if(Build.VERSION.SDK_INT>=21){
            getWindow().setStatusBarColor(Color.parseColor("#43A047"));
        }

        /*mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();*/

        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        String name = user.get("name");
        String email = user.get("email");

        /*String name = mUser.getDisplayName();
        String email = mUser.getEmail();*/

        textName = (TextView) findViewById(R.id.textName);
        textEmail= (TextView) findViewById(R.id.textEmail);

        textName.setText(name);
        textEmail.setText(email);
    }

    public void pwchange_onclick(View v){
        Intent i = new Intent(MypageActivity.this, PwchangeActivity.class);
        startActivity(i);
    }

    public void logout_onclick(View v){
        logoutUser();
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

    //사용자 사진 등록하기
    //디비에 저장해야함
    //지금 사용하는 이미지 고정인데
    //그거 대신에 사진 선택하면 그 사진이
    //사용자가 지정한 사진이 되도록
    //코드수정해야됨
    //식물 추가에서도 똑같이 해야됨
    public void insert_picture_onclick(View v){
          /* if(v.getId()==R.id.btn_signupfinish){
            SharedPreferences prefs = getSharedPreferences("login",0);

            String user_name = prefs.getString("USER_NAME","");
            db_manager.selectPhoto(user_name,mImageCaptureUri,absoultePath);

            Intent mainIntent = new Intent(SingUpPhotoActivity.this, LoginActivity.class);
            SignUpPhotoActivity.this.startActivity(mainIntent);
            SignUpPhotoActivity.this.finish();
        }*/
        if(v.getId() == R.id.insert_picture){
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

                    .setNeutralButton("앨범선택", albumListener)
                    .setNegativeButton("취소",cancelListener)
                    .show();
        }
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

    private void logoutUser() {
        // mAuth.signOut();
        session.setLogin(false);

        // Launching the login activity

        Intent intent = new Intent(MypageActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

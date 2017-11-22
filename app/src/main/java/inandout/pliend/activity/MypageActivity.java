package inandout.pliend.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import inandout.pliend.R;
import inandout.pliend.app.AppController;
import inandout.pliend.helper.SQLiteHandler;
import inandout.pliend.helper.SessionManager;

public class MypageActivity extends AppCompatActivity{
    private SQLiteHandler db;
    TextView textName;
    TextView textEmail;
    TextView textPlant;
    private SessionManager session;

    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_CROP = 2;
    Uri photoURI = null;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    ImageView ivCapture;

    private SharedPreferences sharedPreferences;
    private SharedPreferences getSharedPreferences;
    private SharedPreferences.Editor editor;

    final Context context = this;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_new);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4CAF50")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        session = new SessionManager(getApplicationContext());
        /*ivCapture = (ImageView)findViewById(R.id.insert_picture);

        getSharedPreferences = getSharedPreferences("image", MODE_PRIVATE);
        String image = getSharedPreferences.getString("imagestrings","");
        Bitmap bit = StringToBitMap(image);
        Drawable dr = getDrawableFromBitmap(bit);

        ivCapture.setBackground(null);
        ivCapture.setBackground(dr);*/

        if(Build.VERSION.SDK_INT>=21){
            getWindow().setStatusBarColor(Color.parseColor("#000000"));
        }

        /*mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();*/

        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        String name = user.get("name");
        String email = user.get("email");

        /*String name = mUser.getDisplayName();
        String email = mUser.getEmail();*/

        textName = (TextView) findViewById(R.id.currentName);
        textEmail= (TextView) findViewById(R.id.currentEmail);
        textPlant = (TextView) findViewById(R.id.currentPlant);

        textName.setText(name);
        textEmail.setText(email);
        textPlant.setText(AppController.getInstance().getPlantName());
    }

    /*public void pwchange_onclick(View v){
        Intent i = new Intent(MypageActivity.this, PwchangeActivity.class);
        startActivity(i);
    }*/

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
    /*public void insert_picture_onclick(View v){
          *//* if(v.getId()==R.id.btn_signupfinish){
            SharedPreferences prefs = getSharedPreferences("login",0);

            String user_name = prefs.getString("USER_NAME","");
            db_manager.selectPhoto(user_name,mImageCaptureUri,absoultePath);

            Intent mainIntent = new Intent(SingUpPhotoActivity.this, LoginActivity.class);
            SignUpPhotoActivity.this.startActivity(mainIntent);
            SignUpPhotoActivity.this.finish();
        }*//*
        if(v.getId() == R.id.insert_picture){
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
            new AlertDialog.Builder(context).setTitle("업로드할 이미지 선택")
                    .setNeutralButton("사진촬영",cameraListener)
                    .setPositiveButton("앨범선택", albumListener)
                    .setNegativeButton("취소",cancelListener)
                    .show();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if (resultCode != RESULT_OK) {
            Toast.makeText(getApplicationContext(), "onActivityResult : RESULT_NOT_OK", Toast.LENGTH_LONG).show();
        }
        switch (requestCode) {

            case REQUEST_TAKE_PHOTO: // 앨범이미지 가져오기
                photoURI = data.getData();
                //break; //바로 cropImage()로 전달되도록
                Log.d("byk", photoURI.toString());

                try {
                    Bitmap ib = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
                    Log.d("bykk",ib.toString());
                    ivCapture.setBackground(null);
                    Drawable d = getDrawableFromBitmap(ib);

                    ivCapture.setBackground(d);
                    Log.d("bykk",d.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Bitmap save = null;
                try {
                    save = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String image = BitMapToString(save);
                sharedPreferences = getSharedPreferences("image", MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("imagestrings",image);

                editor.commit();

                break;

            case REQUEST_IMAGE_CAPTURE:
                //cropImage();

                Bitmap bmp = BitmapFactory.decodeFile(photoURI.getPath());

                Log.d("byk",data.getExtras().toString());

                ivCapture.setBackground(null);
                Drawable d = getDrawableFromBitmap(bmp);
                ivCapture.setBackground(d);

                break;
            case REQUEST_IMAGE_CROP:

                Bitmap photo = BitmapFactory.decodeFile(photoURI.getPath());

                ivCapture.setImageBitmap(photo);
                //Bitmap photo = BitmapFactory.decodeFile(mCurrentPhotoPath);
                // photo가져올 때 옵션 지정 가능, 아래는 예
              *//*  BitmapFactory.Options options = new BitmapFactory.Options();
                options.inInputShareable = true;
                options.inDither=false;
                options.inTempStorage=new byte[32 * 1024];
                options.inPurgeable = true;
                options.inJustDecodeBounds = false;*//*


                break;

        }
    }*/

    private void logoutUser() {
        session.setLogin(false);
        mAuth.signOut();
        db.deleteUsers();
        // Launching the login activity

        Intent intent = new Intent(MypageActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    /*private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(); // 사진찍은 후 저장할 임시 파일(껍데기)
            } catch (IOException ex) {
                Toast.makeText(getApplicationContext(), "createImageFile Failed", Toast.LENGTH_LONG).show();
            }

            if (photoFile != null) {
                Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE ); // 동기화
                photoURI = Uri.fromFile(photoFile); // 임시 파일의 위치,경로 가져옴
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI); // 임시 파일 위치에 저장
                mediaScanIntent.setData(photoURI); // 동기화
                this.sendBroadcast(mediaScanIntent); // 동기화
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
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

        cropIntent.setDataAndType(photoURI, "image*//*");
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
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
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
    }*/


}

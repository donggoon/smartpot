package inandout.pliend.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import inandout.pliend.R;

/**
 * Created by skehg on 2017-07-11.
 */

public class AnalyzeFragment extends Fragment {


    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;

    private Uri mImageCaptureUri;
    private ImageView iv_UserPhoto;
    private int id_view;
    private String absoultePath;

    private Context context;

    Button previousBtn;
    Button nowBtn;
    Button analyzeBtn;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analyze, null);

        previousBtn = (Button) view.findViewById(R.id.btn_previous);
        nowBtn = (Button) view.findViewById(R.id.btn_now);
        analyzeBtn = (Button) view.findViewById(R.id.btn_analyze);

        //식물 이미지 넣기
        //디비에 저장해야함
        previousBtn.setOnClickListener(new View.OnClickListener(){
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
        return view;
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
        if(requestCode != getActivity().RESULT_OK)
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
                if(resultCode != getActivity().RESULT_OK){return;}
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

            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));

            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

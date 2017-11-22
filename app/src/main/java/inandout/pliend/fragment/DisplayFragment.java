package inandout.pliend.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import inandout.pliend.R;
import inandout.pliend.app.AppController;

/**
 * Created by DK on 2017-11-20.
 */

public class DisplayFragment extends Fragment {
    private TextView emoticon;
    private TextView name, birth, status;
    public Context mContext;

    View view;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_display, null);

        emoticon = (TextView) view.findViewById(R.id.emoticon);
        name = (TextView) view.findViewById(R.id.textName);
        birth = (TextView) view.findViewById(R.id.textBirth);
        status = (TextView) view.findViewById(R.id.textStatus);
        mContext = getActivity();
        setDisplay(AppController.getInstance().getStatusType());

        return view;
    }

    public void onStart() {
        super.onStart();
    }

    public void onPause() {
        super.onPause();
    }

    /*0. 빈 화면
1. 평상시 이모티콘
2. 애정이 필요할 때 이모티콘
3. 물이 필요할 때 이모티콘
4. 퀘스트 완료 이모티콘
5. 빛이 필요할 때 이모티콘
6. 베터리 부족 이모티콘
7. 온도가 낮을 때 이모티콘*/
    public void setDisplay(int type) {
        switch(type) {
            case 1:
                emoticon.setText("□□□□■■■■■■■■□□□□\n" +
                        "□□□■□□□□□□□□■□□□\n" +
                        "□□■□□□□□□□□□□■□□\n" +
                        "□■□□□■□□□□■□□□■□\n" +
                        "□■□□□■□□□□■□□□■□\n" +
                        "□■■■□□□□□□□□■■■□\n" +
                        "□■■■□■■■■■■□■■■□\n" +
                        "□■□□□■□□□□■□□□■□\n" +
                        "□□■□□□■■■■□□□■□□\n" +
                        "□□□■□□□□□□□□■□□□\n" +
                        "□□□□■■■■■■■■□□□□\n" +
                        "□■□□□□□■■□□□□□■□\n" +
                        "□■■■□□□■■□□□■■■□\n" +
                        "□□■■■□□■■□□■■■□□\n" +
                        "□□□□■■□■■□■■□□□□\n" +
                        "□□□□□□■■■■□□□□□□");

                name.setText(AppController.getInstance().getPlantName());
                birth.setText(AppController.getInstance().getPlantBirth());
                status.setText("아주 좋아요(>_<)");
                break;
            case 3:
                emoticon.setText("□□□□□□□□□□□□□□□□\n" +
                    "□■■■■■■■■■■□□□■□\n" +
                    "■■□□□□□□■□■□■□□□\n" +
                    "■□□□□□□□■□■□□□■□\n" +
                    "□■■■■■■■■■■□■□□□\n" +
                    "□□■□□□■□■□□□□□■□\n" +
                    "□□■□□■□□■□□□□□□□\n" +
                    "□□■□□□■□□■□□□□□□\n" +
                    "□■□□□□□■□□□□□□□□\n" +
                    "■□□□□□□□■□□□□□□□\n" +
                    "■■■■■■■■■■□□□□□□\n" +
                    "■□□□□□□□□□■□□□□□\n" +
                    "■□□□□□□□□□■□□□□□\n" +
                    "■□□□□□□□□□■□□□□□\n" +
                    "■□□□□□□□□□■□□□□□\n" +
                    "□■■■■■■■■■□□□□□□");
                status.setText("물 좀 주세요!(o_o)");
                name.setText(AppController.getInstance().getPlantName());
                birth.setText(AppController.getInstance().getPlantBirth());
                break;

            case 4:
                emoticon.setText("□□□□□■■□□□□■■□□□\n" +
                        "□□□□■□□■□□■□□■□□\n" +
                        "□□□□■□□■□■□□□■□□\n" +
                        "□□■■■□□■■□□□■□□□\n" +
                        "□■□□■□□■□□□■□□□□\n" +
                        "□■□□■□■□□□■□□□□□\n" +
                        "□■□□■■□□■■■■□□□□\n" +
                        "□■□□■□■■□□□□■■□□\n" +
                        "□■□□■■□□□□□□□□■□\n" +
                        "□■□■■□□□■■■□□□■□\n" +
                        "□■■□□□□■□□□■■■□□\n" +
                        "□■□□□□■□□□□□■■□□\n" +
                        "□■□□□□■□□□■■□□■□\n" +
                        "□■□□□□□■■■□□□□■□\n" +
                        "□■□□□□□□□□□□■■□□\n" +
                        "□■□□□□□□□□□■□□□□");
                status.setText("퀘스트가 완료되었습니다!(>_<)");
                name.setText(AppController.getInstance().getPlantName());
                birth.setText(AppController.getInstance().getPlantBirth());
                break;

            case 7:
                emoticon.setText("□□□□□□□□□□□□□□□□\n" +
                        "■□□■□□■□□□■■■□□□\n" +
                        "□■□■□■□□□■□□□■□□\n" +
                        "□□■■■□□□□■■■□■□□\n" +
                        "■■■■■■■□□■□□□■□□\n" +
                        "□□■■■□□□□■■■□■□□\n" +
                        "□■□■□■□□□■□□□■□□\n" +
                        "■□□■□□■□□■■■□■□□\n" +
                        "□□□□□□□□□■□□□■□□\n" +
                        "□□□□□□□□■□■■■□■□\n" +
                        "□□□□□□□■□■■■■■□■\n" +
                        "□□□□□□□■□■■■■■□■\n" +
                        "□□□□□□□■□■■■■■□■\n" +
                        "□□□□□□□■□■■■■■□■\n" +
                        "□□□□□□□□■□□□□□■□\n" +
                        "□□□□□□□□□■■■■■□□");
                status.setText("따뜻한 곳으로 옮겨주세요!(e_e)");
                name.setText(AppController.getInstance().getPlantName());
                birth.setText(AppController.getInstance().getPlantBirth());
                break;

            case 5:
                emoticon.setText("□□□□□□□■■□□□□□□□\n" +
                        "□□□□□□□■■□□□□□□□\n" +
                        "□□□□□□■■■■□□□□□□\n" +
                        "□□□□□■■□□■■□□□□□\n" +
                        "□□□■■□□□□□□■■□□□\n" +
                        "□□■□□□□□□□□□□■□□\n" +
                        "□■■■■■■■■■■■■■■□\n" +
                        "□□□□□■■■■■■□□□□□\n" +
                        "□□□□□□■■■■□□□□□□\n" +
                        "□□□■□□□□□□□□■□□□\n" +
                        "□□■■□□■□□■□□■■□□\n" +
                        "□■■□□□■□□■□□□■■□\n" +
                        "□■□□□■■□□■■□□□■□\n" +
                        "□□□□□■□□□□■□□□□□\n" +
                        "□□□□□■□□□□■□□□□□\n" +
                        "□□□□□□□□□□□□□□□□");
                status.setText("밝은 곳으로 옮겨주세요!(u_u)");
                name.setText(AppController.getInstance().getPlantName());
                birth.setText(AppController.getInstance().getPlantBirth());
                break;
        }
    }
}

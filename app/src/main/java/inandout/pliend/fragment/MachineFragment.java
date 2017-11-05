package inandout.pliend.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import inandout.pliend.R;
import inandout.pliend.activity.AddBluetoothActivity;
import inandout.pliend.activity.ModifyPlantActivity;
import inandout.pliend.app.AppController;


public class MachineFragment extends Fragment {
    // FloatingActionButton addBluetoothBtn;
    FloatingActionButton addWifiBtn;

    ImageButton serialImage;
    TextView serialText;
    ImageButton bluetoothImage;
    TextView bluetoothText;
    TextView disconnectText;

    private String serial;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_machine, null);
        // plantImg = (ImageView)view.findViewById(R.id.main_leaf);

        // addBluetoothBtn = (FloatingActionButton) view.findViewById(R.id.btn_bluetooth);
        addWifiBtn = (FloatingActionButton) view.findViewById(R.id.btn_wifi);
        serialImage = (ImageButton) view.findViewById(R.id.imageSerial);
        serialText = (TextView) view.findViewById(R.id.textSerial);
        bluetoothImage = (ImageButton) view.findViewById(R.id.imageBluetooth);
        bluetoothText = (TextView) view.findViewById(R.id.textBluetooth);
        disconnectText = (TextView) view.findViewById(R.id.textDisconnect);

        addWifiBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try {
                    Intent intent = new Intent(getActivity(), AddBluetoothActivity.class);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        serialImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final EditText editText = new EditText(getActivity());
                editText.setLines(1);
                editText.setMaxLines(1);

                try {
                    DialogInterface.OnClickListener commitListener = new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            serial = editText.getText().toString();
                        }
                    };
                    DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    };
                    new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
                            .setTitle("기기 메뉴얼의 시리얼 입력")
                            .setView(editText, 100, 50, 100, 0)
                            .setPositiveButton("확인", commitListener)
                            .setNegativeButton("취소", cancelListener)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        serialText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final EditText editText = new EditText(getActivity());
                editText.setLines(1);
                editText.setMaxLines(1);

                try {
                    DialogInterface.OnClickListener commitListener = new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            serial = editText.getText().toString();
                        }
                    };
                    DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    };
                    new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
                            .setTitle("기기 메뉴얼의 시리얼 입력")
                            .setView(editText, 100, 50, 100, 0)
                            .setPositiveButton("확인", commitListener)
                            .setNegativeButton("취소", cancelListener)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if(AppController.getInstance().getIsBluetooth()) {
            disconnectText.setVisibility(View.INVISIBLE);
            bluetoothText.setText("블루투스 페어링 됨 (" + AppController.getInstance().getMachineName() + ")");
            bluetoothImage.setImageResource(R.mipmap.ic_bluetooth_connected);

            bluetoothImage.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    try {
                        Intent intent = new Intent(getActivity(), AddBluetoothActivity.class);
                        startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            bluetoothText.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    try {
                        Intent intent = new Intent(getActivity(), AddBluetoothActivity.class);
                        startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            disconnectText.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    try {
                        AddBluetoothActivity addBluetoothActivity = new AddBluetoothActivity();
                        addBluetoothActivity.disconnectDevice();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }

        else {
            disconnectText.setVisibility(View.INVISIBLE);
            bluetoothText.setText("블루투스 페어링되지 않음");
            bluetoothImage.setImageResource(R.mipmap.ic_bluetooth_disabled);

            bluetoothImage.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    try {
                        Intent intent = new Intent(getActivity(), AddBluetoothActivity.class);
                        startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            bluetoothText.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    try {
                        Intent intent = new Intent(getActivity(), AddBluetoothActivity.class);
                        startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
        return view;
    }
}
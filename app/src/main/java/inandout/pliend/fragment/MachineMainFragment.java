package inandout.pliend.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import inandout.pliend.R;
import inandout.pliend.activity.AddMachineActivity;
import inandout.pliend.app.AppController;
import inandout.pliend.helper.SQLiteHandler;
import inandout.pliend.store.AdapterMachine;
import inandout.pliend.store.DataMachine;


public class MachineMainFragment extends Fragment {
    Intent intent;
    private RecyclerView mRVPlant;
    private AdapterMachine mAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_recycler, null);

        // plantImg = (ImageView)view.findViewById(R.id.main_leaf);

        ImageButton addMachineBtn = (ImageButton)view.findViewById(R.id.btn_add);
        addMachineBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try {
                    Intent intent = new Intent(getActivity(), AddMachineActivity.class);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        if(!AppController.getInstance().getIsBluetooth()) {
            DataMachine noData = new DataMachine();
            noData.machineName = "기기를 연결해주세요";
            List<DataMachine> data = new ArrayList<>();
            data.add(noData);

            mRVPlant = (RecyclerView) view.findViewById(R.id.list);
            mAdapter = new AdapterMachine(getActivity(), data);
            mRVPlant.setAdapter(mAdapter);
            mRVPlant.setLayoutManager(new LinearLayoutManager(getActivity()));
            /*view = inflater.inflate(R.layout.fragment_no_plant_main, null);

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
            });*/
        }
        else {
            DataMachine noData = new DataMachine();
            noData.machineName = AppController.getInstance().getMachineName();
            List<DataMachine> data = new ArrayList<>();
            data.add(noData);

            mRVPlant = (RecyclerView) view.findViewById(R.id.list);
            mAdapter = new AdapterMachine(getActivity(), data);
            mRVPlant.setAdapter(mAdapter);
            mRVPlant.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        return view;
        /*if(!AppController.getInstance().getIsBluetooth()) {
            view = inflater.inflate(R.layout.fragment_no_machine_main, null);

            ImageButton addPlantImageBtn = (ImageButton)view.findViewById(R.id.btn_add_machine_image);
            Button addPlantTextBtn = (Button)view.findViewById(R.id.btn_add_machine_text);

            addPlantImageBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    try {
                        intent = new Intent(getActivity(), AddMachineActivity.class);
                        startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            addPlantTextBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    intent = new Intent(getActivity(), AddMachineActivity.class);
                    startActivity(intent);
                }
            });
        }
        else {
        }*/
    }
}
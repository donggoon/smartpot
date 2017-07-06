package inandout.pliend.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import inandout.pliend.R;
import inandout.pliend.app.AppController;


public class MachineMainFragment extends Fragment {
    Intent intent;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_machine_main, null);

        if(!AppController.getInstance().getIsPlantRegister()) {
            view = inflater.inflate(R.layout.fragment_no_machine_main, null);
        }

        return view;
    }
}
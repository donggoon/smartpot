package inandout.pliend.store;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import inandout.pliend.R;
import inandout.pliend.activity.MainActivity;
import inandout.pliend.app.AppController;
import inandout.pliend.helper.SQLiteHandler;

/**
 * Created by SJ on 2016-11-07.
 */
public class AdapterMachine extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<DataMachine> data= Collections.emptyList();

    private SQLiteHandler db;

    String query;
    String id;
    String key;

    // create constructor to initialize context and data sent from NameSearchActivity
    public AdapterMachine(Context context, List<DataMachine> data){
        this.context = context;

        db = new SQLiteHandler(context);

        HashMap<String, String> user = db.getUserDetails();
        id = user.get("id");

        inflater= LayoutInflater.from(context);
        this.data=data;
    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.container_machine, parent,false);
        MyHolder holder = new MyHolder(view);

        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get current position of item in RecyclerView to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        DataMachine current = data.get(position);
        myHolder.textName.setText(current.machineName);
    }
    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textName;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.textName);
            itemView.setOnClickListener(this);
        }

        // Click event for all items
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            context.startActivity(intent);
        }
    }
}
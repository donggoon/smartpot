package inandout.pliend.store;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class AdapterStore extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private Context context;
    private LayoutInflater inflater;
    List<DataPlant> data= Collections.emptyList();
    AppController appController;

    private SQLiteHandler db;

    String query;
    String id;
    String key;

    // create constructor to initialize context and data sent from NameSearchActivity
    public AdapterStore(Context context, List<DataPlant> data, Context getAppContext){
        this.context=context;

        appController = (AppController)getAppContext;

        db = new SQLiteHandler(getAppContext);

        HashMap<String, String> user = db.getUserDetails();
        id = user.get("id");

        inflater= LayoutInflater.from(context);
        this.data=data;
    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.container_store, parent,false);
        MyHolder holder=new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get current position of item in RecyclerView to bind data and assign values from list
        MyHolder myHolder= (MyHolder) holder;
        DataPlant current=data.get(position);
        myHolder.textName.setText(current.plantName);
        myHolder.textBirth.setText(current.plantBirth);
        myHolder.textType.setText(current.plantType);
    }
    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textName;
        TextView textBirth;
        TextView textType;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.textName);
            textBirth = (TextView) itemView.findViewById(R.id.textBirth);
            textType = (TextView) itemView.findViewById(R.id.textType);
            itemView.setOnClickListener(this);
        }

        // Click event for all items
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            context.startActivity(intent);
        }
    }
}
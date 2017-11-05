package inandout.pliend.store;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import inandout.pliend.R;
import inandout.pliend.activity.MainActivity;
import inandout.pliend.app.AppController;
import inandout.pliend.helper.SQLiteHandler;

/**
 * Created by SJ on 2016-11-07.
 */
public class AdapterQuest extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private Context context;
    private LayoutInflater inflater;
    List<DataQuest> data= Collections.emptyList();
    AppController appController;

    private SQLiteHandler db;

    String query;
    String id;
    String key;

    // create constructor to initialize context and data sent from NameSearchActivity
    public AdapterQuest(Context context, List<DataQuest> data) {
        this.context = context;

        db = new SQLiteHandler(context);

        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.container_quest, parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get current position of item in RecyclerView to bind data and assign values from list
        MyHolder myHolder= (MyHolder) holder;
        DataQuest current = data.get(position);
        if(current.questType == 1) myHolder.imageView.setImageResource(R.drawable.isthirsty);
        else if(current.questType == 2) myHolder.imageView.setImageResource(R.drawable.iscold);
        else if(current.questType == 3) myHolder.imageView.setImageResource(R.drawable.isdark);
        myHolder.textContent.setText(current.questContent);
        myHolder.textComplete.setText("퀘스트 완료시 친밀도 5상승!");
        myHolder.textTime.setText(current.questDate);
    }
    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        TextView textContent;
        TextView textComplete;
        TextView textTime;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageQuest);
            textContent = (TextView) itemView.findViewById(R.id.textContent);
            textComplete = (TextView) itemView.findViewById(R.id.textComplete);
            textTime = (TextView) itemView.findViewById(R.id.textTime);
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
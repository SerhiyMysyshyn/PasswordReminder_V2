package com.example.ethernetapp.mainList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ethernetapp.MainActivity;
import com.example.ethernetapp.Program;
import com.example.ethernetapp.R;


public class DataManager extends RecyclerView.Adapter<DataManager.RecyclerViewHolder>{

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView mImage;
        TextView mName, mTime, mDate;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.item_img);
            mName = (TextView) itemView.findViewById(R.id.item_name);
            mTime = (TextView) itemView.findViewById(R.id.item_time);
            mDate = (TextView) itemView.findViewById(R.id.item_date);
        }
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.program_item, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        // get the single element from the main array
        final Program program = MainActivity.ProgramData[position];
        // Set the values
        holder.mImage.setBackgroundResource(Integer.parseInt(program.get(Program.Field.IMG)));
        holder.mName.setText(program.get(Program.Field.NAME));
        holder.mTime.setText(program.get(Program.Field.TIME));
        holder.mDate.setText(program.get(Program.Field.DATE));
    }

    @Override
    public int getItemCount() {
        return MainActivity.ProgramData.length;
    }
}

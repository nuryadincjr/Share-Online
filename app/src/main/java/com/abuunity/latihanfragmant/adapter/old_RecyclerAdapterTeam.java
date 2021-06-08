package com.abuunity.latihanfragmant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abuunity.latihanfragmant.R;
import com.abuunity.latihanfragmant.pojo.old_Team;
import com.abuunity.latihanfragmant.Interface.ItemClickListener;

import java.util.ArrayList;

public class old_RecyclerAdapterTeam extends RecyclerView.Adapter<old_RecyclerAdapterTeam.ViewHolder> {
    private ArrayList<old_Team> oldTeamArrayList;
    private Context context;
    private ItemClickListener itemClickListener;

    public old_RecyclerAdapterTeam(Context context, ArrayList<old_Team> oldTeamArrayList) {
        this.oldTeamArrayList = oldTeamArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.old_list_team,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        int no=i+1;
        viewHolder.txtNo.setText(String.valueOf(no));
        viewHolder.txtTeam.setText(oldTeamArrayList.get(i).getNama());
        viewHolder.imgLogo.setImageResource(Integer.parseInt(oldTeamArrayList.get(i).getLogo()));
    }

    @Override
    public int getItemCount() {
        return oldTeamArrayList !=null ? oldTeamArrayList.size():0;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        TextView txtTeam, txtNo;
        ImageView imgLogo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNo = itemView.findViewById(R.id.text_no);
            txtTeam = itemView.findViewById(R.id.text_team);
            imgLogo = itemView.findViewById(R.id.img_logo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(itemClickListener != null) {
                itemClickListener.onClick(v,getAdapterPosition());
            }
        }
    }
}

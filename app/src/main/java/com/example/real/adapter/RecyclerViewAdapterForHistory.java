package com.example.real.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.real.R;

import java.util.List;
import java.util.zip.Inflater;

public class RecyclerViewAdapterForHistory extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> data;
    private Context context;

    public RecyclerViewAdapterForHistory(List<String> data, Context context) {
        this.data = data;
        this.context = context;
        // data looks like
        // [ "Content/MfnsDaivaiyedaOpTxdp/Comments/20220110163124295" , ~~~ ]
    }

    public void AddItem(List<String> tempstring){
        data.addAll(tempstring);
    }
    @Override
    public int getItemViewType(int position) {
        String datum = data.get(position);
        String[] DatumSplit = datum.split("#");
        String Address = DatumSplit[1];
        if (DatumSplit[0].equals("Content")){
            // CONTENT
            return 0;
        } else if (DatumSplit[0].equals("AuctionContent")){
            // AUCTIONCONTENT
            return 1;
        } else if (DatumSplit[0].equals("Comment")){
            // COMMENT
            return 2;
        } else{
            return 3;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(viewType){
            case 0:
                return new ContentViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.logitem_for_content, parent, false));
            case 1:
                //return
            case 2:
                //return
            case 3:
                //return
            default:
                return null;
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String datum = data.get(position);
        System.out.println("**********************************************");
        switch (getItemViewType(position)){
            case 0:
                ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
                String address = datum.split("#")[1];
                contentViewHolder.asdfasdf.setText(address);
            case 1:
            case 2:
            case 3:

        }

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ContentViewHolder extends RecyclerView.ViewHolder{

        TextView asdfasdf;


        public ContentViewHolder(View itemView) {
            super(itemView);

            //asdfasdf = itemView.findViewById(R.id.logitemforcontentaddress);
        }
    }

    class AuctionContentViewHolder extends RecyclerView.ViewHolder{

        TextView asdfasdf;
        ImageView asdfsfddsfds;

        public AuctionContentViewHolder(View itemView) {
            super(itemView);

        }
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{

        TextView asdfasdf;


        public CommentViewHolder(View itemView) {
            super(itemView);

            //asdfasdf = itemView.findViewById(R.id.logitemforcontentaddress);
        }
    }
}

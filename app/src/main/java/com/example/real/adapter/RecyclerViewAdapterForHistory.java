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

import org.w3c.dom.Text;

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

    public void AddItem(List<String> tempString){
        // todo: error check
        data.addAll(tempString);
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
            // error
            return 3;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(viewType){
            case 0: return new ContentViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.logitem_for_content, parent, false));
            case 1: return new AuctionContentViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.logitem_for_auctioncontent, parent, false));
            case 2: return new CommentViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.logitem_for_comment, parent, false));
            default: return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String datum = data.get(position);
        String[] datumSplit = datum.split("#");
        String address = datumSplit[1];

        switch (getItemViewType(position)){
            case 0: // type - Content
                ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
                /* todo: 아래 내용을 채워주면 됨, 아마 address를 통해서.
                contentViewHolder.logitemforcontent_ContentImg;
                contentViewHolder.logitemforcontent_ContentTitle;
                contentViewHolder.logitemforcontent_ContentDescription;
                contentViewHolder.logitemforcontent_ContentTime;

                 */

            case 1: // type - AuctionContent
                AuctionContentViewHolder auctionContentViewHolder = (AuctionContentViewHolder) holder;
                /*
                auctionContentViewHolder.logitemforauctioncontent_AuctionContentImg;
                auctionContentViewHolder.logitemforauctioncontent_AuctionContentTitle;
                auctionContentViewHolder.logitemforauctioncontent_AuctionContentDescription;
                auctionContentViewHolder.logitemforauctioncontent_AuctionContentTime;

                 */

            case 2: // type - Comment
                CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
                /*
                commentViewHolder.logitemforcomment_To;
                commentViewHolder.logitemforcomment_Mention;
                commentViewHolder.logitemforcomment_CommentTime;

                 */
            default:
                // todo: error handling

        }

    }

    @Override
    public int getItemCount() {
        // todo: 이 부분이 " return 0; " 으로 되어있더라고 그래서 우리가 돌려봤을 때 아무것도 안나온 것 같아.
        return data.size();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder{

        ImageView logitemforcontent_ContentImg;
        TextView logitemforcontent_ContentTitle;
        TextView logitemforcontent_ContentDescription;
        TextView logitemforcontent_ContentTime;

        public ContentViewHolder(View itemView) {
            super(itemView);

            logitemforcontent_ContentImg = itemView.findViewById(R.id.logitemforcontent_ContentImg);
            logitemforcontent_ContentTitle = itemView.findViewById(R.id.logitemforcontent_ContentTitle);
            logitemforcontent_ContentDescription = itemView.findViewById(R.id.logitemforcontent_ContentDescription);
            logitemforcontent_ContentTime = itemView.findViewById(R.id.logitemforcontent_ContentTime);
        }
    }

    class AuctionContentViewHolder extends RecyclerView.ViewHolder{

        ImageView logitemforauctioncontent_AuctionContentImg;
        TextView logitemforauctioncontent_AuctionContentTitle;
        TextView logitemforauctioncontent_AuctionContentDescription;
        TextView logitemforauctioncontent_AuctionContentTime;

        public AuctionContentViewHolder(View itemView) {
            super(itemView);

            logitemforauctioncontent_AuctionContentImg = itemView.findViewById(R.id.logitemforauctioncontent_AuctionContentImg);
            logitemforauctioncontent_AuctionContentTitle = itemView.findViewById(R.id.logitemforauctioncontent_AuctionContentTitle);
            logitemforauctioncontent_AuctionContentDescription = itemView.findViewById(R.id.logitemforauctioncontent_AuctionContentDescription);
            logitemforauctioncontent_AuctionContentTime = itemView.findViewById(R.id.logitemforauctioncontent_AuctionContentTime);
        }
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{

        TextView logitemforcomment_To;
        TextView logitemforcomment_Mention;
        TextView logitemforcomment_CommentTime;

        public CommentViewHolder(View itemView) {
            super(itemView);

            logitemforcomment_To = itemView.findViewById(R.id.logitemforcomment_To);
            logitemforcomment_Mention = itemView.findViewById(R.id.logitemforcomment_Mention);
            logitemforcomment_CommentTime = itemView.findViewById(R.id.logitemforcomment_CommentTime);
        }
    }
}

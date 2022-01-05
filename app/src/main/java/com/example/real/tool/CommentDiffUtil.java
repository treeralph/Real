package com.example.real.tool;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.example.real.adapter.ExpandableListAdapter;

import java.util.List;

public class CommentDiffUtil extends DiffUtil.Callback {

    private final List<ExpandableListAdapter.Item> olddata;
    private final List<ExpandableListAdapter.Item> newdata;

    public CommentDiffUtil(List<ExpandableListAdapter.Item> olddata,List<ExpandableListAdapter.Item> newdata){
        this.olddata = olddata;
        this.newdata = newdata;
    }


    @Override
    public int getOldListSize() {
        return olddata.size();
    }

    @Override
    public int getNewListSize() {
        return newdata.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return olddata.get(oldItemPosition).time.equals(newdata.get(newItemPosition).time);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return olddata.get(oldItemPosition).mention.equals(newdata.get(newItemPosition).mention);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItenPosition){
        // Implement method if ur going to use ItemAnimator
        return super.getChangePayload(oldItemPosition,newItenPosition);
    }
}

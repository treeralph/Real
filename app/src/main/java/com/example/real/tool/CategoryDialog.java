package com.example.real.tool;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.cardview.widget.CardView;

import com.example.real.Callback;
import com.example.real.R;

import java.util.ArrayList;

public class CategoryDialog extends Dialog {

    Callback callback;
    String[] categoryList = SearchTool.category;

    public CategoryDialog(Context context, Callback callback){
        super(context);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_category);

        ArrayList<CardView> cardViewList = new ArrayList<>();
        cardViewList.add(findViewById(R.id.customDialogCategory_0));
        cardViewList.add(findViewById(R.id.customDialogCategory_1));
        cardViewList.add(findViewById(R.id.customDialogCategory_2));
        cardViewList.add(findViewById(R.id.customDialogCategory_3));
        cardViewList.add(findViewById(R.id.customDialogCategory_4));
        cardViewList.add(findViewById(R.id.customDialogCategory_5));
        cardViewList.add(findViewById(R.id.customDialogCategory_6));
        cardViewList.add(findViewById(R.id.customDialogCategory_7));
        cardViewList.add(findViewById(R.id.customDialogCategory_8));
        cardViewList.add(findViewById(R.id.customDialogCategory_9));
        cardViewList.add(findViewById(R.id.customDialogCategory_10));
        cardViewList.add(findViewById(R.id.customDialogCategory_11));
        cardViewList.add(findViewById(R.id.customDialogCategory_12));
        cardViewList.add(findViewById(R.id.customDialogCategory_13));
        cardViewList.add(findViewById(R.id.customDialogCategory_14));
        cardViewList.add(findViewById(R.id.customDialogCategory_15));
        cardViewList.add(findViewById(R.id.customDialogCategory_16));
        cardViewList.add(findViewById(R.id.customDialogCategory_17));

        for(int i=0; i<cardViewList.size(); i++){
            String category = categoryList[i];
            cardViewList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.OnCallback(category);
                    dismiss();
                }
            });
        }
    }
}

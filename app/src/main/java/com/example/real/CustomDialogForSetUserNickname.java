package com.example.real;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

public class CustomDialogForSetUserNickname extends Dialog implements View.OnClickListener {

    private Button positiveButton;
    private Button negativeButton;
    private EditText setusernicknameedt;
    private Context context;
    private CustomDialogListener customDialogListener;

    public CustomDialogForSetUserNickname(Context context) {
        super(context);
        this.context = context;
    }
    interface CustomDialogListener{
        void onPositiveClicked(String Nickname);
        void onNegativeClicked(); }
    public void setDialogListener(CustomDialogListener customDialogListener){
        this.customDialogListener = customDialogListener;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_setusernickname);
        //init
        positiveButton = (Button)findViewById(R.id.dialogpositivebutton);
        negativeButton = (Button)findViewById(R.id.dialognegativebutton);
        setusernicknameedt = (EditText)findViewById(R.id.dialogsetuserprofilenickname);

        //버튼 클릭 리스너 등록
        positiveButton.setOnClickListener(this);
        negativeButton.setOnClickListener(this); }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialogpositivebutton://확인 버튼을 눌렀을 때

                String Nickname = setusernicknameedt.getText().toString();

                //인터페이스의 함수를 호출하여 변수에 저장된 값들을 Activity로 전달
                customDialogListener.onPositiveClicked(Nickname);
                dismiss();
                break;
            case R.id.dialognegativebutton: //취소 버튼을 눌렀을 때
                cancel();
                break;
        }
    }
}

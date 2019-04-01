package com.kylone.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kylone.player.R;
import com.kylone.utils.ApiUtils;
import com.kylone.utils.LogUtil;

public class PasscodeDialog extends Dialog implements View.OnClickListener {
    private EditText dialog_et;
    private TextView tvInfo;
    private OnEditableListener mOnEditableListener;

    public PasscodeDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_passcode);
//        sp = getContext().getSharedPreferences("kylone", Context.MODE_PRIVATE);
        setCanceledOnTouchOutside(false);
        dialog_et = (EditText) findViewById(R.id.dialog_pc_et);
        tvInfo = (TextView) findViewById(R.id.dialog_pc_info);


        TextView tvImput1 = (TextView) findViewById(R.id.dialog_pc_1);
        TextView tvImput2 = (TextView) findViewById(R.id.dialog_pc_2);
        TextView tvImput3 = (TextView) findViewById(R.id.dialog_pc_3);
        TextView tvImput4 = (TextView) findViewById(R.id.dialog_pc_4);
        TextView tvImput5 = (TextView) findViewById(R.id.dialog_pc_5);
        TextView tvImput6 = (TextView) findViewById(R.id.dialog_pc_6);
        TextView tvImput7 = (TextView) findViewById(R.id.dialog_pc_7);
        TextView tvImput8 = (TextView) findViewById(R.id.dialog_pc_8);
        TextView tvImput9 = (TextView) findViewById(R.id.dialog_pc_9);
        TextView tvImput0 = (TextView) findViewById(R.id.dialog_0);
        TextView tvImputB = (TextView) findViewById(R.id.dialog_black);


        tvImput1.setOnClickListener(this);
        tvImput2.setOnClickListener(this);
        tvImput3.setOnClickListener(this);
        tvImput4.setOnClickListener(this);
        tvImput5.setOnClickListener(this);
        tvImput6.setOnClickListener(this);
        tvImput7.setOnClickListener(this);
        tvImput8.setOnClickListener(this);
        tvImput9.setOnClickListener(this);
        tvImput0.setOnClickListener(this);
        tvImputB.setOnClickListener(this);

    }

    @Override
    public void show() {
        super.show();
    }

    public void show(String info) {
        if (!isShowing()) {
            super.show();
        }
        tvInfo.setText(info);
        cleanCode();
    }

    public void cleanCode(){
        dialog_et.getEditableText().clear();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Editable et = dialog_et.getEditableText();
        if (v.getId() == R.id.dialog_black) {
            if (et.length() > 0) {
                et.delete(et.length() - 1, et.length());
            }
        } else if (et.length() < 5) {
            CharSequence a = ((TextView) v).getText();
            et.append(a);
        }
        if (et.length() == 5) {
            LogUtil.i(" input code ->  " + dialog_et.getText());
            if (mOnEditableListener != null) {
                boolean isDismiss = mOnEditableListener.onEditableCompletion(dialog_et.getText().toString());
                if (isDismiss) {
                    dismiss();
                }
            }
        }
    }

    public void setOnEditableListener(OnEditableListener editableListener) {
        this.mOnEditableListener = editableListener;
    }

    public interface OnEditableListener {
        boolean onEditableCompletion(String edit);
    }
}

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

public class HostDialog extends Dialog {
    private EditText dialog_et;
    private TextView tvEnter;
    private TextView tvDefault;
    private SharedPreferences sp;

    public HostDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_host);
        sp = getContext().getSharedPreferences("kylone", Context.MODE_PRIVATE);

        dialog_et = (EditText) findViewById(R.id.dialog_et);
        tvEnter = (TextView) findViewById(R.id.dialog_enter);
        tvDefault = (TextView) findViewById(R.id.dialog_default);

        String host = sp.getString("ip", "");
        dialog_et.setText(host);

        tvEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tv = dialog_et.getText().toString();
                if (TextUtils.isEmpty(tv)){
                    Toast.makeText(getContext(), "The input cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                ApiUtils.setIp(tv);
                sp.edit().putString("ip",tv).apply();
                dismiss();
            }
        });

        tvDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiUtils.setIp("10.47.48.1");
                dismiss();
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}

package com.mardawang.android.mylocationdemo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mardawang.android.mylocationdemo.R;

/**
 * Created by mardawang on 2017/7/6.
 */

public class SignedActivity extends AppCompatActivity {
    public static String CUR_TIME = "current_time";
    public static String CUR_LOCAT = "current_location";

    private TextView tv_sign_time;
    private TextView tv_sign_location;
    private TextView tv_commit;
    private EditText et_extrainfo;
    private TextView tv_back;
    private TextView tv_title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed);
        initView();
    }

    private void initView() {
        tv_sign_time = (TextView) findViewById(R.id.tv_sign_time);
        tv_sign_location = (TextView) findViewById(R.id.tv_sign_location);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_commit = (TextView) findViewById(R.id.tv_commit);
        et_extrainfo = (EditText) findViewById(R.id.et_extrainfo);

        if (null != getIntent()) {
            String curTime = getIntent().getStringExtra(CUR_TIME);
            String curLocat = getIntent().getStringExtra(CUR_LOCAT);
            tv_sign_location.setText(curLocat);
            tv_sign_time.setText(curTime);
        }
        tv_title.setText("签到");
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignedActivity.this, "提交", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

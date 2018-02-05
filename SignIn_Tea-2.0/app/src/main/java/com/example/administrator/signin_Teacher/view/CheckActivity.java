package com.example.administrator.signin_Teacher.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.administrator.signin_Teacher.R;

/**
 * Created by Administrator on 2018/1/1.
 */

public class CheckActivity extends AppCompatActivity {
    private Button record,not;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        this.record = (Button) findViewById(R.id.record);
        this.not = (Button) findViewById(R.id.not);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CheckActivity.this,RecordActivity.class));
            }
        });
        not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CheckActivity.this,NotArriveActivity.class));
            }
        });
    }


}

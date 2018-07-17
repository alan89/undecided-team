package com.test.firemomo.firemomo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.test.firemomo.firemomo.MomoGen.MomoCam;

public class MainActivity extends AppCompatActivity {
    private Button goFast;
    private Intent goCam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        goFast= findViewById(R.id.go_fast);
        goFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goCam=new Intent(MainActivity.this, MomoCam.class);
                startActivity(goCam);
            }
        });

    }
}

package com.test.firemomo.firemomo.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.test.firemomo.firemomo.R;

public class ChooserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);

        findViewById(R.id.button_launch_google_flow)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ChooserActivity.this, GoogleSignInActivity.class));
                    }
                });

        findViewById(R.id.button_launch_facebook_flow)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ChooserActivity.this, FacebookLoginActivity.class));
                    }
                });
    }
}

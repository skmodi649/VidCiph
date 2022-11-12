package com.example.vidciph;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ShareActivity extends AppCompatActivity {
    Button shareAbhishek, shareSuraj, shareAdam, shareManoj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        shareAbhishek = findViewById(R.id.shareAbhishek);
        shareSuraj = findViewById(R.id.shareSuraj);
        shareManoj = findViewById(R.id.shareManoj);

        shareManoj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                if (intent != null) {
                    startActivity(intent);
                }

            }
        });

    }
}
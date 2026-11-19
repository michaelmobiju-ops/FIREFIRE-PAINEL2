package com.firefire.painel;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.provider.Settings;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final int GOLD = 0xFFFFD700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(40, 40, 40, 40);
        layout.setBackgroundColor(Color.BLACK);

        TextView title = new TextView(this);
        title.setText("FREEFIRE PAINEL");
        title.setTextColor(GOLD);
        title.setTextSize(26);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        Button start = new Button(this);
        start.setText("ATIVAR PAINEL");
        start.setTextColor(Color.BLACK);
        start.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(GOLD)
        );

        start.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())
                );
                startActivity(intent);
            } else {
                Intent serviceIntent = new Intent(
                    this, FloatingService.class
                );

                if (Build.VERSION.SDK_INT >= 26) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }
            }
        });

        layout.addView(title);
        layout.addView(start);
        setContentView(layout);
    }
}

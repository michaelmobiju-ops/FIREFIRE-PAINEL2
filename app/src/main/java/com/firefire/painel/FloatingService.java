
package com.firefire.painel;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class FloatingService extends Service {

    private WindowManager manager;
    private WindowManager.LayoutParams params;
    private LinearLayout panel;
    private SharedPreferences prefs;

    private final int GOLD = 0xFFFFD700;

    @Override
    public void onCreate() {
        super.onCreate();

        if (!Settings.canDrawOverlays(this)) {
            stopSelf();
            return;
        }

        prefs = getSharedPreferences("painel", MODE_PRIVATE);
        manager = (WindowManager)
                getSystemService(WINDOW_SERVICE);

        params = new WindowManager.LayoutParams(
                -2, -2,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 40;
        params.y = 150;

        createPanel();
        manager.addView(panel, params);
    }

    private void createPanel() {
        panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(25, 20, 25, 20);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.BLACK);
        bg.setCornerRadius(25);
        bg.setStroke(3, GOLD);
        panel.setBackground(bg);

        TextView title = new TextView(this);
        title.setText("☰ FREEFIRE PAINEL");
        title.setTextColor(GOLD);
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setPadding(0, 0, 0, 15);
        panel.addView(title);

        enableDrag(title);

        addSlider("Sensibilidade", 200, 100);
        addSlider("FOV (treinamento)", 360, 90);
        addSlider("Suavização", 100, 50);

        Button close = new Button(this);
        close.setText("FECHAR PAINEL");
        close.setTextColor(Color.BLACK);
        close.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(GOLD)
        );

        close.setOnClickListener(v -> stopSelf());
        panel.addView(close);
    }

    private void addSlider(String name, int max, int initial) {
        TextView label = new TextView(this);
        label.setTextColor(GOLD);
        label.setTextSize(14);

        int saved = prefs.getInt(name, initial);
        saved = Math.max(0, Math.min(max, saved));

        label.setText(name + ": " + saved);
        panel.addView(label);

        SeekBar slider = new SeekBar(this);
        slider.setMax(max);
        slider.setProgress(saved);
        panel.addView(slider);

        slider.setOnSeekBarChangeListener(
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(
                        SeekBar bar, int value, boolean user) {
                    label.setText(name + ": " + value);
                    if (user) {
                        prefs.edit().putInt(name, value).apply();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar bar) {}

                @Override
                public void onStopTrackingTouch(SeekBar bar) {}
            }
        );
    }

    private void enableDrag(View handle) {
        handle.setOnTouchListener(new View.OnTouchListener() {
            int startX, startY;
            float touchX, touchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = params.x;
                        startY = params.y;
                        touchX = event.getRawX();
                        touchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = startX +
                            (int)(event.getRawX() - touchX);
                        params.y = startY +
                            (int)(event.getRawY() - touchY);
                        manager.updateViewLayout(panel, params);
                        return true;

                    case MotionEvent.ACTION_UP:
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        if (manager != null && panel != null) {
            manager.removeView(panel);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

package ru.issergeev.parking;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    private Sleep sleep;

    private Animation translateImage, translateText;
    private ImageView logo;
    private TextView text;

    @Override
    protected void onDestroy() {
        sleep.cancel(true);
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        translateImage = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.translate_image);
        translateImage.setFillAfter(true);
        translateText = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.translate_text);
        translateText.setFillAfter(true);

        logo = findViewById(R.id.logo);
        text = findViewById(R.id.text);

        sleep = new Sleep();
        sleep.execute();
    }

    private class Sleep extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!isCancelled()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logo.startAnimation(translateImage);
                        text.startAnimation(translateText);
                    }
                });

                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            }
            return null;
        }
    }
}
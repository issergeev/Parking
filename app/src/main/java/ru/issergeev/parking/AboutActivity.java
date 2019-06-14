package ru.issergeev.parking;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    TextView version, yandexTerms, picturesTerms, maskTerms, licenceTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String versionNum = pInfo.versionName;

        version = findViewById(R.id.version);
        version.append(versionNum);
        yandexTerms = findViewById(R.id.yandexTerms);
        yandexTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent yandexIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://yandex.ru/legal/maps_termsofuse"));
                startActivity(yandexIntent);
            }
        });
        picturesTerms = findViewById(R.id.picturesTerms);
        picturesTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent picturesIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.flaticon.com"));
                startActivity(picturesIntent);
            }
        });
        maskTerms = findViewById(R.id.maskTerms);
        maskTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent maskIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/egslava/edittext-mask/blob/master/LICENSE"));
                startActivity(maskIntent);
            }
        });
        licenceTerms = findViewById(R.id.maskLicense);
        licenceTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent maskIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/issergeev/Parking/wiki/Privacy-Policy"));
                startActivity(maskIntent);
            }
        });
    }
}

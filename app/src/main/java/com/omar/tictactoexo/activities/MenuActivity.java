package com.omar.tictactoexo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.omar.tictactoexo.R;

public class MenuActivity extends AppCompatActivity {
    Button play_cpu_bu;
    Button play_multi_bu;
    Button play_online_bu;
    //Firebase
    //private FirebaseAuth mAuth;
    //Ads
    //private AdView mAdView;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Ads

//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//
//            }
//        });


        //interstital ad
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-6129503049169576/8664828004", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });

        //Firebase
        //mAuth = FirebaseAuth.getInstance();

        //
        play_cpu_bu = findViewById(R.id.pl1);
        play_multi_bu = findViewById(R.id.pl2);
        play_online_bu = findViewById(R.id.pl3);
        play_cpu_bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MenuActivity.this , GameActivity.class);
                intent.putExtra("isMulti",false);
                intent.putExtra("isOffline",true);
            //    startActivity(intent);
               showAd(intent);

            }
        });

        play_multi_bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MenuActivity.this , GameActivity.class);
                intent.putExtra("isMulti",true);
                intent.putExtra("isOffline",true);
            showAd(intent);

            }
        });

        play_online_bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MenuActivity.this , GameActivity.class);
                intent.putExtra("isMulti",true);
                intent.putExtra("isOffline",false);
             //   startActivity(intent);
          showAd(intent);


            }
        });
    }

    private void showAd(final Intent intent)
    {
        if (mInterstitialAd!=null) {
            mInterstitialAd.show(MenuActivity.this);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    startActivity(intent);
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }
            });
        } else {
            //  Log.d("TAG", "The interstitial wasn't loaded yet.");
            startActivity(intent);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();


    }
}
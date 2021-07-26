package com.omar.tictactoexo.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseUser;
import com.omar.tictactoexo.R;
import com.omar.tictactoexo.firbasehelper.FirebaseAuthHelper;
import com.omar.tictactoexo.firbasehelper.FirebaseDatabaseHelper;
import com.omar.tictactoexo.gamePlayers.OnlineGameSession;
import com.omar.tictactoexo.gamePlayers.Player;
import com.omar.tictactoexo.gamePlayers.Users;
import com.omar.tictactoexo.utilties.LaodingDialog;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    Player player1;
    Player player2;
    Player activePlayer;
    TextView bu00;
    TextView bu01;
    TextView bu02;
    TextView bu10;
    TextView bu11;
    TextView bu12;
    TextView bu20;
    TextView bu21;
    TextView bu22;
    TextView player_name_tv;
    TextView player1_score_tv;
    TextView player2_score_tv;
    boolean isOffline;
    boolean isMulti;
    private ArrayList<TextView> activeButtons ;
    View winLine_view0;
    View winLine_view1;
    View winLine_view2;
    View winLine_view3;
    View winLine_view4;
    View winLine_view5;
    View winLine_view6;
    View winLine_view7;
    ImageView reset_img;
    ImageView hint_img;

    private Users user;
    private String user_key;
  private   FirebaseDatabaseHelper firebaseDatabaseHelper;
OnlineGameSession onlineGameSession;
private Player myPlaye;
    LaodingDialog progressDialog;
    private int level;

    //ads
    private AdView mAdViewGame;
    private RewardedAd mRewardedAd;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);



         savedInstanceState = getIntent().getExtras();
         isMulti = savedInstanceState.getBoolean("isMulti");
         isOffline = savedInstanceState.getBoolean("isOffline");
         init();
         //Ads
        mAdViewGame = findViewById(R.id.adView_game);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdViewGame.loadAd(adRequest);



        if(!isOffline){
            firbaseInit();


        }
        else if((!isMulti)&&(isOffline)) {
            final AlertDialog alertDialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View v = inflater.inflate(R.layout.levels_dialog,null);

            builder.setView(v);

            builder.setCancelable(false);

            alertDialog = builder.create();
            alertDialog.show();
            Button b1 = v.findViewById(R.id.easybtn);
            Button b2 = v.findViewById(R.id.mediumbtn);
            Button b3 = v.findViewById(R.id.hardbtn);
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    level = 0;
                    alertDialog.dismiss();
                }
            });
            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    level = 1;
                    alertDialog.dismiss();
                }
            });
            b3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    level = 2;
                    alertDialog.dismiss();
                }
            });


        }

    }
    void firbaseInit()
    {
        progressDialog.show();
        //Auth
        FirebaseAuthHelper firebaseAuthHelper = new FirebaseAuthHelper();
        firebaseAuthHelper.signIn(new FirebaseAuthHelper.AuthStatus() {
            @Override
            public void onAuthSuccess(FirebaseUser firebaseUser) {

                user_key = firebaseUser.getUid();


                //Database
                firebaseDatabaseHelper = new FirebaseDatabaseHelper();

                user = new Users( user_key,true , false,null);
                firebaseDatabaseHelper.addUser( user,new FirebaseDatabaseHelper.DataStatus() {
                    @Override
                    public void DataIsLoaded(ArrayList<Users> users, ArrayList<String> keys) {

                    }

                    @Override
                    public void DataIsInserted() {

                        onlineGameSession = new OnlineGameSession();
//onlineGameSession = onlineGameSession.createGame(user);
                        createGame(user);
                    }

                    @Override
                    public void DataIsUpdated() {

                    }

                    @Override
                    public void DataIsDeleted() {

                    }
                });


            }
        });




//onlineGameSession.setCellID("bbb");





    }


    private void autoOnlinePlay() {

      //  Log.e("omaa","auto 1 ");



            firebaseDatabaseHelper.getGameSession(new FirebaseDatabaseHelper.GameDataStatus() {
                @Override
                public void DataIsLoaded(ArrayList<OnlineGameSession> onlineGameSessions, ArrayList<String> keys) {

                }

                @Override
                public void DataIsInserted(String key, OnlineGameSession session) {

                }

                @Override
                public void DataIsUpdated() {

                }

                @Override
                public void SingleDataIsLoaded(OnlineGameSession onlineGameSessions) {

                    onlineGameSession = onlineGameSessions;
                    String s = onlineGameSession.getCellID();

                    player_name_tv.setText(activePlayer.getName()+" turn");


                    if(activePlayer==myPlaye) {
                        unLockButtons(activeButtons);
                        hint_img.setVisibility(View.VISIBLE);
                    }


                    if ((!s.equalsIgnoreCase("bu")) && (activePlayer != myPlaye)) {


                        View temp = getView(onlineGameSession.getCellID());

                        if (temp.isClickable())
                            buClick(temp);
                        hint_img.setVisibility(View.GONE);

                    }






                }

                @Override
                public void DataIsDeleted() {

                }
            }, onlineGameSession.getGameID());

    }

    private void checkNoOfPlayers(){
        firebaseDatabaseHelper.getGameSession(new FirebaseDatabaseHelper.GameDataStatus() {
            @Override
            public void DataIsLoaded(ArrayList<OnlineGameSession> onlineGameSessions, ArrayList<String> keys) {

            }

            @Override
            public void DataIsInserted(String key, OnlineGameSession session) {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void SingleDataIsLoaded(OnlineGameSession onlineGameSessions) {
                onlineGameSession  =onlineGameSessions;

                if(onlineGameSession.getNoOfPlayers()==1){
                    progressDialog.show();

                    onlineGameSession.setActivePlayer(1);
                    onlineGameSession.setCellID("bu");

                    myPlaye = player1;
                    firebaseDatabaseHelper.updateGameSession(onlineGameSession.getGameID(), onlineGameSession, new FirebaseDatabaseHelper.GameDataStatus() {
                        @Override
                        public void DataIsLoaded(ArrayList<OnlineGameSession> onlineGameSessions, ArrayList<String> keys) {

                        }

                        @Override
                        public void DataIsInserted(String key, OnlineGameSession session) {

                        }

                        @Override
                        public void DataIsUpdated() {

                        }

                        @Override
                        public void SingleDataIsLoaded(OnlineGameSession onlineGameSessions) {

                        }

                        @Override
                        public void DataIsDeleted() {

                        }
                    });

                }
                else {
            //        unLockButtons();
                    progressDialog.dismiss();


                }

            }

            @Override
            public void DataIsDeleted() {

            }
        }, onlineGameSession.getGameID());
    }

    void init(){
        player1 = new Player("Player 1" ,0);
        player2 = new Player("Player 2",0);

        myPlaye = new Player("me",0);
        if(isOffline){
            activePlayer = player1;
            myPlaye = activePlayer;}

        bu00 = findViewById(R.id.bu00);
        bu01 = findViewById(R.id.bu01);
        bu02 = findViewById(R.id.bu02);
        bu10 = findViewById(R.id.bu10);
        bu11 = findViewById(R.id.bu11);
        bu12 = findViewById(R.id.bu12);
        bu20 = findViewById(R.id.bu20);
        bu21 = findViewById(R.id.bu21);
        bu22 = findViewById(R.id.bu22);
        player_name_tv = findViewById(R.id.player_name);
        player1_score_tv = findViewById(R.id.player1_score);
        player2_score_tv = findViewById(R.id.player2_score);
        if (isOffline&&!isMulti){
            player2.setName("CPU");
        }

        player_name_tv.setText(player1.getName()+" turn");

        reset_img = findViewById(R.id.reset_ing);
        if(!isOffline)
            reset_img.setVisibility(View.INVISIBLE);
        reset_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  endGame(false);
                for (int i=0 ;i<activeButtons.size();i++){
                    activeButtons.get(i).setText("");
                    activeButtons.get(i).setClickable(true);
                }
                player1_score_tv.setText("0");
                player2_score_tv.setText("0");
                if(!isOffline){
                    endOnlineGame();
                }



            }
        });
        //Ad reward


       loadRewardAd();
/*
        */


        hint_img = findViewById(R.id.hint_img);
        hint_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRewardedAd != null) {
                    mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            //   Log.d(TAG, 'Ad was shown.');
                           // mRewardedAd = null;

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when ad fails to show.
                            //   Log.d(TAG, 'Ad failed to show.');
                            loadRewardAd();
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Don't forget to set the ad reference to null so you
                            // don't show the ad a second time.
                            //   Log.d(TAG, 'Ad was dismissed.');
                           // if(!isOffline)
                                //cpuPlay();
                            loadRewardAd();

                        }
                    });
                    Activity activityContext = GameActivity.this;
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                       //     Log.d("TAG", "The user earned the reward.");
                          //  int rewardAmount = rewardItem.getAmount();
                           // String rewardType = rewardItem.getType();
                       //     cpuPlay();
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    //do stuff like remove view etc
                                    try {
                                        Thread.sleep(500);
                                        cpuPlay(2);

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });



                        }
                    });
                } else {
                  //  Log.d("TAG", "The rewarded ad wasn't ready yet.");
                    if(isOffline){
                        cpuPlay(level);
                    }
                }
               // hint_img.setVisibility(View.GONE);
            }

        });


        winLine_view0 = findViewById(R.id.winLine0);
        winLine_view1 = findViewById(R.id.winLine1);
        winLine_view2 = findViewById(R.id.winLine2);
        winLine_view3 = findViewById(R.id.winLine3);
        winLine_view4 = findViewById(R.id.winLine4);
        winLine_view5 = findViewById(R.id.winLine5);
        winLine_view6 = findViewById(R.id.winLine6);
        winLine_view7 = findViewById(R.id.winLine7);


        player1_score_tv.setText(""+player1.getScore());
        player2_score_tv.setText(""+player2.getScore());
        activeButtons = new ArrayList<>();
        activeButtons.add(bu00);
        activeButtons.add(bu01);
        activeButtons.add(bu02);
        activeButtons.add(bu10);
        activeButtons.add(bu11);
        activeButtons.add(bu12);
        activeButtons.add(bu20);
        activeButtons.add(bu21);
        activeButtons.add(bu22);

        progressDialog = new LaodingDialog(GameActivity.this);


    }

    private void loadRewardAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-6129503049169576/5058068560",
                adRequest, new RewardedAdLoadCallback(){
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        //   Log.d(TAG, loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;

                        //  Log.d(TAG, "onAdFailedToLoad");
                    }
                });
    }


    public void buClick(View view) {


        String activeText = "";
        TextView buTemp;


        switch (view.getId()) {
            case R.id.bu00:
                buTemp = bu00;
                activeButtons.get(0).setClickable(false);
                break;
            case R.id.bu01:
                buTemp = bu01;
                activeButtons.get(1).setClickable(false);

                break;
            case R.id.bu02:
                buTemp = bu02;
                activeButtons.get(2).setClickable(false);

                break;
            case R.id.bu10:
                buTemp = bu10;
                activeButtons.get(3).setClickable(false);

                break;
            case R.id.bu11:
                buTemp = bu11;
                activeButtons.get(4).setClickable(false);

                break;
            case R.id.bu12:
                buTemp = bu12;
                activeButtons.get(5).setClickable(false);

                break;
            case R.id.bu20:
                buTemp = bu20;
                activeButtons.get(6).setClickable(false);

                break;
            case R.id.bu21:
                buTemp = bu21;
                activeButtons.get(7).setClickable(false);

                break;
            case R.id.bu22:
                buTemp = bu22;
                activeButtons.get(8).setClickable(false);

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
        player_name_tv.setTextColor(getResources().getColor(R.color.prim));

        //  if(isOffline){
        if (activePlayer == player1) {
            activeText = "X";
            buTemp.setTextColor(getResources().getColor(R.color.colorx));
            //
            if (!isOffline)
                onlineGameSession.setActivePlayer(2);
          //  else
                activePlayer = player2;

        } else if (activePlayer == player2) {
            activeText = "O";

            //
            if (!isOffline)
                onlineGameSession.setActivePlayer(1);
        //    else
                activePlayer = player1;
            buTemp.setTextColor(getResources().getColor(R.color.colorRed));


        }

     //   if (isOffline)

        player_name_tv.setText("" + activePlayer.getName() + " turn");
      /*  else {
            if(activePlayer==player1)
                player_name_tv.setText("" + player2.getName() + " turn");
            else if(activePlayer==player2)
                player_name_tv.setText("" + player1.getName() + " turn");*/

       // }


        buTemp.setText(activeText);
        buTemp.setClickable(false);
        //buTemp = null;
        final boolean isEnd = checkEnd();


        if (isOffline && (activePlayer == player2) && !isEnd && !isMulti) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //do stuff like remove view etc
                    try {
                        Thread.sleep(800);
                        cpuPlay(level);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });


        }
        if (!isOffline) {

            lockButtons(activeButtons);

            if(isEnd && (activePlayer!=myPlaye)){
                onlineGameSession.setCellID("bu");
            }else
                onlineGameSession.setCellID(convertViewIDtoString(buTemp) + "");


            firebaseDatabaseHelper.updateCellID(onlineGameSession.getGameID(), onlineGameSession.getCellID(), onlineGameSession.getActivePlayer(), new FirebaseDatabaseHelper.GameDataStatus() {
                @Override
                public void DataIsLoaded(ArrayList<OnlineGameSession> onlineGameSessions, ArrayList<String> keys) {

                }

                @Override
                public void DataIsInserted(String key, OnlineGameSession session) {

                }

                @Override
                public void DataIsUpdated() {

                }

                @Override
                public void SingleDataIsLoaded(OnlineGameSession onlineGameSessions) {

                }

                @Override
                public void DataIsDeleted() {

                }
            });




        // String s = onlineGameSession.getCellID();
        //  while (s.equalsIgnoreCase(onlineGameSession.getCellID()))
        //       autoOnlinePlay();
        // Log.e("o ", " o= "+convertViewIDtoString(buTemp));
        //    onlineGameSession.updateGameSession();
        //updateGameSession(onlineGameSession);
    }
    }


    void endOnlineGame(){
        onlineGameSession.setCellID("bu");

        firebaseDatabaseHelper.updateCellID(onlineGameSession.getGameID(), onlineGameSession.getCellID(), onlineGameSession.getActivePlayer(), new FirebaseDatabaseHelper.GameDataStatus() {
            @Override
            public void DataIsLoaded(ArrayList<OnlineGameSession> onlineGameSessions, ArrayList<String> keys) {

            }

            @Override
            public void DataIsInserted(String key, OnlineGameSession session) {

            }

            @Override
            public void DataIsUpdated() {
             //   unLockButtons();
          //      reset_img.performClick();
                for(int i=0 ;i<activeButtons.size();i++){
                    activeButtons.get(i).setText("");
                }

            }

            @Override
            public void SingleDataIsLoaded(OnlineGameSession onlineGameSessions) {

            }

            @Override
            public void DataIsDeleted() {

            }
        });


    }

    private void lockButtons(ArrayList<TextView> artemp) {

        for (int i=0 ;i<artemp.size();i++){
            //if(activeButtons.get(i).isClickable())

                artemp.get(i).setEnabled(false);

        }
    }
    private void unLockButtons(ArrayList<TextView> artemp) {

        for (int i=0 ;i<artemp.size();i++) {

            artemp.get(i).setEnabled(true);
        }
    }


    public void createGame(Users userrr){

        firebaseDatabaseHelper.createGameSession(new FirebaseDatabaseHelper.GameDataStatus() {
            @Override
            public void DataIsLoaded(ArrayList<OnlineGameSession> onlineGameSessions, ArrayList<String> keys) {

            }

            @Override
            public void DataIsInserted(String key, OnlineGameSession session) {
                onlineGameSession =session;
                int pl = onlineGameSession.getNoOfPlayers();
          //      Log.e("cr","pl = "+pl) ;
                if(onlineGameSession.getActivePlayer()==1){
                    activePlayer=player1;

                }
                else {
                    activePlayer=player2;

                }
                if(pl==1){
                    player1.setName("Your ");
                    player2.setName("Your opponent's ");
                    myPlaye = player1;

                }
                else if(pl==2) {
                    player2.setName("Your ");
                    player1.setName("Your opponent's ");
                    myPlaye = player2;



                }
                player_name_tv.setText(activePlayer.getName()+"turn");
                autoOnlinePlay();
                checkNoOfPlayers();
              //  Log.e("om",""+(onlineGameSession ==null));
            }

            @Override
            public void DataIsUpdated( ) {

            }

            @Override
            public void SingleDataIsLoaded(OnlineGameSession onlineGameSessions) {

            }

            @Override
            public void DataIsDeleted() {

            }
        }, userrr);
    }
    public void updateGameSession(OnlineGameSession game){

        firebaseDatabaseHelper.updateGameSession(game.getGameID(), game, new FirebaseDatabaseHelper.GameDataStatus() {
            @Override
            public void DataIsLoaded(ArrayList<OnlineGameSession> onlineGameSessions, ArrayList<String> keys) {

            }

            @Override
            public void DataIsInserted(String key, OnlineGameSession session) {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void SingleDataIsLoaded(OnlineGameSession onlineGameSessions) {

            }

            @Override
            public void DataIsDeleted() {

            }
        });

    }

    void cpuPlay(int level){


     //   ArrayList<TextView> btns = new ArrayList<>();
        if(level==0){
        Random  random = new Random();
        int randomNo = random.nextInt(9);

        while (!activeButtons.get(randomNo).isClickable()){
            randomNo = random.nextInt(9);
        }
        activeButtons.get(randomNo).performClick();}
        else {

         if((activeButtons.get(0).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(2).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(1).isClickable()))
            activeButtons.get(1).performClick();
        else if((activeButtons.get(0).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(1).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(2).isClickable()))
            activeButtons.get(2).performClick();
        else if((activeButtons.get(1).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(2).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(0).isClickable()))
            activeButtons.get(0).performClick();
        else if((activeButtons.get(3).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(4).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(5).isClickable()))
            activeButtons.get(5).performClick();
        else if((activeButtons.get(3).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(5).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(4).isClickable()))
            activeButtons.get(4).performClick();
        else if((activeButtons.get(4).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(5).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(3).isClickable()))
            activeButtons.get(3).performClick();
        else if((activeButtons.get(6).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(7).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(8).isClickable()))
            activeButtons.get(8).performClick();
        else if((activeButtons.get(6).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(8).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(7).isClickable()))
            activeButtons.get(7).performClick();
        else if((activeButtons.get(6).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(7).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(8).isClickable()))
            activeButtons.get(8).performClick();
        else if((activeButtons.get(7).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(8).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(6).isClickable()))
            activeButtons.get(6).performClick();
        else if((activeButtons.get(0).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(3).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(6).isClickable()))
            activeButtons.get(6).performClick();
        else if((activeButtons.get(0).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(6).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(3).isClickable()))
            activeButtons.get(3).performClick();
        else if((activeButtons.get(3).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(6).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(0).isClickable()))
            activeButtons.get(0).performClick();

        else if((activeButtons.get(1).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(4).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(7).isClickable()))
            activeButtons.get(7).performClick();
        else if((activeButtons.get(1).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(7).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(4).isClickable()))
            activeButtons.get(4).performClick();
        else if((activeButtons.get(7).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(4).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(1).isClickable()))
            activeButtons.get(1).performClick();
        else if((activeButtons.get(2).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(5).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(8).isClickable()))
            activeButtons.get(8).performClick();
        else if((activeButtons.get(2).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(8).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(5).isClickable()))
            activeButtons.get(5).performClick();
        else if((activeButtons.get(8).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(5).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(2).isClickable()))
            activeButtons.get(2).performClick();

        else if((activeButtons.get(0).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(4).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(8).isClickable()))
            activeButtons.get(8).performClick();
        else if((activeButtons.get(0).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(8).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(4).isClickable()))
            activeButtons.get(4).performClick();
        else if((activeButtons.get(8).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(4).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(0).isClickable()))
            activeButtons.get(0).performClick();

        else if((activeButtons.get(2).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(4).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(6).isClickable()))
            activeButtons.get(6).performClick();
        else if((activeButtons.get(2).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(6).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(4).isClickable()))
            activeButtons.get(4).performClick();
        else if((activeButtons.get(6).getText().toString().equalsIgnoreCase("o")) &&
                (activeButtons.get(4).getText().toString().equalsIgnoreCase("o")) && (activeButtons.get(2).isClickable()))
            activeButtons.get(2).performClick();

        //X condition

        else if((activeButtons.get(0).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(2).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(1).isClickable()))
          activeButtons.get(1).performClick();
        else if((activeButtons.get(0).getText().toString().equalsIgnoreCase("x")) &&
                (activeButtons.get(1).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(2).isClickable()))
            activeButtons.get(2).performClick();
      else if((activeButtons.get(1).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(2).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(0).isClickable()))
          activeButtons.get(0).performClick();
      else if((activeButtons.get(3).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(4).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(5).isClickable()))
          activeButtons.get(5).performClick();
      else if((activeButtons.get(3).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(5).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(4).isClickable()))
          activeButtons.get(4).performClick();
      else if((activeButtons.get(4).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(5).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(3).isClickable()))
          activeButtons.get(3).performClick();
      else if((activeButtons.get(6).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(7).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(8).isClickable()))
          activeButtons.get(8).performClick();
      else if((activeButtons.get(6).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(8).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(7).isClickable()))
          activeButtons.get(7).performClick();
      else if((activeButtons.get(6).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(7).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(8).isClickable()))
          activeButtons.get(8).performClick();
      else if((activeButtons.get(7).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(8).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(6).isClickable()))
          activeButtons.get(6).performClick();
      else if((activeButtons.get(0).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(3).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(6).isClickable()))
          activeButtons.get(6).performClick();
      else if((activeButtons.get(0).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(6).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(3).isClickable()))
          activeButtons.get(3).performClick();
      else if((activeButtons.get(3).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(6).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(0).isClickable()))
          activeButtons.get(0).performClick();

      else if((activeButtons.get(1).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(4).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(7).isClickable()))
          activeButtons.get(7).performClick();
      else if((activeButtons.get(1).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(7).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(4).isClickable()))
          activeButtons.get(4).performClick();
      else if((activeButtons.get(7).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(4).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(1).isClickable()))
          activeButtons.get(1).performClick();
      else if((activeButtons.get(2).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(5).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(8).isClickable()))
          activeButtons.get(8).performClick();
      else if((activeButtons.get(2).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(8).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(5).isClickable()))
          activeButtons.get(5).performClick();
      else if((activeButtons.get(8).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(5).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(2).isClickable()))
          activeButtons.get(2).performClick();

      else if((activeButtons.get(0).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(4).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(8).isClickable()))
          activeButtons.get(8).performClick();
      else if((activeButtons.get(0).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(8).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(4).isClickable()))
          activeButtons.get(4).performClick();
      else if((activeButtons.get(8).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(4).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(0).isClickable()))
          activeButtons.get(0).performClick();

      else if((activeButtons.get(2).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(4).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(6).isClickable()))
          activeButtons.get(6).performClick();
      else if((activeButtons.get(2).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(6).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(4).isClickable()))
          activeButtons.get(4).performClick();
      else if((activeButtons.get(6).getText().toString().equalsIgnoreCase("x")) &&
              (activeButtons.get(4).getText().toString().equalsIgnoreCase("x")) && (activeButtons.get(2).isClickable()))
          activeButtons.get(2).performClick();

    //  else if(level==2){
       else if(activeButtons.get(0).isClickable() &&(level==2)) {

          activeButtons.get(0).performClick();
        }

       else if(activeButtons.get(2).isClickable()&&(level==2)){
            activeButtons.get(2).performClick();
      }
        else if(activeButtons.get(6).isClickable() &&(level==2)){  activeButtons.get(6).performClick(); }
    else if( activeButtons.get(8).isClickable() &&(level==2)){activeButtons.get(8).performClick(); }
    else if(activeButtons.get(4).isClickable()){activeButtons.get(4).performClick();}
    else {
        for (int i=0;i<activeButtons.size();i++){
            if(activeButtons.get(i).isClickable()) {
                activeButtons.get(i).performClick();
                break;
            }
        }}
      }



    }


    boolean checkEnd(){

        //case 1
         boolean isWin= true;
         boolean isDraw = false;
        if(((bu00.getText().equals(bu01.getText())) &&(bu01.getText().equals(bu02.getText())) &&(!bu00.isClickable())))
        {
            winLine_view0.setVisibility(View.VISIBLE);
        }

   else if  ((bu10.getText().equals(bu11.getText())) &&(bu11.getText().equals(bu12.getText())) &&(!bu11.isClickable())){
            winLine_view1.setVisibility(View.VISIBLE);

        }
                else if ((bu20.getText().equals(bu21.getText())) &&(bu21.getText().equals(bu22.getText())) &&(!bu22.isClickable())){
            winLine_view2.setVisibility(View.VISIBLE);

        }
        //case 2
        else if(((bu00.getText().equals(bu11.getText())) &&(bu11.getText().equals(bu22.getText())) &&(!bu00.isClickable()))){

            winLine_view7.setVisibility(View.VISIBLE);
        }
            else if ((bu02.getText().equals(bu11.getText())) &&(bu11.getText().equals(bu20.getText())) &&(!bu02.isClickable())){
            winLine_view6.setVisibility(View.VISIBLE);

        }
        //case 3

        else if(((bu00.getText().equals(bu10.getText())) &&(bu10.getText().equals(bu20.getText())) &&(!bu00.isClickable())))
        {
            winLine_view3.setVisibility(View.VISIBLE);
        }
               else if  ((bu01.getText().equals(bu11.getText())) &&(bu11.getText().equals(bu21.getText())) &&(!bu01.isClickable())){
                   winLine_view4.setVisibility(View.VISIBLE);
        }
                else if ((bu02.getText().equals(bu12.getText())) &&(bu12.getText().equals(bu22.getText())) &&(!bu02.isClickable())){
                    winLine_view5.setVisibility(View.VISIBLE);

        }
        else {
            isWin = false;
            isDraw = checkDraw();
        }
        if(isDraw || isWin)
        {
            endGame(isWin);
        }
        return isWin||isDraw;



    }


    boolean checkDraw(){
        int count=0;

       for ( int i=0 ;i<activeButtons.size();i++){
           if(!activeButtons.get(i).isClickable())
               count++;

       }
        return count == 9;

    }

    void endGame(final boolean isWin){


        if(activePlayer == player1) {
            activePlayer = player2;
            if(!isOffline)
                onlineGameSession.setActivePlayer(2);

        }
        else {
            activePlayer = player1;
            if(!isOffline)
                onlineGameSession.setActivePlayer(1);
        }
        if(!isOffline){

            endOnlineGame();
          //  Log.e("oaa","o = "+activePlayer.getName());

        }
        if (isWin &&(isOffline)){
            player_name_tv.setTextColor(getResources().getColor(R.color.colorYellow));
            player_name_tv.setText(""+activePlayer.getName() + " Win");

        }
        else if(isWin && (!isOffline)){
            if(activePlayer==myPlaye){
                player_name_tv.setText("You" + " Win");

            }
            else {
                player_name_tv.setText("You Lose the game");

            }
        }
        else {
            player_name_tv.setText( "Draw");

        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //do stuff like remove view etc
                try {
                    Thread.sleep(1500);



                    if(isWin) {
                        updateScore(activePlayer);


                    }
                    else {
                        player_name_tv.setText("Draw");

                    }

                    for (int i =0 ; i<activeButtons.size();i++){
                        activeButtons.get(i).setClickable(true);
                        activeButtons.get(i).setText("");
                    }
                    winLine_view0.setVisibility(View.GONE);
                    winLine_view1.setVisibility(View.GONE);
                    winLine_view2.setVisibility(View.GONE);
                    winLine_view3.setVisibility(View.GONE);
                    winLine_view4.setVisibility(View.GONE);
                    winLine_view5.setVisibility(View.GONE);
                    winLine_view6.setVisibility(View.GONE);
                    winLine_view7.setVisibility(View.GONE);



                    if ((activePlayer==player2)&&(isOffline) &&(!isMulti)){
                        cpuPlay(level);


                    }

                    if(isOffline) {
                        player_name_tv.setTextColor(getResources().getColor(R.color.prim));
                        player_name_tv.setText("" + activePlayer.getName() + " turn");

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });



      //  player_name_tv.setTextColor(getResources().getColor(R.color.prim));




    }



    void updateScore(Player player){
        int score = player.getScore();
        score++;
        player.setScore(score);
        if(player == player1)
        {
            player1_score_tv.setText(""+score);
        }
        else if(player == player2)
            player2_score_tv.setText(""+score);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {

        super.onStop();

        if(!isOffline)
            leaveGame();


    }

    @Override
    protected void onPause() {
      //  if(!isOffline)
       // leaveGame();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
   //     if(!isOffline)
     //       leaveGame();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
    /*    if(!isOffline){
        leaveGame();
        progressDialog.dismiss();}*/
        super.onBackPressed();

    /*    if(!isOffline) {
            progressDialog.dismiss();
            startActivity(new Intent(GameActivity.this, MenuActivity.class));
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void leaveGame(){
        int n = onlineGameSession.getNoOfPlayers();
        int c;
        if(n>0)
        n--;
        onlineGameSession.setNoOfPlayers(n);
        if(myPlaye==player1){
            c=1;
        }else
            c=2;

        firebaseDatabaseHelper.removePLayer(onlineGameSession ,c );
    }

    private String convertViewIDtoString(View view){

        String s ="";
        switch (view.getId()) {
            case R.id.bu00:
                s = "bu00" ;
                break;
            case R.id.bu01:
                s = "bu01" ;
                break;
            case R.id.bu02:
                s = "bu02" ;
                break;
            case R.id.bu10:
                s = "bu10" ;
                break;
            case R.id.bu11:
                s = "bu11" ;
                break;

            case R.id.bu12:
                s = "bu12" ;
                break;
            case R.id.bu20:
                s = "bu20" ;
                break;
            case R.id.bu21:
                s = "bu21" ;
                break;
            case R.id.bu22:
                s = "bu22" ;
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
return s;
    }
    private View getView(String id)
    {
        View view = null;
        switch (id) {
            case "bu00":
                view = bu00 ;
                break;
            case "bu01":
                view =bu01 ;
                break; case"bu02" :
                view = bu02;
                break; case "bu10":
                view =bu10;
                break; case "bu11":
                view = bu11;
                break; case "bu12":
                view = bu12;
                break;
            case "bu20" :
                view = bu20;
                break;
            case "bu21":
                view = bu21;
                break;
            case "bu22":
                view = bu22;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
        return view;

    }



}
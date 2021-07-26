package com.omar.tictactoexo.firbasehelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omar.tictactoexo.gamePlayers.OnlineGameSession;
import com.omar.tictactoexo.gamePlayers.Users;

import java.util.ArrayList;

public class FirebaseDatabaseHelper {

    private final FirebaseDatabase database;
    private final DatabaseReference myRef;
    private final DatabaseReference myRefPlay;

    ArrayList<Users> users = new ArrayList<>();
    ArrayList<OnlineGameSession> gameSessions = new ArrayList<>();
    private final FirebaseAuthHelper firebaseAuthHelper;
    private int childCount=-1;

    public interface DataStatus{
        void DataIsLoaded(ArrayList<Users> users , ArrayList<String>keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();

    }

    public interface GameDataStatus{
        void DataIsLoaded(ArrayList<OnlineGameSession> onlineGameSessions , ArrayList<String>keys);
        void DataIsInserted(String key , OnlineGameSession session);
        void DataIsUpdated();
        void SingleDataIsLoaded(OnlineGameSession onlineGameSessions);

        void DataIsDeleted();

    }


    public FirebaseDatabaseHelper() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        myRefPlay = database.getReference("playing");
        firebaseAuthHelper= new FirebaseAuthHelper();
       // Log.e("heee","k : "+myRef.push());

    }

    public void readUsers(final DataStatus dataStatus){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {
                users.clear();
                ArrayList<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode:snapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                   // String cellID = keyNode.child("cellID").getValue(String.class);
                 //   Boolean isOnline = keyNode.child("isOnline").getValue(Boolean.class);
               //     Boolean isPalying = keyNode.child("isPlaying").getValue(Boolean.class);


                       Users user = snapshot.child(keyNode.getKey()).getValue(Users.class);  //new Users( isOnline , isPalying,cellID);

                    users.add(user);
                  //   Log.e("heee","k : "+keyNode.getValue());


                }
                dataStatus.DataIsLoaded(users , keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addUser( Users user, final DataStatus dataStatus  ){

        //String key = myRef.push().getKey();
        String uid = firebaseAuthHelper.getUser().getUid();
        myRef.child(uid).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dataStatus.DataIsInserted();
            }
        });

    }

    public void updateUserCellID(String key , Users user){
        myRef.child("users").child(key).setValue(user);
    }
    public Users getUserCellID(final Users user , String key) {
        myRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

               String cellID = snapshot.child("cellID").getValue(String.class);
               user.setCellID(cellID);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return user;
    }


    public void checkGameSessionUpdate(final GameDataStatus gameDataStatus , final OnlineGameSession gameSession , final String key){
        myRefPlay.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (DataSnapshot s: snapshot.getChildren() ){
                    String cell = s.child(key).child("cellID").getValue(String.class);
                    if(!(cell.equalsIgnoreCase(gameSession.getCellID()))){
                        gameSession.setCellID(cell);
                       // gameDataStatus.DataIsUpdated(gameSession);

                    }


                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public  void readGamesessions(final GameDataStatus gameDataStatus){

        myRefPlay.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gameSessions.clear();
                ArrayList<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode:snapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                  //   String gameID = keyNode.child("gameID").getValue(String.class);
                    String cellID = keyNode.child("cellID").getValue(String.class);
                    int activePlayer = keyNode.child("activePlayer").getValue(Integer.class);
                    int noOfPlayers = keyNode.child("noOfPlayers").getValue(Integer.class);
                 //   OnlineGameSession session = new OnlineGameSession(activePlayer , cellID , noOfPlayers);
            //        gameSessions.add(session);
                    //   Log.e("heee","k : "+keyNode.getValue());


                }
                gameDataStatus.DataIsLoaded(gameSessions , keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void createGameSession(final GameDataStatus gameDataStatus , final Users user ){
        final String[] gameKey = new String[1];
        final OnlineGameSession[] gameSession = new OnlineGameSession[1];
        gameKey[0]=null;
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             //   Log.e("fire","create game");
                childCount =(int) snapshot.getChildrenCount();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    boolean ch = (dataSnapshot.child("noOfPlayers").getValue(Integer.class)) ==1;

                    if(ch && (gameKey[0]==null)){
                        gameKey[0] = dataSnapshot.getKey();

                        gameSession[0] = dataSnapshot.getValue(OnlineGameSession.class);
                        gameSession[0].setGameID(gameKey[0]);
                        gameSession[0].setNoOfPlayers(2);

                        addUserToGame(user , gameKey[0] , 2);



                    }

                }

                if(gameKey[0]==null){
                    gameKey[0]= myRefPlay.push().getKey();
                    gameSession[0] = new OnlineGameSession(gameKey[0],1 , "bu" , 1);
                    myRefPlay.child(gameKey[0]).setValue(gameSession[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            addUserToGame(user , gameKey[0] , 1);

                        }
                    });

                }



                gameDataStatus.DataIsInserted(gameKey[0] , gameSession[0]);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        myRefPlay.addListenerForSingleValueEvent(valueEventListener);


      //   if(gameKey[0]!=null){
     //       addUserToGame(user , gameKey[0] , 2);

        //}
      // else
      /*  if(gameKey[0]==null){
            gameKey[0]= myRefPlay.push().getKey();
            gameSession[0] = new OnlineGameSession(gameKey[0],1 , null , 1);
            myRefPlay.child(gameKey[0]).setValue(gameSession[0]);
            addUserToGame(user , gameKey[0] , 1);

        }*/

        myRefPlay.removeEventListener(valueEventListener);

    }

    public void getGameSession(final GameDataStatus gameDataStatus  , final String key){
        final OnlineGameSession[] game = {null};
        myRefPlay.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

             //   Log.e("omaa","get");
                game[0] = snapshot.getValue(OnlineGameSession.class);
                if(game[0]!=null)
               gameDataStatus.SingleDataIsLoaded(game[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       // gameDataStatus.DataIsUpdated(gameSession);

    }
    public void updateGameSession(String key,OnlineGameSession session, final GameDataStatus gameDataStatus){

        myRefPlay.child(key).setValue(session).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                gameDataStatus.DataIsUpdated();
            }
        });

    }

    public void updateCellID(String key , String cellID , int active,final GameDataStatus gameDataStatus){
        myRefPlay.child(key).child("cellID").setValue(cellID);
        myRefPlay.child(key).child("activePlayer").setValue(active).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                gameDataStatus.DataIsUpdated();
            }
        });

      //  int actv = myRefPlay.child(key).child("activePlayer");


    }



    public void addUserToGame(Users user1 , String gameKey , int no){
        myRefPlay.child(gameKey).child("noOfPlayers").setValue(no);
        myRefPlay.child(gameKey).child(no+"").setValue(user1);


    }

    public void removePLayer(OnlineGameSession game , int no){

        myRefPlay.child(game.getGameID()).child(no+"").setValue(null);
    //    if(game.getNoOfPlayers()==1)
      //  Log.e("omar","f = "+game.getNoOfPlayers());
        if(game.getNoOfPlayers()<=0){
            myRefPlay.child(game.getGameID()).setValue(null);

        }else {
            myRefPlay.child(game.getGameID()).child("noOfPlayers").setValue(game.getNoOfPlayers());

        }

    }


}

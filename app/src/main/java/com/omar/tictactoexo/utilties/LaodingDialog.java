package com.omar.tictactoexo.utilties;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.omar.tictactoexo.R;

public class LaodingDialog {

    Activity activity;
    AlertDialog alertDialog;

    public LaodingDialog(Activity activity) {
        this.activity = activity;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog,null));

        builder.setCancelable(false);

        alertDialog = builder.create();

    }

    public void show(){

        alertDialog.show();

    }
    public void dismiss(){
        alertDialog.dismiss();
    }
}


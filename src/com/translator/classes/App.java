package com.translator.classes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import com.example.example.R;

/**
 * Created by Арем on 11.09.14.
 */
public class App {
    private static App instance;
    private Context context;
    public Server server;
    private Resources resources;

    public App(Context context){
        this.context = context;
        server = new Server();
        resources = context.getResources();
    }
    public Context getContext(){
        return context;
    }
    public static void initInstance(Context context) {
        if (instance == null)
        {
            instance = new App(context);
        }
    }
    public static App getInstance()
    {
        return instance;
    }
    public String getStringFromResources(int id){
        return resources.getString(id);
    }
    public void Alert(String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg).
        setNegativeButton(getStringFromResources(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        builder.show();
    }
}

package com.ellalan.certifiedparent;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;

public class TopExceptionHandler implements Thread.UncaughtExceptionHandler {
    Context context;

    private Thread.UncaughtExceptionHandler defaultUEH;

    private Activity app = null;

    public TopExceptionHandler(Activity app) {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.app = app;
        context=app.getApplicationContext();

    }

    public void uncaughtException(Thread t, Throwable e)
    {
        Log.d("aaa","entereddd");
        StackTraceElement[] arr = e.getStackTrace();
        String report = e.toString()+"\n\n";
        report += "--------- Stack trace ---------\n\n";
        for (int i=0; i<arr.length; i++)
        {
            report += "    "+arr[i].toString()+"\n";
            Log.d("aaa","loop1");

        }
        report += "-------------------------------\n\n";

// If the exception was thrown in a background thread inside
// AsyncTask, then the actual exception can be found with getCause
        report += "--------- Cause ---------\n\n";
        Throwable cause = e.getCause();
        if(cause != null) {
            report += cause.toString() + "\n\n";
            arr = cause.getStackTrace();
            for (int i=0; i<arr.length; i++)
            {
                Log.d("aaa","loop2");

                report += "    "+arr[i].toString()+"\n";
            }
        }
        report += "-------------------------------\n\n";






        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        String subject = "Error report";
        String body =
                "Mail to Maani5: Regarding Parenting Challenge"+
                        "\n"+
                        report+
                        "\n";
        Log.d("aaa","sengng mail");

        sendIntent.putExtra(Intent.EXTRA_EMAIL,
                new String[] {"chmusman88@gmail.com"});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.setType("message/rfc822");
        app.startActivity(sendIntent);

        Log.d("aaa","sengng mail sent");




        try {
            FileOutputStream trace = app.openFileOutput(
                    "stack.trace", Context.MODE_PRIVATE);
            trace.write(report.getBytes());
            trace.close();
        } catch(IOException ioe) {
// ...
        }

        Log.d("aaa","saveedfile");



        defaultUEH.uncaughtException(t, e);
    }
}
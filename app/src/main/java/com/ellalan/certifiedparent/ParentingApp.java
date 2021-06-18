package com.ellalan.certifiedparent;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
public class ParentingApp extends MultiDexApplication{

    @Override
    public void onCreate() {
        super.onCreate();

        initFacebook();

    }




    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    private void initFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
/*    public SpringMobile getSpring() {

    *//**
     * Gets the default {@link Tracker} for this {@link ParentingApp}.
     *
     * @return tracker
     *//*



    *//**
     * release the memory
     *//*





    *//**
     * Clear almost all the data if any serious issues arrives
     *//*


    }*/
}

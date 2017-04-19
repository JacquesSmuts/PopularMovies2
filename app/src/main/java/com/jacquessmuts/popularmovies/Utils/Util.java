package com.jacquessmuts.popularmovies.Utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Jacques Smuts on 2017/04/19.
 * Until they find a new home, random utils in here
 */

public class Util {

    public static int getDPI(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int)(metrics.density * 160f);
    }
}

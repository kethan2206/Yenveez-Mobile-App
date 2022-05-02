package com.example.yenveez_mobile_app.Beacon;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class Utils {
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isLocationBluePermission(final Context context) {
        if (!Utils.isMPhone()) {
            return true;
        } else {
            boolean result = true;
            if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                result = false;
            }
            return result;
        }
    }

    public static boolean isMPhone() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

}
package ru.startandroid.flashlight_new.utils;

import android.content.Context;
import android.content.pm.PackageManager;

public class DeviceUtils {
    public static boolean hasCameraFlash(Context context){
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

}
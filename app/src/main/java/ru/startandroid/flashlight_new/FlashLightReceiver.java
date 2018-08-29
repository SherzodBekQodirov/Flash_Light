package ru.startandroid.flashlight_new;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.util.Log;
import android.widget.RemoteViews;
import ru.startandroid.flashlight_new.ui.widget.FlashLightAppWidget;
import sherzodbek.flashlight.R;
public class FlashLightReceiver extends BroadcastReceiver {

    private Camera camera = null;
    private Camera.Parameters params;
    private static boolean isFlashLightOn = false;


    @Override
    public void onReceive(Context context, Intent intent) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.flash_light_app_widget);
        String action = intent.getAction();
        if(!action.equals(FlashLightAppWidget.FLASH_LIGHT_ACTION)) return;
        Log.d("FlashLightReceiver", "onReceive isFlashLightOn: "+ isFlashLightOn);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if (isFlashLightOn) {
            offFlashLight();
            views.setImageViewResource(R.id.btnSwitcher, R.drawable.widget_light_off);
            appWidgetManager.updateAppWidget(new ComponentName(context, FlashLightAppWidget.class), views);
        } else {
            onFlashLight();
            views.setImageViewResource(R.id.btnSwitcher, R.drawable.widget_light_on);
            appWidgetManager.updateAppWidget(new ComponentName(context, FlashLightAppWidget.class), views);
        }
    }
    public FlashLightReceiver(){
        connectCameraService();
    }
    public void onFlashLight() {
        isFlashLightOn = true;
        Log.d("FlashLightReceiver", "onFlashLight() "+ isFlashLightOn);
        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
        camera.startPreview();
    }
    public void offFlashLight() {
        isFlashLightOn = false;
        Log.d("FlashLightReceiver", "offFlashLight() "+ isFlashLightOn);
        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);
        camera.stopPreview();
    }
    public void connectCameraService() {
        if (camera != null) {
            camera.release();
            camera = null;
            camera = Camera.open();
        }else {
            camera = Camera.open();
        }



    }
}
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
    private boolean isFlashLightOn = false;

    public FlashLightReceiver(){
        connectCameraService();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(!action.equals(FlashLightAppWidget.FLASH_LIGHT_ACTION)) return;

        // 1 marta bosilganda icon uzgardi va 1 marta morgat qildi keyin uchdi
        // 1 marta bosganimda icon uzgardi, fonar ishlamadi, va icon qaytib uzgarmay qoldi
        //

        Log.d("FlashLightReceiver", "onReceive isFlashLightOn: "+ isFlashLightOn);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.flash_light_app_widget);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        //connectCameraService();
        //appWidgetManager.updateAppWidget(new ComponentName(context, LightWidgetProvider.class), views);
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

    public void onFlashLight() {
        params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        params = camera.getParameters();
        camera.setParameters(params);
        camera.startPreview();
        isFlashLightOn = true;
    }

    public void offFlashLight() {
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        params = camera.getParameters();
        camera.setParameters(params);
        camera.stopPreview();
        isFlashLightOn = false;
    }

    public void connectCameraService() {
        if (camera == null) {
            camera = Camera.open();
            params = camera.getParameters();
        }
    }
}
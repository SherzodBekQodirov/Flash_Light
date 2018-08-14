package ru.startandroid.flashlight_new.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Arrays;

import ru.startandroid.flashlight_new.FlashLightReceiver;
import sherzodbek.flashlight.R;

/**
 * Implementation of App Widget functionality.
 */
public class FlashLightAppWidget extends AppWidgetProvider {

    public static String FLASH_LIGHT_ACTION = "ACTION_FLASH_LIGHT";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(FLASH_LIGHT_ACTION.equals(intent.getAction())){
            Toast.makeText(context, "onReceiver()", Toast.LENGTH_LONG).show();
            Intent i = new Intent(FLASH_LIGHT_ACTION);
            context.sendBroadcast(i);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        Log.i("ExampleWidget", "Update widgets "+ Arrays.asList(appWidgetIds));
        for (int i = 0; i < N; i++){
            int appWidgetID = appWidgetIds[i];
            Intent intent = new Intent(context, FlashLightReceiver.class);
            intent.setAction(FLASH_LIGHT_ACTION);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.flash_light_app_widget);
            views.setOnClickPendingIntent(R.id.btnSwitcher, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetID, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}


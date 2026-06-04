package com.surffountain.browser.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.surffountain.browser.R;
import com.surffountain.browser.activities.MainActivity;

public class SearchWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_search);

            Intent intent = new Intent(context, MainActivity.class);
            intent.setAction(Intent.ACTION_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            views.setOnClickPendingIntent(R.id.widget_search_bar, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }
}

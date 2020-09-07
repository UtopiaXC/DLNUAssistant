package com.utopiaxc.urpassistant.weidgt;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.utopiaxc.urpassistant.R;

public class WeidgtChart extends AppWidgetProvider {
    public static final String CLICK_ACTION = "com.utopiaxc.urpassistant.weidgt.WeidgtChart.CLICK";
    private static RemoteViews mRemoteViews;

    /**
     * 每删除一次窗口小部件就调用一次
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        System.out.println("onDeleted");
        super.onDeleted(context, appWidgetIds);
    }

    /**
     * 当最后一个该窗口小部件删除时调用该方法，注意是最后一个
     */
    @Override
    public void onDisabled(Context context) {
        System.out.println("onDisabled");
        super.onDisabled(context);
    }

    /**
     * 当该窗口小部件第一次添加到桌面时调用该方法，可添加多次但只第一次调用
     */
    @Override
    public void onEnabled(Context context) {
        System.out.println("onEnabled");
        super.onEnabled(context);
    }

    /**
     * 接收窗口小部件点击时发送的广播
     */
    @Override
    public void onReceive(final Context context, Intent intent) {
        System.out.println("onReceive");
        super.onReceive(context, intent);

        //这里判断是自己的action，做自己的事情，比如小工具被点击了要干啥，这里是做来一个动画效果
        if (intent.getAction().equals(CLICK_ACTION)) {
            Toast.makeText(context, "clicked it", Toast.LENGTH_SHORT).show();

        }

    }

    /**
     * 每次窗口小部件被点击更新都调用一次该方法
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        System.out.println("onUpdate");
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final int counter = appWidgetIds.length;
        for (int i = 0; i < counter; i++) {
            int appWidgetId = appWidgetIds[i];
            onWidgetUpdate(context, appWidgetManager, appWidgetId);
        }

    }

    /**
     * 窗口小部件更新
     *
     * @param context
     * @param appWidgeManger
     * @param appWidgetId
     */
    private void onWidgetUpdate(Context context,
                                AppWidgetManager appWidgeManger, int appWidgetId) {

        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.weidgt_chart);

        // "窗口小部件"点击事件发送的Intent广播
        Intent intentClick = new Intent();
        intentClick.setAction(CLICK_ACTION);
        appWidgeManger.updateAppWidget(appWidgetId, mRemoteViews);
    }
}
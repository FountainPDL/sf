package com.surffountain.browser.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";
    private final Thread.UncaughtExceptionHandler defaultHandler;
    private final Context context;

    private CrashHandler(Context context) {
        this.context = context;
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public static void install(Context context) {
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(context));
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            saveCrashLog(ex);
        } catch (Exception e) {
            Log.e(TAG, "Failed to save crash log", e);
        }
        if (defaultHandler != null) {
            defaultHandler.uncaughtException(thread, ex);
        }
    }

    private void saveCrashLog(Throwable ex) {
        File dir = new File(context.getFilesDir(), "crashes");
        dir.mkdirs();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File file = new File(dir, "crash_" + timestamp + ".txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("Surf Fountain Crash Report");
            pw.println("Time: " + timestamp);
            pw.println("---");
            ex.printStackTrace(pw);
        } catch (Exception e) {
            Log.e(TAG, "Write failed", e);
        }
    }
}

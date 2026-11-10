package com.ansub.tvhome;

import android.content.Context;
import android.os.Build;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** Recovers only our fixed shell helper; no package, setting or generic shell controls. */
final class HelperRecovery {
    private static final ScheduledExecutorService WORK = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "overlook-helper-recovery"); thread.setDaemon(true); return thread;
    });
    private static boolean started;
    private static int failures;
    private static java.util.concurrent.ScheduledFuture<?> next;
    static volatile String status = "Not started";
    static synchronized void start(Context context) {
        if (started || Build.VERSION.SDK_INT != 28) return;
        started = true;
        ShellPlayback.start(context);
        Context app = context.getApplicationContext(); next = WORK.schedule(() -> check(app), 3, TimeUnit.SECONDS);
    }
    static void authorize(Context context) {
        Context app = context.getApplicationContext();
        WORK.execute(() -> {
            try (LocalAdb ignored = new LocalAdb(app, true)) {
                status = "TV key authorized"; failures = 0;
                if (next != null) next.cancel(false);
                synchronized (HelperRecovery.class) { started = true; }
                check(app);
            }
            catch (Exception e) { status = e instanceof SecurityException ? "TV key authorization required" : "TV key connection unavailable"; }
            start(app);
        });
    }
    private static void check(Context context) {
        try {
            android.os.IBinder binder = RecentAppsBridge.service;
            if (binder != null && binder.isBinderAlive()) { status = "Helper connected"; failures = 0; }
            else {
                byte[] jar;
                try (InputStream input = context.getAssets().open("recents-helper.jar")) { jar = LocalAdb.limited(input, 256 * 1024); }
                try (LocalAdb adb = new LocalAdb(context, false)) { adb.upload(jar); }
                try (LocalAdb adb = new LocalAdb(context, false)) { adb.startHelper(); }
                status = "Helper started; awaiting attachment"; failures = 0;
            }
        } catch (SecurityException e) { status = "TV key authorization required"; failures++; }
        catch (Exception e) { status = "Helper unavailable (" + e.getClass().getSimpleName() + "); retrying"; failures++; }
        next = WORK.schedule(() -> check(context), failures == 0 ? 30 : Math.min(300, 15L << Math.min(failures, 4)), TimeUnit.SECONDS);
    }
}

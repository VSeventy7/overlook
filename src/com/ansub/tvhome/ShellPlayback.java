package com.ansub.tvhome;

import android.content.Context;
import android.os.SystemClock;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Low-RAM Android 9 fallback through the already authorized narrow TV helper. */
final class ShellPlayback {
    private static boolean started;
    private static final PlaybackRules.Gate GATE = new PlaybackRules.Gate();
    static volatile String status = "Not started";
    static synchronized void start(Context context) {
        if (started || android.os.Build.VERSION.SDK_INT != 28) return;
        started = true; Context app = context.getApplicationContext();
        Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "overlook-shell-playback"); thread.setDaemon(true); return thread;
        }).scheduleWithFixedDelay(() -> poll(app), 5, 5, TimeUnit.SECONDS);
    }
    private static void poll(Context context) {
        if (PlaybackTracker.isConnected()) { GATE.clear(); status = "Native media tracking active"; return; }
        android.os.IBinder binder = RecentAppsBridge.service;
        if (binder == null || !binder.isBinderAlive()) { GATE.clear(); status = "Awaiting TV helper"; return; }
        try {
            JSONArray sessions = new JSONObject(RecentAppsBridge.request("PLAYBACK")).getJSONArray("sessions");
            int eligible = 0;
            for (int i = 0; i < sessions.length(); i++) {
                JSONObject session = sessions.getJSONObject(i); String pkg = session.getString("package");
                android.content.pm.ApplicationInfo info = context.getPackageManager().getApplicationInfo(pkg, 0);
                if (!PlaybackTracker.video(pkg, info)) continue;
                String title = session.getString("title");
                String label = context.getPackageManager().getApplicationLabel(info).toString();
                if (PlaybackRules.generic(title, label)) continue;
                eligible++;
                PlaybackHistory.Entry entry = new PlaybackHistory.Entry(); entry.pkg = pkg; entry.title = title;
                entry.id = PlaybackHistory.id(pkg, title, null); entry.position = session.getLong("position");
                entry.updated = System.currentTimeMillis();
                if (GATE.sample(pkg, entry.id, SystemClock.elapsedRealtime(), entry.position, session.getBoolean("playing")))
                    PlaybackHistory.get(context).record(entry, null);
            }
            status = "Helper tracking active; " + eligible + " usable sessions";
        } catch (Exception e) { GATE.clear(); status = "Playback helper unavailable"; }
    }
}

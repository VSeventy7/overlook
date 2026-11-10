package com.ansub.tvhome;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Notification access is used solely to inspect active media sessions, never notification contents. */
public final class PlaybackTracker extends NotificationListenerService {
    private HandlerThread thread;
    private Handler worker;
    private static volatile boolean connected;
    static boolean isConnected() { return connected; }
    private final PlaybackRules.Gate gate = new PlaybackRules.Gate();
    @Override public void onListenerConnected() {
        if (android.os.Build.VERSION.SDK_INT < 26) return;
        HelperRecovery.start(this);
        connected = true;
        if (thread == null) {
            thread = new HandlerThread("overlook-playback"); thread.start(); worker = new Handler(thread.getLooper());
        }
        worker.removeCallbacks(poll); worker.post(poll);
    }
    @Override public void onListenerDisconnected() {
        connected = false;
        if (worker != null) worker.post(() -> { worker.removeCallbacks(poll); gate.clear(); PlaybackHistory.get(this).targets(new HashMap<>()); });
    }
    @Override public void onDestroy() {
        connected = false;
        if (worker != null) worker.removeCallbacksAndMessages(null);
        if (thread != null) thread.quitSafely();
        super.onDestroy();
    }
    private final Runnable poll = new Runnable() {
        @Override public void run() {
            if (!connected) return;
            PlaybackHistory history = PlaybackHistory.get(PlaybackTracker.this);
            Map<String, PendingIntent> targets = new HashMap<>();
            try {
                MediaSessionManager manager = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
                List<MediaController> sessions = manager.getActiveSessions(new ComponentName(PlaybackTracker.this, PlaybackTracker.class));
                for (MediaController controller : sessions) {
                    try {
                        String pkg = controller.getPackageName();
                        ApplicationInfo info = getPackageManager().getApplicationInfo(pkg, 0);
                        if (!video(pkg, info)) continue;
                        MediaMetadata metadata = controller.getMetadata(); PlaybackState state = controller.getPlaybackState();
                        if (metadata == null || state == null) continue;
                        String title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE);
                        if (title == null || title.trim().isEmpty()) title = metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE);
                        String label = getPackageManager().getApplicationLabel(info).toString();
                        if (PlaybackRules.generic(title, label)) continue;
                        PlaybackHistory.Entry entry = new PlaybackHistory.Entry();
                        entry.pkg = pkg; entry.title = title.trim();
                        entry.id = PlaybackHistory.id(pkg, entry.title, metadata.getString(MediaMetadata.METADATA_KEY_MEDIA_ID));
                        PendingIntent target = controller.getSessionActivity();
                        if (target != null && pkg.equals(target.getCreatorPackage())) targets.put(entry.id, target);
                        entry.position = state.getPosition(); entry.duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
                        entry.updated = System.currentTimeMillis();
                        boolean qualifies = gate.sample(pkg, entry.id, SystemClock.elapsedRealtime(), entry.position,
                                state.getState() == PlaybackState.STATE_PLAYING);
                        if (!qualifies) continue;
                        entry.mediaUri = metadata.getString(MediaMetadata.METADATA_KEY_MEDIA_URI);
                        entry.subtitle = metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE);
                        entry.art = PlaybackLinks.image(metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI));
                        if (entry.art == null) entry.art = PlaybackLinks.image(metadata.getString(MediaMetadata.METADATA_KEY_ART_URI));
                        if (entry.art == null) entry.art = PlaybackLinks.image(metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI));
                        entry.artSource = entry.art == null ? null : "app";
                        android.graphics.Bitmap bitmap = metadata.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON);
                        if (bitmap == null) bitmap = metadata.getBitmap(MediaMetadata.METADATA_KEY_ART);
                        if (bitmap == null) bitmap = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
                        history.record(entry, bitmap);
                    } catch (Exception ignored) { }
                }
            } catch (SecurityException ignored) { gate.clear(); }
            catch (Exception ignored) { }
            history.targets(targets);
            if (connected) worker.postDelayed(this, 5000);
        }
    };
    static boolean video(String pkg, ApplicationInfo info) {
        return info.category == ApplicationInfo.CATEGORY_VIDEO || ("|com.netflix.ninja|com.amazon.amazonvideo.livingroom|com.google.android.youtube.tv|com.disney.hotstar|in.startv.hotstar|com.sonyliv|com.crunchyroll.crunchyroid|com.apple.atve.androidtv.appletv|org.jellyfin.androidtv|com.plexapp.android|").contains("|" + pkg + "|");
    }
}

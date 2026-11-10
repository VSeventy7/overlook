package com.ansub.tvhome;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/** Platform-independent qualification rules; all timing uses a monotonic clock. */
final class PlaybackRules {
    static final long RETENTION_MS = 30L * 24 * 60 * 60 * 1000;
    static String normalize(String text) {
        return text == null ? "" : Normalizer.normalize(text, Normalizer.Form.NFKC)
                .toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]", "");
    }
    static boolean generic(String title, String appLabel) {
        String normalized = normalize(title);
        return normalized.length() < 2 || normalized.equals(normalize(appLabel))
                || ("|netflix|primevideo|amazonprimevideo|disneyplus|hotstar|jiohotstar|sonyliv|crunchyroll|appletv|youtube|unknown|")
                        .contains("|" + normalized + "|");
    }
    static boolean expired(long playedAt, long now) {
        return playedAt <= 0 || (now >= playedAt && now - playedAt >= RETENTION_MS);
    }
    static final class Gate {
        private final Map<String, Window> windows = new LinkedHashMap<>();
        boolean sample(String source, String item, long elapsed, long position, boolean playing) {
            if (!playing || position < 0 || source == null || item == null) {
                windows.remove(source); return false;
            }
            Window window = windows.get(source);
            if (window == null || !item.equals(window.item) || elapsed <= window.lastAt
                    || elapsed - window.lastAt > 15000 || position < window.position
                    || position - window.position > (elapsed - window.lastAt) * 3 + 5000) {
                windows.put(source, new Window(item, elapsed, position));
                while (windows.size() > 16) windows.remove(windows.keySet().iterator().next());
                return false;
            }
            long advance = position - window.position;
            if (advance > 0) { window.progress += advance; window.progressAt = elapsed; }
            if (elapsed - window.progressAt > 15000) {
                windows.put(source, new Window(item, elapsed, position)); return false;
            }
            window.position = position; window.lastAt = elapsed;
            return elapsed - window.started >= 60000 && window.progress >= 45000 && position >= 60000;
        }
        void clear() { windows.clear(); }
    }
    private static final class Window {
        final String item;
        final long started;
        long lastAt, progressAt, position, progress;
        Window(String item, long now, long position) {
            this.item = item; this.started = now; this.lastAt = now; this.progressAt = now;
            this.position = position;
        }
    }
}

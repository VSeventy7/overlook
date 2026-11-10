package com.ansub.tvhome;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.SystemClock;
import android.util.AtomicFile;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** App-private, bounded, 30-day history. No notification contents or account credentials. */
final class PlaybackHistory {
    static final Uri CHANGES = Uri.parse("content://com.ansub.tvhome.recents/history");
    private static PlaybackHistory instance;
    static synchronized PlaybackHistory get(Context context) {
        if (instance == null) instance = new PlaybackHistory(context.getApplicationContext());
        return instance;
    }
    private final Context context;
    private final AtomicFile file;
    private final File directory;
    private final Map<String, Entry> entries = new LinkedHashMap<>();
    private Map<String, PendingIntent> liveTargets = new HashMap<>();
    private long persistedAt;
    private PlaybackHistory(Context context) {
        this.context = context;
        directory = new File(context.getFilesDir(), "playback"); directory.mkdirs();
        file = new AtomicFile(new File(directory, "history.json"));
        try (FileInputStream input = file.openRead(); ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096]; int read;
            while ((read = input.read(buffer)) != -1) {
                if (bytes.size() + read > 512 * 1024) throw new IllegalStateException("History too large");
                bytes.write(buffer, 0, read);
            }
            JSONArray array = new JSONObject(new String(bytes.toByteArray(), StandardCharsets.UTF_8)).optJSONArray("entries");
            for (int i = 0; array != null && i < Math.min(array.length(), 100); i++) {
                Entry entry = Entry.from(array.getJSONObject(i));
                if (entry.id.matches("[0-9a-f]{64}") && entry.pkg.matches("[a-zA-Z0-9_.]+")) entries.put(entry.id, entry);
            }
        } catch (Exception ignored) { }
        if (prune()) save();
    }
    static String id(String pkg, String title, String mediaId) {
        return hash(pkg + "\n" + PlaybackRules.normalize(title) + "\n" + (mediaId == null ? "" : mediaId));
    }
    private static String hash(String value) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) result.append(String.format(java.util.Locale.ROOT, "%02x", b & 255));
            return result.toString();
        } catch (Exception e) { throw new IllegalStateException(e); }
    }
    synchronized void targets(Map<String, PendingIntent> targets) { liveTargets = new HashMap<>(targets); }
    synchronized PendingIntent target(String id) { return liveTargets.get(id); }
    synchronized boolean contains(String id) { return entries.containsKey(id); }
    synchronized void record(Entry observation, Bitmap bitmap) {
        prune();
        Entry entry = entries.get(observation.id);
        boolean fresh = entry == null;
        if (fresh) entry = observation;
        else {
            entry.position = observation.position; entry.duration = observation.duration;
            entry.updated = observation.updated; entry.subtitle = observation.subtitle;
            if (observation.mediaUri != null) entry.mediaUri = observation.mediaUri;
            if (observation.art != null && !observation.art.equals(entry.art)) {
                entry.art = observation.art; entry.artSource = "app";
            }
        }
        boolean persist = fresh || SystemClock.elapsedRealtime() - persistedAt >= 15000;
        if (bitmap != null && (fresh || persist) && storeArt(entry.id, bitmap)) {
            entry.art = "content://com.ansub.tvhome.recents/art/" + entry.id + ".jpg?v=" + entry.updated;
            entry.artSource = "app";
        }
        entries.put(entry.id, entry); prune();
        if (persist) { save(); changed(); }
        if (entry.art == null && !entry.pkg.equals("com.google.android.youtube.tv")) ArtworkLookup.enqueue(context, entry.copy());
    }
    private boolean storeArt(String id, Bitmap bitmap) {
        Bitmap small = bitmap;
        try {
            float fit = Math.min(1f, Math.min(640f / bitmap.getWidth(), 360f / bitmap.getHeight()));
            if (fit < 1) small = Bitmap.createScaledBitmap(bitmap, Math.max(1, Math.round(bitmap.getWidth() * fit)),
                    Math.max(1, Math.round(bitmap.getHeight() * fit)), true);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            if (!small.compress(Bitmap.CompressFormat.JPEG, 80, bytes) || bytes.size() > 256 * 1024) return false;
            AtomicFile art = new AtomicFile(new File(directory, id + ".jpg"));
            FileOutputStream output = null;
            try { output = art.startWrite(); output.write(bytes.toByteArray()); art.finishWrite(output); }
            catch (Exception e) { if (output != null) art.failWrite(output); return false; }
            return true;
        } catch (Exception ignored) { return false; }
        finally { if (small != bitmap) small.recycle(); }
    }
    synchronized File art(String name) {
        if (!name.matches("[0-9a-f]{64}\\.jpg")) return null;
        return new File(directory, name);
    }
    synchronized List<Entry> records() {
        boolean changed = prune(); if (changed) save();
        List<Entry> sorted = sorted();
        List<Entry> result = new ArrayList<>(); Set<String> shows = new HashSet<>();
        for (Entry entry : sorted) {
            if (entry.duration > 0 && entry.position >= entry.duration - 2000) continue;
            if (shows.add(entry.pkg + "\n" + PlaybackRules.normalize(entry.title) + "\n" + entry.year)) result.add(entry.copy());
        }
        return result;
    }
    synchronized boolean beginLookup(String id) {
        Entry entry = entries.get(id);
        if (entry == null || entry.art != null || System.currentTimeMillis() - entry.lookupAt < 24L * 60 * 60 * 1000) return false;
        entry.lookupAt = System.currentTimeMillis(); save(); return true;
    }
    synchronized void enrich(String id, String title, String art, String link, String country) {
        Entry entry = entries.get(id);
        if (entry == null || !entry.title.equals(title) || entry.art != null || PlaybackRules.expired(entry.updated, System.currentTimeMillis())) return;
        if (!country.equals(ArtworkLookup.country(context))) return;
        entry.art = art; entry.artSource = "JustWatch"; entry.link = link; save(); changed();
    }
    private List<Entry> sorted() {
        List<Entry> sorted = new ArrayList<>(entries.values());
        Collections.sort(sorted, (a, b) -> Long.compare(b.updated, a.updated)); return sorted;
    }
    private boolean prune() {
        boolean changed = false; long now = System.currentTimeMillis();
        for (Entry entry : new ArrayList<>(entries.values())) if (PlaybackRules.expired(entry.updated, now)) {
            entries.remove(entry.id); liveTargets.remove(entry.id); changed = true;
        }
        List<Entry> sorted = sorted();
        for (int i = 100; i < sorted.size(); i++) { entries.remove(sorted.get(i).id); changed = true; }
        Set<String> keep = new HashSet<>();
        for (Entry entry : entries.values()) keep.add(entry.id + ".jpg");
        File[] files = directory.listFiles();
        if (files != null) for (File child : files)
            if (child.getName().matches("[0-9a-f]{64}\\.jpg(?:\\.bak)?")
                    && !keep.contains(child.getName().replace(".bak", ""))) child.delete();
        return changed;
    }
    private void save() {
        FileOutputStream output = null;
        try {
            JSONArray array = new JSONArray(); for (Entry entry : sorted()) array.put(entry.json());
            byte[] bytes = new JSONObject().put("version", 1).put("entries", array).toString().getBytes(StandardCharsets.UTF_8);
            output = file.startWrite(); output.write(bytes); file.finishWrite(output);
            persistedAt = SystemClock.elapsedRealtime();
        } catch (Exception ignored) { if (output != null) file.failWrite(output); }
    }
    private void changed() { context.getContentResolver().notifyChange(CHANGES, null); }

    static final class Entry {
        String id, pkg, title, subtitle, mediaUri, art, artSource, link;
        int year;
        long position, duration, updated, lookupAt;
        Entry copy() { try { return from(json()); } catch (Exception e) { throw new IllegalStateException(e); } }
        JSONObject json() throws Exception {
            return new JSONObject().put("id", id).put("package", pkg).put("title", title).put("subtitle", subtitle)
                    .put("uri", mediaUri).put("art", art).put("artSource", artSource).put("link", link).put("year", year)
                    .put("position", position).put("duration", duration).put("updated", updated).put("lookupAt", lookupAt);
        }
        static Entry from(JSONObject json) {
            Entry e = new Entry(); e.id = json.optString("id"); e.pkg = json.optString("package");
            e.title = json.optString("title"); e.subtitle = nullable(json, "subtitle");
            e.mediaUri = nullable(json, "uri"); e.art = nullable(json, "art"); e.artSource = nullable(json, "artSource");
            e.link = nullable(json, "link"); e.year = json.optInt("year"); e.position = json.optLong("position");
            e.duration = json.optLong("duration"); e.updated = json.optLong("updated"); e.lookupAt = json.optLong("lookupAt");
            return e;
        }
        private static String nullable(JSONObject json, String key) { return json.isNull(key) ? null : json.optString(key, null); }
    }
}

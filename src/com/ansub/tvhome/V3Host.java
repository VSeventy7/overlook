package com.ansub.tvhome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** The small compatibility boundary between Couch TV v3 and existing Overlook features. */
public final class V3Host {
    private static final Map<String, Intent> targets = new ConcurrentHashMap<>();
    private static final Map<String, String> artwork = new ConcurrentHashMap<>();
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    public static void init(Context context) {
        if (android.os.Build.VERSION.SDK_INT < 23) return;
        SharedPreferences prefs = context.getSharedPreferences("config", 0);
        if (!prefs.getBoolean("v3-default-migrated", false)) {
            prefs.edit().putString("o.version", "v3").putBoolean("v3-default-migrated", true).apply();
            try {
                JSONArray order = new JSONArray();
                String saved = prefs.getString("apps", null);
                for (String pkg : saved == null ? Apps.LIST : saved.split(",")) if (!pkg.trim().isEmpty()) order.put(pkg.trim());
                JSONObject setting = new JSONObject().put("clock", "24".equals(prefs.getString("o.clock", "12")) ? "24h" : "12h")
                        .put("wx_show", "on".equals(prefs.getString("o.weather", "off")));
                String at = prefs.getString("o.location", context.getSharedPreferences("weather", 0).getString("at", ""));
                if (at.matches("-?[0-9.]+,-?[0-9.]+")) {
                    String[] ll = at.split(",");
                    setting.put("place", new JSONObject().put("lat", Double.parseDouble(ll[0])).put("lon", Double.parseDouble(ll[1]))
                            .put("name", "").put("timezone", TimeZone.getDefault().getID()));
                }
                context.getSharedPreferences("page", 0).edit()
                        .putString("tvhome.order.tv", new JSONObject().put("packages", order).put("line", Math.min(10, order.length())).put("pending", true).toString())
                        .putString("tvhome.settings", new JSONObject().put("s", setting).put("updated", System.currentTimeMillis()).put("pending", true).toString()).apply();
                String country = Locale.getDefault().getCountry();
                context.getSharedPreferences("couchtv", 0).edit().putString("people", "[]")
                        .putString("country", country.matches("[A-Z]{2}") ? country : "")
                        .putBoolean("adb_ok_once", new java.io.File(context.getFilesDir(), "helper-adb-key.pk8").isFile())
                        .putBoolean("adb_ok_tcp", new java.io.File(context.getFilesDir(), "helper-adb-key.pk8").isFile()).apply();
            } catch (Exception ignored) { }
        }
    }
    public static boolean selected(Context context) { return android.os.Build.VERSION.SDK_INT >= 23 && "v3".equals(context.getSharedPreferences("config", 0).getString("o.version", "v3")); }
    public static void versions(Activity activity) {
        MAIN.post(() -> new AlertDialog.Builder(activity).setTitle("Home screen version")
                .setSingleChoiceItems(android.os.Build.VERSION.SDK_INT >= 23 ? new String[]{"v3 · Couch TV", "v2 · Scenic dock", "v1 · Classic home"} : new String[]{"v2 · Scenic dock", "v1 · Classic home"}, selected(activity) ? 0 :
                        "v2".equals(activity.getSharedPreferences("config", 0).getString("o.version", "")) ? (android.os.Build.VERSION.SDK_INT >= 23 ? 1 : 0) : (android.os.Build.VERSION.SDK_INT >= 23 ? 2 : 1), (dialog, which) -> {
                    String version = android.os.Build.VERSION.SDK_INT >= 23 ? (which == 0 ? "v3" : which == 1 ? "v2" : "v1") : (which == 0 ? "v2" : "v1");
                    activity.getSharedPreferences("config", 0).edit().putString("o.version", version).apply(); dialog.dismiss();
                    if (activity instanceof MainActivity) activity.recreate();
                    else {
                        activity.startActivity(new Intent(activity, MainActivity.class).putExtra("overlook-rebuild", true).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        activity.finish();
                    }
                }).setNegativeButton("Cancel", null).show());
    }
    public static void controls(Activity activity) {
        MAIN.post(() -> new AlertDialog.Builder(activity).setTitle("Overlook controls")
                .setItems(new String[]{"Recent apps", "Home screen version", "Overlook settings and folders", "TV settings", "Couch TV setup / phone remote"}, (d, which) -> {
                    if (which == 0) activity.startActivity(new Intent(activity, RecentAppsActivity.class));
                    else if (which == 1) versions(activity);
                    else if (which == 2) activity.startActivity(new Intent(activity, MainActivity.class).putExtra("overlook-controls", true));
                    else if (which == 3) activity.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    else activity.startActivity(new Intent().setClassName(activity, "dev.couchtv.ontv.SetupActivity"));
                }).setNegativeButton("Close", null).show());
    }
    public static JSONArray folderApps(Context context) {
        JSONArray result = new JSONArray();
        try {
            JSONObject folders = new JSONObject(context.getSharedPreferences("config", 0).getString("o.folders", "{}"));
            Iterator<String> names = folders.keys();
            while (names.hasNext()) { String name = names.next(); result.put(new JSONObject().put("package", "folder:" + name).put("label", name).put("hasBanner", false)); }
        } catch (Exception ignored) { }
        return result;
    }
    public static boolean folder(Activity activity, String key) {
        if (!key.startsWith("folder:")) return false;
        MAIN.post(() -> {
            try {
                JSONObject groups = new JSONObject(activity.getSharedPreferences("config", 0).getString("o.folders", "{}"));
                JSONArray group = groups.optJSONArray(key.substring(7)); if (group == null) return;
                List<String> packages = new ArrayList<>(), labels = new ArrayList<>();
                for (int i = 0; i < group.length(); i++) {
                    String pkg = group.getString(i);
                    Intent launch = activity.getPackageManager().getLeanbackLaunchIntentForPackage(pkg);
                    if (launch == null) launch = activity.getPackageManager().getLaunchIntentForPackage(pkg);
                    if (launch != null) { packages.add(pkg); labels.add(activity.getPackageManager().getApplicationLabel(activity.getPackageManager().getApplicationInfo(pkg, 0)).toString()); }
                }
                new AlertDialog.Builder(activity).setTitle(key.substring(7)).setItems(labels.toArray(new String[0]), (d, which) -> {
                    Intent launch = activity.getPackageManager().getLeanbackLaunchIntentForPackage(packages.get(which));
                    if (launch == null) launch = activity.getPackageManager().getLaunchIntentForPackage(packages.get(which));
                    if (launch != null) activity.startActivity(launch);
                }).setNegativeButton("Close", null).show();
            } catch (Exception ignored) { }
        }); return true;
    }
    public static JSONArray watchNext(Context context) {
        JSONArray result = new JSONArray(); Set<String> seen = new HashSet<>(); Map<String, Integer> perApp = new HashMap<>();
        Map<String, Intent> nextTargets = new HashMap<>(); Map<String, String> nextArt = new HashMap<>();
        try (Cursor cursor = context.getContentResolver().query(Uri.parse("content://android.media.tv/watch_next_program"),
                new String[]{"_id", "package_name", "title", "intent_uri", "thumbnail_uri", "poster_art_uri", "last_playback_position_millis", "duration_millis", "last_engagement_time_utc_millis", "watch_next_type", "browsable"},
                null, null, "last_engagement_time_utc_millis DESC")) {
            while (cursor != null && cursor.moveToNext() && result.length() < 20) {
                String pkg = cursor.getString(1), title = cursor.getString(2), id = "wn:" + cursor.getLong(0);
                if (pkg == null || title == null || cursor.getInt(10) == 0 || cursor.getInt(9) > 2 || cursor.getInt(9) < 0) continue;
                int count = perApp.containsKey(pkg) ? perApp.get(pkg) : 0;
                if (count >= 3 || (cursor.getLong(7) > 0 && cursor.getLong(6) >= cursor.getLong(7))) continue;
                Intent intent = PlaybackLinks.view(context, pkg, cursor.getString(3)); if (intent == null) continue;
                String art = PlaybackLinks.image(cursor.getString(4)); if (art == null) art = PlaybackLinks.image(cursor.getString(5));
                result.put(item(context, id, pkg, title, art, cursor.getLong(6), cursor.getLong(7), cursor.getLong(8), false));
                nextTargets.put(id, intent); if (art != null) nextArt.put(id, art);
                seen.add(pkg + "\n" + PlaybackRules.normalize(title)); perApp.put(pkg, count + 1);
            }
        } catch (Exception ignored) { }
        for (PlaybackHistory.Entry entry : PlaybackHistory.get(context).records()) {
            if (result.length() >= 30 || !seen.add(entry.pkg + "\n" + PlaybackRules.normalize(entry.title))) continue;
            try {
                Intent intent = PlaybackLinks.view(context, entry.pkg, entry.mediaUri); boolean appOnly = intent == null;
                if (intent == null) intent = context.getPackageManager().getLeanbackLaunchIntentForPackage(entry.pkg);
                if (intent == null) continue;
                String id = "history:" + entry.id;
                result.put(item(context, id, entry.pkg, entry.title, entry.art, entry.position, entry.duration, entry.updated, appOnly));
                nextTargets.put(id, intent); if (entry.art != null) nextArt.put(id, entry.art);
            } catch (Exception ignored) { }
        }
        targets.putAll(nextTargets); artwork.putAll(nextArt);
        // Keep only current bounded feed targets; no caller-controlled URLs or intents.
        targets.keySet().retainAll(nextTargets.keySet()); artwork.keySet().retainAll(nextArt.keySet());
        return result;
    }
    private static JSONObject item(Context context, String id, String pkg, String title, String art, long position, long duration, long engaged, boolean appOnly) throws Exception {
        String label = context.getPackageManager().getApplicationLabel(context.getPackageManager().getApplicationInfo(pkg, 0)).toString();
        return new JSONObject().put("id", id).put("wn", true).put("package", pkg).put("title", title).put("engaged", engaged)
                .put("app", new JSONObject().put("package", pkg).put("name", label + (appOnly ? " · Open app" : "")))
                .put("backdrop", art == null ? JSONObject.NULL : "/overlook/art/" + Uri.encode(id))
                .put("progress", duration > 0 ? Math.min(1d, (double)position / duration) : JSONObject.NULL);
    }
    public static boolean open(Context context, String id) {
        Intent intent = targets.get(id); if (intent == null) { watchNext(context); intent = targets.get(id); }
        if (intent == null) return false;
        final Intent chosen = intent; MAIN.post(() -> { try { context.startActivity(chosen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); } catch (Exception ignored) { } }); return true;
    }
    public static byte[] art(Context context, String id) {
        String source = artwork.get(id); if (source == null) return null;
        android.graphics.Bitmap bitmap = ResumeArtwork.load(context, source); if (bitmap == null) return null;
        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream(); bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, output); return output.toByteArray();
    }
    public static void configurationChanged(Context context, String apps, String options) {
        if (android.os.Build.VERSION.SDK_INT < 23) return;
        try {
            if (apps != null) {
                JSONArray order = new JSONArray();
                for (String pkg : apps.equalsIgnoreCase("auto") || apps.trim().isEmpty() ? Apps.LIST : apps.split(","))
                    if (!pkg.trim().isEmpty()) order.put(pkg.trim());
                context.getSharedPreferences("page", 0).edit().putString("tvhome.order.tv", new JSONObject()
                        .put("packages", order).put("line", Math.min(10, order.length())).put("pending", true).toString()).apply();
            }
            if (options != null) for (String pair : options.split(";")) {
                int at = pair.indexOf('='); if (at > 0) optionChanged(context, pair.substring(0, at).trim(), pair.substring(at + 1).trim());
            }
            context.getSharedPreferences("config", 0).edit().putLong("v3-config-revision", System.currentTimeMillis()).apply();
        } catch (Exception ignored) { }
    }
    public static void keep(Context context, String key, String value) {
        context.getSharedPreferences("page", 0).edit().putString(key, value).apply();
        if (!"tvhome.settings".equals(key)) return;
        try {
            JSONObject settings = new JSONObject(value).optJSONObject("s");
            if (settings == null) return;
            String clock = settings.optString("clock");
            if (clock.equals("12h") || clock.equals("24h")) context.getSharedPreferences("config", 0).edit().putString("o.clock", clock.equals("24h") ? "24" : "12").apply();
        } catch (Exception ignored) { }
    }
    public static void optionChanged(Context context, String name, String value) {
        if (!name.equals("clock") && !name.equals("weather") && !name.equals("location")) return;
        try {
            SharedPreferences page = context.getSharedPreferences("page", 0);
            JSONObject record = new JSONObject(page.getString("tvhome.settings", "{}"));
            JSONObject settings = record.optJSONObject("s"); if (settings == null) settings = new JSONObject();
            if (name.equals("clock")) settings.put("clock", value.equals("24") ? "24h" : "12h");
            if (name.equals("weather")) settings.put("wx_show", value.equals("on"));
            if (name.equals("location") && value.matches("-?[0-9.]+,-?[0-9.]+")) {
                String[] ll = value.split(","); settings.put("place", new JSONObject().put("lat", Double.parseDouble(ll[0])).put("lon", Double.parseDouble(ll[1])).put("name", "").put("timezone", TimeZone.getDefault().getID()));
            }
            page.edit().putString("tvhome.settings", record.put("s", settings).put("updated", System.currentTimeMillis()).put("pending", true).toString()).apply();
            context.getSharedPreferences("config", 0).edit().putLong("v3-config-revision", System.currentTimeMillis()).apply();
        } catch (Exception ignored) { }
    }
    private static Wallpaper wallpaper;
    public static void wallpaper(Context context, boolean foreground) {
        MAIN.post(() -> {
            if (wallpaper == null) wallpaper = new Wallpaper(context.getApplicationContext());
            wallpaper.setMode(context.getSharedPreferences("config", 0).getString("o.photos", Wallpaper.TIME));
            if (foreground) wallpaper.start(); else wallpaper.stop();
        });
    }
    public static void recovery(Context context) { HelperRecovery.start(context); }
}

package com.ansub.tvhome;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** Optional exact-title fallback. No viewing account or credentials are sent. */
final class ArtworkLookup {
    private static final ThreadPoolExecutor WORK = new ThreadPoolExecutor(1, 1, 20, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(16), r -> { Thread t = new Thread(r, "overlook-art-match"); t.setDaemon(true); return t; },
            new ThreadPoolExecutor.DiscardPolicy());
    static { WORK.allowCoreThreadTimeOut(true); }
    static String country(Context context) {
        String country = (android.os.Build.VERSION.SDK_INT >= 24 ? context.getResources().getConfiguration().getLocales().get(0) : context.getResources().getConfiguration().locale).getCountry();
        return country.matches("[A-Za-z]{2}") ? country.toUpperCase(Locale.ROOT) : "";
    }
    static void enqueue(Context context, PlaybackHistory.Entry entry) {
        WORK.execute(() -> {
            PlaybackHistory history = PlaybackHistory.get(context);
            String country = country(context);
            if (country.isEmpty() || !history.beginLookup(entry.id)) return;
            HttpsURLConnection connection = null;
            try {
                String query = "query($country: Country!, $language: Language!, $first: Int!, $filter: TitleFilter) { popularTitles(country: $country, first: $first, filter: $filter) { edges { node { content(country: $country, language: $language) { title originalReleaseYear posterUrl backdrops { backdropUrl } } } } } }";
                JSONObject variables = new JSONObject().put("country", country).put("language", "en").put("first", 12)
                        .put("filter", new JSONObject().put("searchQuery", entry.title));
                byte[] request = new JSONObject().put("query", query).put("variables", variables).toString().getBytes(StandardCharsets.UTF_8);
                connection = (HttpsURLConnection) new URL("https://apis.justwatch.com/graphql").openConnection();
                connection.setConnectTimeout(5000); connection.setReadTimeout(5000); connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST"); connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setFixedLengthStreamingMode(request.length);
                try (java.io.OutputStream output = connection.getOutputStream()) { output.write(request); }
                if (connection.getResponseCode() != 200) return;
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                try (InputStream input = connection.getInputStream()) {
                    byte[] buffer = new byte[4096]; int count;
                    while ((count = input.read(buffer)) != -1) {
                        if (bytes.size() + count > 512 * 1024) return;
                        bytes.write(buffer, 0, count);
                    }
                }
                JSONArray edges = new JSONObject(new String(bytes.toByteArray(), StandardCharsets.UTF_8))
                        .getJSONObject("data").getJSONObject("popularTitles").getJSONArray("edges");
                JSONObject match = null; int matches = 0;
                for (int i = 0; i < edges.length(); i++) {
                    JSONObject content = edges.getJSONObject(i).getJSONObject("node").getJSONObject("content");
                    if (!PlaybackRules.normalize(entry.title).equals(PlaybackRules.normalize(content.optString("title")))) continue;
                    if (entry.year > 0 && entry.year != content.optInt("originalReleaseYear")) continue;
                    match = content; matches++;
                }
                if (matches != 1) return;
                String path = null;
                JSONArray backdrops = match.optJSONArray("backdrops");
                if (backdrops != null && backdrops.length() > 0) path = backdrops.getJSONObject(0).optString("backdropUrl", null);
                if (path == null || path.isEmpty()) path = match.optString("posterUrl", null);
                if (path == null || !path.startsWith("/") || path.startsWith("//")) return;
                String artwork = "https://images.justwatch.com" + path.replace("{profile}", "s640").replace("{format}", "jpg");
                history.enrich(entry.id, entry.title, artwork, null, country);
            } catch (Exception ignored) { /* App artwork and native feed remain usable offline. */ }
            finally { if (connection != null) connection.disconnect(); }
        });
    }
}

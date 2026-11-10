package com.ansub.tvhome;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Conservative Android 9 media-session dump adapter for low-RAM TVs. */
final class PlaybackDump {
    static final class Session { String pkg, title; long position; boolean playing, active; }
    private static final Pattern STATE = Pattern.compile("state=(\\d+), position=(-?\\d+)");
    static List<Session> parse(String dump) {
        List<Session> sessions = new ArrayList<>(); Session current = null;
        for (String raw : dump.split("\\r?\\n")) {
            String line = raw.trim();
            if (line.startsWith("package=")) {
                if (current != null) add(sessions, current);
                current = new Session(); current.pkg = line.substring(8).trim();
            } else if (current != null && line.startsWith("active=")) current.active = line.equals("active=true");
            else if (current != null && line.startsWith("state=PlaybackState")) {
                Matcher match = STATE.matcher(line);
                if (match.find()) { try { current.playing = Integer.parseInt(match.group(1)) == 3; current.position = Long.parseLong(match.group(2)); } catch (NumberFormatException ignored) { } }
            } else if (current != null && line.startsWith("metadata:")) {
                int start = line.indexOf("description=");
                if (start >= 0) {
                    String description = line.substring(start + 12);
                    // Only strip explicit empty subtitle/description fields; commas in titles survive.
                    // Rich subtitle dumps are ambiguous and deliberately produce no guessed title.
                    if (description.endsWith(", null, null")) current.title = description.substring(0, description.length() - 12).trim();
                }
            }
        }
        if (current != null) add(sessions, current);
        return sessions;
    }
    private static void add(List<Session> result, Session session) {
        if (session.active && session.pkg != null && session.pkg.matches("[a-zA-Z0-9_.]+")
                && session.title != null && session.title.length() <= 512 && session.position >= 0 && result.size() < 16) result.add(session);
    }
}

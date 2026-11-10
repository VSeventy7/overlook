package com.ansub.tvhome;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;

final class PlaybackLinks {
    static Intent view(Context context, String pkg, String address) {
        if (address == null || address.isEmpty() || address.length() > 8192) return null;
        try {
            Intent supplied = Intent.parseUri(address, Intent.URI_INTENT_SCHEME);
            Uri data = supplied.getData();
            String scheme = data == null ? null : data.getScheme();
            if (scheme == null || "file".equalsIgnoreCase(scheme) || "content".equalsIgnoreCase(scheme)
                    || "javascript".equalsIgnoreCase(scheme) || "data".equalsIgnoreCase(scheme)) return null;
            Intent target = new Intent(Intent.ACTION_VIEW, data).setPackage(pkg).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (supplied.getComponent() != null && pkg.equals(supplied.getComponent().getPackageName()))
                target.setComponent(supplied.getComponent());
            ResolveInfo resolved = context.getPackageManager().resolveActivity(target, 0);
            return resolved != null && resolved.activityInfo != null && resolved.activityInfo.exported ? target : null;
        } catch (Exception ignored) { return null; }
    }
    static String image(String address) {
        if (address == null || address.length() > 8192) return null;
        try {
            String scheme = Uri.parse(address).getScheme();
            return "https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme)
                    || "content".equalsIgnoreCase(scheme) || "android.resource".equalsIgnoreCase(scheme) ? address : null;
        } catch (Exception ignored) { return null; }
    }
}

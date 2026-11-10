---
version: 1
slug: "v3-web-app-js"
primary_target: "v3/web/app.js"
related_targets: ["v3/android/app/src/main/java/dev/couchtv/ontv/HomeActivity.kt"]
---

# V3 Couch TV

Mode: Operate. Native Android TV with the upstream local web surface. Audience: owner using a directional remote from a couch. Copy the supplied Couch TV design and on-TV implementation; retain Overlook custom controls and legacy versions. No mock playback in the production build.

## Direction contract

THESIS: Actual Couch TV becomes a selectable v3, rather than a native approximation. The original app identity and classic fallbacks remain intact.

OWN-WORLD: Inherit the pinned upstream Night scenes, glass clock, landscape artwork, compact dock, Inter/Antonio fonts and full-screen settings. User-selected wallpaper options remain available. The bottom gradient preserves readability.

STORY: See the time/date, resume real available playback, launch configured apps and folders, then reach all native custom controls without losing remote focus.

FIRST VIEWPORT: Couch TV's scene fills the 16:9 TV. Its large scene clock and upper-right date/quick controls lead; Continue Watching and the app dock sit below. Add Refresh, Recent apps, Controls and v3 through the existing quick-control navigation.

FORM: Owner-pinned upstream reproduction, commit 1dbdcf195ad6ecb2e5e75337ee63cf6d263b88b6. Seed: supplied-couch-tv; no concept randomization for this explicit copy request. Code-led, as the owner previously requested editable HTML rather than generated images.

FINISH: unreviewed and undocumented is unfinished; this build ends with the finish review, the verdict, DESIGN.md, and every shipping raster carrying its provenance

Constraints: preserve signing key, app data, v1/v2, 30-day retention, original wallpaper/folders and global PatchWall shortcuts. No automatic grants, package changes or debugging activation. Local server loopback-only until explicit phone-remote activation. Netflix metadata remains service-dependent.

## Timeline

- 2026-11-10: Recorded the owner-selected upstream copy direction for v3; no new aesthetic choice is inferred.

# Playback history and helper recovery

Version 1.7.0 adds local playback tracking to the existing v1 and v2 Continue watching shelf. Both home layouts remain unchanged. It also packages the existing Android 9 recent-app helper for automatic recovery.

## Playback data

Android TV Watch Next remains the preferred source for app-provided resume links. An optional notification-listener service obtains Android media-session metadata without reading notification contents. It samples eligible video sessions every five seconds and records a title only after at least one minute of continuous playing, at least 45 seconds of genuine reported position advancement, and a playback position of at least one minute. Pauses, title changes, sampling gaps, rewinds and large seeks reset qualification. Short previews and frozen progress do not create records; longer previews with real playback metadata cannot reliably be distinguished from intentional playback.

Low-RAM Android 9 devices cannot bind notification listeners. The tested Mi TV reports `ro.config.low_ram=true` and ignores the notification-listener grant. On this firmware, the already authorized helper reads a fixed, bounded `dumpsys media_session` diagnostic instead. Only active video sessions with unambiguous titles and advancing raw progress are retained; rich/ambiguous descriptions and absent metadata are skipped. No notification grant is bypassed. This path cannot extract bitmap artwork or private account history; missing images use native feed matching or the approved exact-title lookup.

The private atomic history is limited to 100 records and 30 days. Expired records and private image files are removed when history is accessed. A dormant app cannot perform pruning until Android runs it again. Completed records are omitted from the shelf. The latest record for a given app/title is shown, and exact app/title matches enrich a native Watch Next card instead of duplicating it. Refresh reloads available provider and local data; it cannot force a streaming app to reveal private account history.

Use app-provided artwork first. If missing, qualified non-YouTube titles may be sent with the TV locale country to JustWatch's public HTTPS title search. Only a single exact normalized title match is accepted; ambiguous titles receive no guessed image. Requests are bounded, failures are retried no sooner than 24 hours, and the feed remains usable offline. This optional public endpoint can change. No API key or viewing-account credentials are used. Hold a card to see its data/artwork source, including JustWatch attribution.

Only app-pinned, exported playback URI targets are used. An owning app's current session activity is usable only while that same observed title remains active. Otherwise a card without a playback link explicitly says **Open app**. An app may still open its home screen; exact playback resume is not universal. Netflix, Prime and other apps appear only if they expose useful playback metadata or Watch Next rows. No retroactive account-history import is provided.

## Activation

Install as a signed update using the original key. Notification-listener access is a new owner-approved settings grant; it is not enabled by installation. Until granted, the existing Watch Next feed works normally. Revoking access stops session observation. No notification message callback is implemented.

On the current Android 9 TV, recovery connects only to `127.0.0.1:5555` using a new RSA key stored in Overlook's private files. It never copies the computer's ADB identity, enables debugging, disables packages or opens a listener. The existing accessibility service binding and home lifecycle start bounded retries. After a reboot, recovery depends on Android rebinding that enabled service and TCP debugging still being available.

A new TV key requires explicit owner approval and the TV's authorization prompt. Background retries send signatures only and never publish a new key/request a prompt. After approval, an authorized ADB shell can initiate the one-time prompt:

```sh
adb -s TV_IP:5555 shell content call --uri content://com.ansub.tvhome.recents --method authorize_recovery
```

The user must accept the TV prompt. Do not accept it unattended or transfer another device's private key. Status is available to the ADB shell and Overlook only:

```sh
adb -s TV_IP:5555 shell content call --uri content://com.ansub.tvhome.recents --method status
```

The embedded helper is uploaded through ADB sync to its fixed staging path, atomically replaced and started with a fixed command. No generic shell execution interface is exposed. The helper retains its existing UID checks and protected-task rules. A shell-owned file lock prevents concurrent helper instances, and startup allows an existing helper to reattach after an app update. Recovery is limited to Android 9/user 0; other versions require separate validation. The manual `recents-helper.sh` remains available. Revoking the TV key or turning off debugging makes automatic recovery unavailable without changing those settings back.

## Validation and rollback

Build: `JAVA_HOME=/path/to/jdk sh build.sh` (also embeds the helper). The original signing certificate must match the installed app. Pure Java checks in `tests/com/ansub/tvhome/PlaybackChecks.java` exercise sustained playback, frozen progress, seeks, sampling gaps, retention boundaries, corrupt ADB packets and RSA public-key encoding. Compile with PlaybackRules.java, PlaybackDump.java and AdbWire.java, then run `com.ansub.tvhome.PlaybackChecks`.

Local build, qualification/protocol checks and signature verification passed. A signed 1.7.0 update installed successfully on the Mi TV; the existing feed still renders thumbnails. A public exact-title search and its image returned successfully. The owner approved playback access and authorized the TV-private ADB identity. Notification access was attempted but remains unavailable on this low-RAM firmware. Private-key upload/start, fallback reader, automatic restart after stopping only Overlook’s helper, and duplicate-start rejection were verified on-device. The tested Netflix session initially reported position zero and later advancing progress, but exposed no title or artwork throughout; no record was fabricated. Actual qualified-session capture and reboot behavior remain pending validation; service coverage is not guaranteed.

Rollback: revoke the newly granted notification access or TV key if desired (owner-approved settings actions); the native Watch Next feed remains available. Preserve app data and the signing key. Do not uninstall or clear data as a rollback shortcut.

## References

- [Couch TV](https://github.com/Seagey/couch-tv): reference for combining observed playback with Watch Next and optional artwork lookup. No Couch TV installer or package-disable script was executed.
- [Android MediaSessionManager](https://developer.android.com/reference/android/media/session/MediaSessionManager#getActiveSessions(android.content.ComponentName))
- [Android notification listener](https://developer.android.com/reference/android/service/notification/NotificationListenerService)
- [JustWatch](https://www.justwatch.com/): matched artwork source.

## Timeline

- 2026-11-10 21:07 IST: Linked draft PR #2; verified helper recovery and singleton behavior. Made staging replacement resilient to interruption before final file permissions. Qualified playback and reboot validation remain pending.
- 2026-11-10 21:04 IST: Verified automatic recovery after helper stop and single-instance rejection; Netflix playback progressed without exposing title/artwork.
- 2026-11-10 21:02 IST: Added the low-RAM Android 9 fallback after the TV rejected notification access; verified private-key startup and recorded Netflix metadata limits. Actual qualified-session and reboot tests remain pending.
- 2026-11-10 20:54 IST: Implemented 30-day local tracking, exact-title artwork fallback and private-key Android 9 helper recovery; built and installed the signed update. New permission and recovery activation checks remain pending.

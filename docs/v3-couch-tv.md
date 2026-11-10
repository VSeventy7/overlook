# V3 — Couch TV inside Overlook

Version 1.8.0/code 29 copies Couch TV’s on-TV implementation and TV web interface into the existing Overlook app. V3 becomes the default once on update/new installation; later version selections are saved. The selector lists v3, v2, v1. Native v1/v2 remain available. V3 requires API23 or newer; the validated device is the existing Mi TV on Android 9/API28 with WebView138. Other firmware is not yet device-validated.

## What is copied

Pinned upstream: https://github.com/Seagey/couch-tv/commit/1dbdcf195ad6ecb2e5e75337ee63cf6d263b88b6

`vendor/couch-tv` retains the upstream TV home, on-TV Kotlin backend, phone remote, computer-bridge source and documentation, with its MIT license. V3 packages the Android on-TV backend, the complete TV web surface and built phone remote. Other TV platforms and computer-only Python tools are retained as source/reference; they are not Android runtime services. No upstream installer or cleanup script was run.

Adapted code lives under `v3/android` and `v3/web`, with the compatibility boundary in `src/com/ansub/tvhome/V3Host.java`. The original visual system includes rotating Night scenes, a large clock/date, artwork rail, dock, expanded app view, profiles, settings, weather and optional ambient/sound features. The on-TV backend supplies search, app launching, artwork lookup, playback observation and paired remote APIs. Account/API-key integrations remain unconfigured/off until the owner configures them; their presence in source is not a claim they were activated or tested.

## Preserved customizations

- Original package, signing certificate and app data; this is an in-place update.
- Classic v1 and Scenic v2, with persistent newest-first switching.
- Large time/date without an Overlook home wordmark. Hour-format preferences bridge to the native editor.
- Existing app order is seeded into v3. Existing folders are exposed through the native bridge; original folder/app management remains in Controls.
- Original wallpaper modes, favourites and personal-photo setup. In **Home settings → Look and scenes → Overlook wallpaper**, the cached/daily Overlook photo replaces the scene while retaining the large clock, date and dock. **Controls → Overlook settings and folders** reaches original wallpaper options.
- The stronger bottom gradient applies to all v3 looks.
- Refresh, Recent apps, Controls and the v3 version button are in the existing top-right quick-button navigation.
- Existing PatchWall short-press info overlay and hold-for-recents service, including Clear app/Clear all protections.

V3 has its own upstream Home settings. The dock’s gear opens Home settings or TV settings. Controls also exposes original native settings and Couch TV setup. No packages are automatically disabled, re-enabled, uninstalled or cleared by the integration; no new TV grants are automatically applied.

## Playback limitations

The native Watch Next feed and existing 30-day local history join Couch TV’s shelf. V3 caps native entries per app so one service does not consume the entire row. Native entries retain owning-app playback links; app-only local entries say Open app. Refresh reloads available local/provider data, not private service-account histories.

The copied backend also records titles opened through its search/phone flow and qualified playback it can identify. History remains local and is pruned to the last 30 days, bounded to 100 entries. No person names are invented when none are configured. App metadata, Watch Next contributions and deep links still vary by service. Netflix on this TV exposed progressing playback but no title/artwork and no readable Watch Next rows; copying Couch TV does not supply that missing account history. No universal streaming support or exact-resume guarantee is made.

## Local service and optional phone remote

The service binds only `127.0.0.1:5400` by default, verified on the TV. It reuses Overlook’s already authorized private on-TV RSA identity, rather than copying a computer key or asking to trust another identity. Background connection attempts fail without generating authorization dialogs. Existing accessibility/helper functionality stays intact.

For phone access, open **Controls → Couch TV setup / phone remote → Enable paired phone access** and confirm in the app. This deliberately changes the server from loopback to the local network; every phone still needs the displayed six-digit pairing code. Turn it off there to return to loopback. No internet forwarding is configured. LAN remote control, pairing and optional account integrations were not activated during validation.

Automatic upstream permission grants, wireless-debugging activation and launcher/package repair/takeover routines were removed or disabled. Existing TV permissions, disabled PatchWall state and default HOME component remain under the owner’s control. Boot and package-update startup is gated by selected v3; reboot behavior was not tested.

## Build and attribution

`JAVA_HOME=/path/to/jdk ANDROID_HOME=/path/to/sdk sh build.sh` builds the existing Java/helper intermediates, builds the pinned phone frontend when needed, and uses the Gradle8.11.1 wrapper with AGP8.7.3/Kotlin2.0.21 to package v1/v2/v3. Requirements: JDK17+, SDK34/build tools, Node/npm compatible with upstream Vite8, Python3 and network access for initial dependencies. Local verification used JDK21 and Node25.9.0. Output remains `out/tvhome.apk`, signed with the existing key. Never replace `key.jks` or uninstall to update.

`build-native.sh` is an intermediate/development Java build; use `build.sh` for the complete release. No private keys, TV screenshots, personal configuration or generated build/dependency directories enter Git.

The APK retains Couch TV’s MIT license, background-photo attribution/CC terms and Inter/Antonio OFL notices. All68 shipping TV/remote rasters retain their pinned upstream origin metadata (embedded where supported, with adjacent WebP sidecars); no visual image content was changed. `v3/licenses/asset-provenance.json` records original upstream hashes and annotated shipping hashes. Source code remains separate from adaptations; vendored raster files differ from upstream only by their origin metadata. Assets and notices are bundled alongside the page.

## Validation

- Complete Android build and release lint pass; phone frontend build, shell syntax, JS parse and diff checks pass.
- Existing qualification/retention/dump/ADB/RSA JVM checks pass.
- Signed update installed on the actual Mi TV, preserving certificate/data; local backend connected with the already trusted key.
- Actual TV captures verify v3 home, version selector, native Controls, original customization, Couch TV settings, Look settings and imported wallpaper.
- Directional navigation verified v3 → v1 → v2 → v3. Existing global recent-app helper remains connected.
- Independent finish review: **Ship** for the reviewed Android TV landscape presentation/discovery. Stronger gradient and imported-wallpaper preservation were resolved and confirmed.
- Universal streaming coverage, every possible folder setup, optional phone/account integrations, other firmware and reboot recovery are not implied by those UI checks.

Rollback is selecting v2 or v1 in the version menu. Keep app data and the signing key. Do not uninstall, clear data, revoke grants or change package state as a shortcut.

## Timeline

- 2026-11-10 23:02 IST: Integrated and validated the default Couch TV-based v3, preserved custom controls and legacy versions, and recorded attribution/limits.

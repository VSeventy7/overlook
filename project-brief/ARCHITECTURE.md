# Architecture

Status: Existing stack verified; current feature design follows the owner's approved tracking/recovery scope and online artwork choice. New TV permissions remain subject to explicit activation approval.

## Verified current system

Native Java Android app, compiled by build.sh using Android SDK 34 and installed as an update with the existing key.jks. The target TV runs Android 9. WatchNextShelf reads READ_TV_LISTINGS provider rows. InfoOverlayService is an enabled system-bound accessibility service for the PatchWall shortcuts. A shell-UID helper communicates through the DUMP-protected RecentAppsProvider and a caller-checked Binder.

## Current feature design

- Use a NotificationListenerService only to obtain media-session access. Observe video playback metadata, supplied artwork and owning-app session activities; do not read or store notification messages.
- On low-RAM Android 9 firmware where Android rejects notification listeners, use the authorized helper’s fixed bounded media-session diagnostic. Parse only unambiguous active video metadata; no permission bypass or fabricated titles. The tested Mi TV needs this fallback.
- Keep bounded, atomic local playback history for 30 days. A pure-Java qualification state machine requires sustained PLAYING state and genuine position advancement, rejecting previews and generic titles. Reuse the existing shelf and prefer real provider resume links when safely matched.
- Prefer app artwork. The owner approved online matching-artwork lookup when it is absent. Use exact-title, unambiguous matches, bounded HTTPS requests/cache and fail-open behavior; expose source attribution without adding home UI elements.
- Recover the existing helper through a small local ADB client on Android 9/port 5555. Store a new TV-only RSA identity in app-private storage. Do not copy the computer's key or auto-accept the TV trust dialog.
- Start bounded recovery from the existing accessibility service binding and home lifecycle. That system-bound service is reconnected at boot. Do not disable packages, turn debugging on, or open network listeners.
- Package the helper inside the signed app and execute only the fixed helper-start operation. Preserve UID checks, recent-task protections and the manual recovery script.

## Data and ownership

Only playback metadata and real available artwork are retained, locally, for 30 days; prune expired records and their artwork. No account credentials, complete subscription history or billing data. Lookup sends a title and country to the metadata provider only when artwork is missing and a qualified record needs enrichment. Device/private keys and screenshots never enter Git.

## Permissions and activation

Existing TV-listing and accessibility grants remain intact. The owner approved playback access and the new TV ADB identity. The TV key is trusted; low-RAM firmware rejected notification access, so the helper fallback is used. Any further new grant still requires owner approval. Until then, the Watch Next feed and manually started helper remain usable. Recovery must not raise authorization dialogs in unattended background retries.

## Validation and failure handling

Use JVM tests for qualification, normalization/merge and ADB protocol/key encoding. Build with the available JDK and SDK; preserve the installed certificate. Validate real-session data and helper restart on the TV; obtain permission for disruptive reboot testing. Report missing metadata/artwork/links honestly. An unavailable ADB port or revoked key results in bounded retries and explicit status, not a false ready state.

## Deferred architecture decisions

Other Android versions, TLS pairing, hosted services, imported account history and subscription renewals are outside this change. No external account/key provisioning is authorized by the artwork decision.

## Timeline

- 2026-11-10 21:02 IST: Verified the low-RAM notification restriction and private-key helper startup; added the narrow diagnostic fallback and single-instance recovery lock.
- 2026-11-10 20:54 IST: Recorded observed Java/Android/helper architecture and the approved 30-day/local-history and online-artwork choices. Planned standard media-session access and private local-ADB recovery with explicit owner activation.
- 2026-11-01: Created interview scaffold; project decisions were not yet approved.

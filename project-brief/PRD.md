# Product Requirements Document
Project folder: overlook.
Existing root documentation to inspect: `../DESIGN.md`, `../PRODUCT.md`, `../README.md`.

Status: V3 scope and Couch TV design explicitly approved by the owner; current implementation and TV checks are documented below. Earlier v1/v2 behavior remains preserved.

## Product overview

Overlook is an existing native Android TV launcher. It launches installed apps, displays shared Continue watching entries and provides PatchWall info/recent-app shortcuts. Preserve the existing implementation and configuration.

## Problem and target users

The owner uses a Mi TV running Android 9 and a directional remote. Shared Watch Next entries do not cover every streaming app, and the optional recent-app helper currently needs a computer to restart after a TV reboot.

## Goal

Broaden Continue watching using genuine playback observations and recover the existing helper on the TV. Success means verified history and recovery, without changing either home layout or inventing service data.

## Core features and acceptance criteria

- Combine observed playback with Watch Next; retain only the last 30 days locally on this TV (explicit owner choice).
- Qualify sustained playback with real position advancement; reject short autoplay previews, generic app-name titles and unavailable metadata.
- Use real supplied artwork and valid owning-app playback targets. Identify app-opening fallbacks honestly when a title-specific resume link is unavailable.
- Recover the helper after app/device restart when authorized local ADB is available; use bounded retries and a private TV identity, never a copied computer key.
- Preserve the signing identity, app data, current navigation and v1/v2 layouts. Capture and show actual TV screenshots after installed changes.
- Validate qualification, expiration, deduplication, failure states and helper restart on Android 9. Obtain any new TV authorization explicitly.

## Out of scope

No automatic package disabling, subscription-renewal tracker, account-history import or universal streaming-service promise. Couch TV’s UI is now explicitly approved for v3; v1/v2 retain their current layouts.

## Success metrics and priorities

Prioritize reliable data and recovery over new presentation. A record must be traceable to observed playback or a provider row. Missing metadata must remain missing rather than being guessed. Preserve classic v1 and Scenic v2 as fallbacks; Couch TV-based v3 is the new default.

## Interview questions

The current scope, existing behavior and deferred UI work are established by this conversation. The owner explicitly selected 30-day local retention. Any new feature or visual redesign needs its own scope.

## V3 — approved scope

Owner approved a full Couch TV-based v3 on 10 November 2026, preserving all Overlook customizations and v1/v2. V3 becomes the default, first in the version selector. Copy the upstream on-TV UI/backend with licensing; do not promise metadata absent from streaming apps.

## Timeline

- 2026-11-10 23:01 IST: Integrated and validated v3 on the existing Mi TV, with v1/v2 fallback, stronger gradient, imported wallpaper, original controls and no automatic TV grants/package changes.
- 2026-11-10 22:28 IST: Owner approved a full Couch TV-based v3 on 10 November 2026, preserving all Overlook customizations and v1/v2. V3 becomes the default, first in the version selector. Copy the upstream on-TV UI/backend with licensing; do not promise metadata absent from streaming apps.
- 2026-11-10 20:54 IST: Recorded the approved playback/recovery scope and explicit 30-day retention choice; preserved the existing UI and deferred future visual choices.
- 2026-11-01: Created interview scaffold; project decisions are not yet approved.

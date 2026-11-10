# Overlook

<!-- impeccable:product-schema 1 -->

## Platform

android

## Users

People using an Android TV or Google TV launcher with a directional remote from a couch.

## Product Purpose

Launch installed apps and resume shared viewing activity from one home screen.

## Operating Context

A native Java Android TV launcher installed on the user's TV. V3 Couch TV is the default after a one-time migration; v1 Classic home remains available. The user selected Scenic dock as the only v2 layout, with smaller elements and a wallpaper gradient that darkens toward the bottom. A persistent newest-first menu selects v3 Couch TV, v2 Scenic dock or v1 Classic home. V2 shows large time/date without visible Overlook branding. V3 runs the pinned upstream Couch TV web surface in its Android host, retaining Overlook integrations. See DESIGN.md for the versioned visual systems.

## Capabilities and Constraints

Preserve existing app order, folders, settings, signing identity and app data. Use generic Android TV / Google TV configuration. V2 reads searchable Watch Next rows with READ_TV_LISTINGS, opens owning-app playback links and displays provider artwork. Current device checks found playable YouTube, Crunchyroll and JioHotstar entries. Netflix and other installed apps may publish no readable entries; never fabricate service history or claim universal support. Text, remote navigation, fallback artwork, permission-denied and empty states must remain functional.

## Brand Commitments

Name: Overlook. The owner selected the full Couch TV design and on-TV implementation as v3; retain Apple TV–inspired v2 Scenic dock and classic v1.

## Evidence on Hand

The following compact Scenic dock notes are retained historical v2 evidence. Current v3 delivery evidence appears below.

Current native source, prior TV screenshots/runtime checks, shared thumbnails, app banners and fonts. The compact Scenic dock revision is documented from source; final render verification is pending. The intended new screenshot is .impeccable/review/tv-scenic-compact.png. Older three-layout screenshots do not verify this revision. Review screenshots contain user viewing data and are not for Git or publication. No invented titles, services, viewing histories or subscription data.

## Product Principles

- Resume actual shared content.
- Keep remote navigation predictable.
- Preserve a familiar classic fallback.
- Let content artwork lead without compromising readability.

## Remote review workflow

The user is away from the TV and uses a Tailscale ADB connection. After each installed UI change, capture the actual launcher screen (dismiss the screensaver using Home if needed), inspect it, and show the screenshot inline to the user. Do not substitute a screensaver capture for launcher evidence.

Latest v1 direction: retain classic home styling and add only one quiet Continue watching row. Avoid the v2 dock or extra styling in v1. V2 remains available.

Recent-apps direction: PatchWall hold opens a modern panel with directional-remote app selection, per-app Clear app controls and Clear all. An optional, local ADB-started helper uses actual Android 9 system dismissal controls and verifies task deletion. App data, Home, System UI and Tailscale remain protected. The helper must be restarted after a TV reboot. Short press retains the info overlay.

## V3 delivery and boundaries

V3 copies Couch TV at upstream commit `1dbdcf195ad6ecb2e5e75337ee63cf6d263b88b6`, preserving custom clock/date, wallpaper modes and selection, app order/folders, settings shortcuts, Refresh and PatchWall info/recents. The original signing identity and app data are preserved. V1 and v2 remain selectable; the default migration occurs once, rather than overriding later owner choices.

The embedded server binds loopback by default. Paired phone access over LAN is an explicit opt-in. V3 does not automatically grant TV permissions, change packages or enable debugging. Existing 30-day retention remains a constraint. Provider support depends on readable system metadata; Netflix still supplies no title/art on the tested TV.

Signed version 1.8.0 (code 29) was installed on the Mi TV (API 28, WebView 138). Actual-device screenshots verified the corrected scene home, custom wallpaper, upstream and Overlook settings, version menu and controls. Remote switching v3 → v1 → v2 → v3 was verified. Finish review disposition: SHIP for reviewed TV landscape presentation/discovery, after resolving gradient and wallpaper preservation. Earlier pending-verification notes above describe historical v2 work, not the current v3 evidence. These checks do not establish universal device/provider compatibility.

The 68 shipping TV/remote raster files embed origin metadata; the bundled asset-provenance manifest retains upstream/shipping hashes and MIT/OFL/photo CC notices remain packaged. Screenshots stay outside Git and must not be published because they contain owner data.

## Timeline

- 2026-11-10 23:01:45 Asia/Calcutta (UTC+0530): Recorded the delivered v3 default, retained versions, device evidence and operational boundaries.

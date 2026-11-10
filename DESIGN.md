---
name: Overlook v3 Couch TV with retained v1 and v2
description: Couch TV scene home with preserved Overlook controls and classic native versions
colors:
  v3-bg: "#0b0b0e"
  v3-bg-deep: "#060608"
  v3-text: "#f5f5f7"
  v3-text-secondary: "rgba(245, 245, 247, 0.62)"
  v3-tile: "#1c1c21"
  v3-warning: "#ff9f8a"
  v3-focus-ink: "#111"
  v3-night-glass: "rgba(30, 20, 28, 0.32)"
  artwork-fallback: "#20232B"
  white: "#FFFFFF"
  date: "rgba(255,255,255,0.851)"
  focus-tint: "rgba(42,48,61,0.098)"
  dock: "rgba(33,38,48,0.600)"
  wallpaper-clear: "rgba(10,13,18,0)"
  wallpaper-soft: "rgba(10,13,18,0.094)"
  wallpaper-mid: "rgba(10,13,18,0.439)"
  wallpaper-bottom: "rgba(10,13,18,0.902)"
typography:
  v3-scene-clock:
    fontFamily: "Antonio, Arial Narrow, sans-serif"
    fontSize: "25rem"
    fontWeight: 300
    lineHeight: 0.82
    letterSpacing: "-0.02em"
  v3-body:
    fontFamily: "Inter, system-ui, sans-serif"
    fontWeight: 450
    letterSpacing: "-0.005em"
  v3-card-title:
    fontFamily: "Inter, system-ui, sans-serif"
    fontSize: "1.8125rem"
    fontWeight: 600
    lineHeight: "2.25rem"
  display:
    fontFamily: "Inter SemiBold, MiLan Pro SemiBold, sans-serif-medium"
    fontSize: "82px"
    fontWeight: 600
  date:
    fontFamily: "Inter, MiLan Pro, sans-serif"
    fontSize: "27px"
    fontWeight: 400
  app-heading:
    fontFamily: "Inter SemiBold, MiLan Pro SemiBold, sans-serif-medium"
    fontSize: "27px"
    fontWeight: 600
  shelf-heading:
    fontFamily: "sans-serif"
    fontSize: "30px"
    fontWeight: 700
  card-title:
    fontFamily: "sans-serif"
    fontSize: "24px"
    fontWeight: 700
  service-detail:
    fontFamily: "sans-serif"
    fontSize: "18px"
    fontWeight: 400
rounded:
  v3-card: "1.875rem"
  v3-dock: "2.875rem"
  v3-quick: "2.75rem"
  v3-settings-row: "1.5rem"
  card: "16px"
  dock: "28px"
  header-control: "29px"
spacing:
  v3-safe-x: "5rem"
  v3-safe-y: "3.75rem"
  v3-gap: "2.5rem"
  v3-card-gap: "1.375rem"
  card-gap: "22px"
  safe-edge: "89px"
  header-top: "34px"
  control-gap: "28px"
components:
  v3-resume-card:
    backgroundColor: "{colors.v3-night-glass}"
    textColor: "{colors.v3-text}"
    rounded: "{rounded.v3-card}"
    padding: "1rem"
    width: "32.5rem"
  v3-quick-control:
    backgroundColor: "{colors.v3-night-glass}"
    textColor: "{colors.white}"
    rounded: "{rounded.v3-quick}"
    height: "5.5rem"
    padding: "0 1.75rem"
  watch-card:
    backgroundColor: "transparent"
    textColor: "{colors.white}"
    rounded: "{rounded.card}"
    padding: "7px 9px"
    width: "376px"
    height: "332px"
  header-control:
    rounded: "{rounded.header-control}"
    height: "58px"
  scenic-dock:
    backgroundColor: "{colors.dock}"
    rounded: "{rounded.dock}"
    height: "194px"
---

# Design System: Overlook v3 Couch TV; retained v1/v2

## Overview

### V3 default — Couch TV

**Creative North Star: "Couch TV Night scenes"** (the owner-selected upstream reproduction). V3 uses the actual pinned Couch TV web surface and Android host, preserving its scenic clock, landscape content, compact dock and full-screen settings. It becomes the default through a one-time migration; the newest-first selector retains v2 Scenic dock and v1 Classic home. All unprefixed frontmatter tokens and the legacy sections below remain v2 references; `v3-` tokens apply only to v3. This is a versioned extension, not a restyle of either native fallback.

V3's source of truth is `vendor/couch-tv/tv-home/web/app.css` at upstream commit `1dbdcf195ad6ecb2e5e75337ee63cf6d263b88b6`, with compatibility rules in `v3/web/overlook.css`, behavior in `v3/web/app.js` and native integration in `v3/android/app/src/main/java/dev/couchtv/ontv/HomeActivity.kt` and `src/com/ansub/tvhome/V3Host.java`.


The retained v2 direction is a compact Apple TV–inspired Scenic dock. Wallpaper leads the upper screen and darkens toward the bottom behind viewing content and apps. V2 contains this single layout, with large time/date and no visible Overlook branding. V1 remains classic and unchanged by this design revision.

**Key Characteristics:**
- Scenic wallpaper with a bottom-darkening veil.
- Compact artwork cards and centered app tiles.
- Large time/date and explicit remote focus.

## Colors

### V3 palette

V3 uses the upstream charcoal background, soft white text and muted secondary text. Warm translucent Night glass groups controls and dock; white focus with dark ink makes remote selection unmistakable. Warning coral belongs to status messages. The compatibility scrim applies across ordinary, Night and OLED themes: `linear-gradient(180deg,rgba(0,0,0,.22),transparent 24%,rgba(0,0,0,.38) 52%,rgba(0,0,0,.94) 100%)`. Preserve the selected image underneath this bottom-darkening layer.


White text and focused outlines sit over dark translucent surfaces. Artwork-fallback supports missing images, focus-tint lightly fills focused cards, and dock groups apps over wallpaper. The v2 wallpaper overlay runs top-to-bottom through wallpaper-clear, wallpaper-soft, wallpaper-mid and wallpaper-bottom, layered over the existing wallpaper shade.

## Typography

### V3 hierarchy

Inter is the variable UI face; Antonio provides the large scene clock. The frontmatter scene-clock values are the upstream baseline, with scene-specific size/position and owner settings overriding it. Date, clock style and user customization remain available. The Night shelf heading is 1.5625rem at 600; provider labels are 1.1875rem at 500 with 0.06em tracking; title and supporting copy remain legible from the couch. Do not apply v2's Android pixel scale to these browser rem values.


The header uses bundled Inter Regular/SemiBold, falling back to MiLan Pro and Android sans-serif. Watch Next uses Android sans-serif normal/bold. Frontmatter sizes are baseline design pixels, not Android sp. The clock uses tabular numerals and system 12/24-hour formatting; dates are localized.

Continue watching uses shelf-heading; Your apps uses app-heading. Card titles occupy two lines in a 62-high box without font padding, with end ellipsis. Service and resume detail are single-line ellipsized text. Permission/empty-state text is 22.

## Layout

### V3 TV surface

The upstream design uses a 1920×1080 reference: `html` font size is `100vw / 120`, giving 16px per rem at that width and proportionally scaled output in the TV's 960×540 CSS viewport. Night resume cards sit at 40rem from the top, with 10.5×5.9375rem landscape thumbnails. The centered dock sits 2.875rem above the bottom; its upstream grid uses twelve 7rem columns, 1.625rem column gaps and 1.375rem 1.75rem padding. Keep scene-specific date and clock placement and full-screen settings rather than translating this into a native approximation. Quick navigation exposes Refresh, Recent apps, Controls and the visible v3 version entry.


Native rendered pixels equal rounded baseline value × display width / 1920. There are no browser breakpoints. Time/date begin at the safe left edge and header top; controls sit upper right. Icon-only controls are 58 square with 30 icons. Text controls size to content with 22 horizontal padding.

Watch Next starts at y=340 and ends at y=800. Cards use frontmatter dimensions, padding and gap; artwork height is 198. Scroll padding is 8 horizontally and 10 vertically. Your apps sits at y=803. App tiles start at y=860; each tile width is 90% of its computed classic width, keeping the existing gaps, with the full row centered. Tile height remains 9/16 of width. The dock starts at y=840, extends 18 beyond the app row on each side, and uses the frontmatter height. This is tile-width reduction, not a 90%-viewport row.

## Elevation & Depth

### V3 glass and motion

Night panels use blur(1.75rem) saturate(170%) with subtle inset rims and diffuse shadows; optional Glass mode has its own upstream material and no-blur fallback. Resume focus scales 1.05 with a 0.1875rem white ring; Glass mode uses 1.03 and a slight upward lift. Dock focus uses upstream scale/lift, with a white ring. Quick controls invert to white/dark ink and scale 1.08. The shared easing is cubic-bezier(0.2,0.8,0.2,1). Preserve upstream lighter/old-engine fallbacks; Overlook's reduced-motion override reduces animations and transitions to .001ms. These web treatments do not replace the native v1/v2 focus values above.


The top-to-bottom wallpaper veil and translucent dock establish depth without a solid v2 backdrop. Header text retains black shadows. Focused viewing cards gain a white 4-pixel stroke, focus-tint fill, scale 1.035 and translationZ 10 over 180ms; system-disabled animation scale removes that animation. App focus retains scale 1.14, translationY −6 and translationZ 26 over 220ms. Header controls focus to white with dark foreground, scale 1.08 and translationZ 12 over 220ms. Values are baseline design pixels.

## Shapes

### V3 silhouettes

Rounded landscape thumbnails sit inside soft glass resume cards. Night app tiles are square with continuous corners where supported and a 22.5% radius fallback; dock and quick controls have generous pill silhouettes. Settings rows use the v3 settings-row radius. Preserve upstream scene and optional theme variants rather than imposing one radius on every version.


Viewing artwork is cropped inside rounded clipped containers. Owning-app fallback icons remain centered. Card and artwork corners share the card radius; the dock is softly rounded and header controls are pills.

## Components

### V3 integration

- **Scene home:** full-screen wallpaper modes, custom time/date and upstream clock options stay available; the owner's wallpaper persists across version changes.
- **Resume rail:** shows actual provider metadata and artwork when available; Refresh re-queries the native source. No synthetic streaming history or Netflix title/art claims.
- **App dock:** preserves owner order, folders and native app launches.
- **Settings and quick controls:** upstream full-screen settings coexist with Overlook Controls and settings shortcuts; the visible version control opens v3, v2, v1 newest-first.
- **Global PatchWall controls:** short press retains information; hold opens native recents. These native panels remain shared across versions.


- **Time/date:** prominent in v2, without a logo or greeting competing with the clock.
- **Viewing rail:** actual provider artwork, service, title and resume metadata. Up reaches header controls; Down reaches apps; first/last cards contain horizontal navigation. Clicking opens the owning-app playback link. Permission and empty states remain navigable.
- **Apps:** compact centered tiles preserve existing order, folders and controls, grouped by the translucent scenic dock.
- **Home screen & layout menu:** v2 Scenic dock first, then v1 Classic home, with a checkmark on the active version. There is no cinematic hero or alternate v2 layout selection.

## Do's and Don'ts

### V3 guardrails and verification

- **Do** keep the pinned upstream styling authoritative and compatibility changes narrow.
- **Do** preserve custom clock/date, wallpaper modes, order, folders, settings shortcuts, Refresh and PatchWall actions.
- **Do** retain the original signing identity, app data and working v1/v2 fallbacks.
- **Don't** infer universal provider metadata from a successful test on one service.
- **Don't** publish or commit screenshots containing the owner's viewing data.

The finish reviewer disposition is **SHIP** for the reviewed TV landscape presentation and control discovery. Actual Mi TV API 28 / WebView 138 evidence includes `tv-v3-corrected-ready-2026-11-10.png`, custom-wallpaper, Couch settings, Look settings, version-menu, Controls and custom-settings captures in the task's external `outputs` directory. The reviewer resolved gradient and wallpaper-preservation findings; remote switching v3 → v1 → v2 → v3 was verified. Signed 1.8.0 (code 29) was installed successfully. This evidence covers the reviewed device and states, not every firmware, theme, provider or remote.

All 68 shipping TV/remote raster files embed upstream origin records; `v3/licenses/asset-provenance.json` records upstream and shipping hashes. MIT, font OFL and photo CC notices ship with v3.


- **Do** preserve the time/date and bottom-darkened scenic wallpaper.
- **Do** keep card text and remote-focus outlines readable and unclipped.
- **Do** use real provider content and owning-app fallback icons.
- **Don't** add visible Overlook branding to v2 home.
- **Don't** restore the removed cinematic hero or artwork-first layout.
- **Don't** invent viewing history for providers without readable entries.

Source of truth: `src/com/ansub/tvhome/MainActivity.java` and `src/com/ansub/tvhome/WatchNextShelf.java`. This revision documents current source; final render verification is pending. The intended new review artifact is `.impeccable/review/tv-scenic-compact.png`; its capture has not been verified here. Older three-layout screenshots describe the previous build and are not evidence of this revision. Screenshot artifacts contain user viewing data and must not be committed to Git or published.

## Compact render review

The final 1.6.1 native source and screenshots `tv-scenic-compact.png` and `tv-scenic-compact-menu.png` were independently reviewed and accepted with no concrete findings. They show readable compact captions, the two-entry version menu and the bottom-darkening wallpaper. This is screenshot/source evidence from the connected TV, not a claim about all Android TV firmware.

## App dock heading

1.6.2 removes the Your apps label; the dock shows app icons without a section heading.

## V1 classic + resume (1.6.3)

V1 retains its original clock/date, app positions/sizes, controls, wallpaper treatment and focus behavior. The only new surface is a resume row at y420 in 1920×1080 design coordinates: 340×260 cards, 180px image, app name and one-line 22px title. No visible playback counters. V2 uses the same quiet captions with its existing Scenic styling. Actual-device screenshots are required after each installed UI change and must be shown to the user.

## Manual refresh (1.6.4)

A compact text-only Refresh button sits 24 design pixels after the Continue watching heading. It uses 20px text, 42px height, 140px minimum width and a 10px corner radius. Resting background is translucent black; focus is white with dark text. Refreshing text replaces Refresh while a query runs. Focus order is apps → cards → Refresh → controls. Existing cards remain visible while loading. Actual TV query/focus behavior and screenshot were checked.

## Recent-apps panel

The global recent-apps panel extends the native visual system without changing either home layout. A quiet navy-to-black background (#182230, #0E141E, #080C12), Inter Regular/SemiBold, 96px side margins, 60px title and 26px supporting text establish hierarchy. App banners occupy 364×220px tiles with 16px corners and 36px column gaps. Each app has an explicit 64px-high Clear app button; Clear all sits at the upper right. Focus uses a white outline on artwork and a white fill/dark text on buttons. A full-screen progress state covers the native system handoff. Empty and offline states retain a clear back/retry action and appropriate remote instructions.

## Timeline

- 2026-11-10 23:01:45 Asia/Calcutta (UTC+0530): Extended the design system for the reviewed v3 Couch TV default; preserved v1/v2 guidance and tokens.

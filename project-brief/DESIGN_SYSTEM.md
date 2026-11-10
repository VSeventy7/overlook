# Design System
Project folder: overlook.
Existing root documentation to inspect: `../DESIGN.md`, `../PRODUCT.md`, `../README.md`.

Status: V3 scope and Couch TV design explicitly approved by the owner; current implementation and TV checks are documented below. Earlier v1/v2 behavior remains preserved.

## UI applicability and reference designs

Native Android TV UI. Retain v1 Classic and v2 Scenic dock, the current Continue watching shelf, remote focus and recent-apps panel. See ../DESIGN.md for the existing implementation reference. Couch TV supplied the behavior reference for the earlier playback phase; its full design is now approved for v3.

## Brand and visual identity

Overlook remains the app/project name. Preserve existing branding decisions in v1/v2; v3 uses the owner-selected Couch TV visual system without an Overlook wordmark on the home screen.

## Colour tokens and typography

Observed baseline: bundled Inter Regular/SemiBold, Android sans-serif shelf labels, white text/focus, dark translucent surfaces and existing wallpaper gradients. Keep the current tokens; a broader design interview is deferred.

## Spacing and layout

Preserve the native TV layouts, sizes, safe edges, card spacing and app rows. V3 embeds the original TV web interface inside the same Android app; the native v1/v2 layouts are preserved.

## Components and loading/error/empty states

Reuse current cards and permission/loading/error/empty states. Real app artwork may replace an icon fallback when available. If only app opening is possible, the existing text/content description must make that limitation clear.

## Responsive behavior and accessibility

Keep directional navigation, stable focus, readable labels and content descriptions. Show real device screenshots after installed changes. New styling and component choices await the owner's next instructions.

## Interview questions

Deferred at the owner's explicit request: they will identify desired UI elements after the tracking/recovery work. This does not approve a new visual system.

## V3 — approved scope

The owner now explicitly selects Couch TV’s complete visual design for v3. Preserve upstream scenes, typography, app grid/dock, settings, focus and remote behavior; integrate Overlook clock/date, wallpaper choices and custom controls. Preserve v1/v2 rather than redesigning them.

## Timeline

- 2026-11-10 23:01 IST: Integrated and validated v3 on the existing Mi TV, with v1/v2 fallback, stronger gradient, imported wallpaper, original controls and no automatic TV grants/package changes.
- 2026-11-10 22:28 IST: The owner now explicitly selects Couch TV’s complete visual design for v3. Preserve upstream scenes, typography, app grid/dock, settings, focus and remote behavior; integrate Overlook clock/date, wallpaper choices and custom controls. Preserve v1/v2 rather than redesigning them.
- 2026-11-10 20:54 IST: Recorded the owner's decision to defer UI elements and preserve the current screens during the backend work; documented the observed baseline without inventing new visual approvals.
- 2026-11-01: Created interview scaffold; project decisions are not yet approved.

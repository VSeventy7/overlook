# Design System
Project folder: overlook.
Existing root documentation to inspect: `../DESIGN.md`, `../PRODUCT.md`, `../README.md`.

Status: The owner explicitly deferred new UI elements on 10 November 2026. This phase preserves the current UI; future visual choices remain pending.

## UI applicability and reference designs

Native Android TV UI. Retain v1 Classic and v2 Scenic dock, the current Continue watching shelf, remote focus and recent-apps panel. See ../DESIGN.md for the existing implementation reference. Couch TV is a behavior reference for this phase, not an approved replacement UI.

## Brand and visual identity

Overlook remains the app/project name. Preserve the existing screen branding decisions and hierarchy; do not add new Couch TV visual elements.

## Colour tokens and typography

Observed baseline: bundled Inter Regular/SemiBold, Android sans-serif shelf labels, white text/focus, dark translucent surfaces and existing wallpaper gradients. Keep the current tokens; a broader design interview is deferred.

## Spacing and layout

Preserve the native TV layouts, sizes, safe edges, card spacing and app rows. No web/mobile layout is introduced.

## Components and loading/error/empty states

Reuse current cards and permission/loading/error/empty states. Real app artwork may replace an icon fallback when available. If only app opening is possible, the existing text/content description must make that limitation clear.

## Responsive behavior and accessibility

Keep directional navigation, stable focus, readable labels and content descriptions. Show real device screenshots after installed changes. New styling and component choices await the owner's next instructions.

## Interview questions

Deferred at the owner's explicit request: they will identify desired UI elements after the tracking/recovery work. This does not approve a new visual system.

## Timeline

- 2026-11-10 20:54 IST: Recorded the owner's decision to defer UI elements and preserve the current screens during the backend work; documented the observed baseline without inventing new visual approvals.
- 2026-11-01: Created interview scaffold; project decisions are not yet approved.

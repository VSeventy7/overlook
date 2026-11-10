# Project Agent Instructions
Project folder: overlook.
Existing root documentation to inspect: `../DESIGN.md`, `../PRODUCT.md`, `../README.md`.

Status: V3 scope and Couch TV design explicitly approved by the owner; current implementation and TV checks are documented below. Earlier v1/v2 behavior remains preserved.

## Before work

- Read this file before project changes.
- Read PRD.md in this folder before product changes, DESIGN_SYSTEM.md before UI changes, and ARCHITECTURE.md before structural, dependency, service, or data changes. Paths in this instruction refer to project-brief/ in the repository root.
- Preserve existing repository instructions, framework-generated blocks, components and documentation. Inspect source before proposing new tools or architecture.
- Complete the interview in order: PRD -> DESIGN_SYSTEM -> ARCHITECTURE -> AGENTS. Ask questions for ONE document, wait for answers, draft it, then move to the next. Do not invent user decisions or treat a scaffold as an approved brief.
- This documentation rollout is explicitly authorized before the interviews. Subsequent implementation depending on unanswered decisions requires those decisions first.

## Change workflow

- Use the established Notion personal-project task board. Create or select a task before each logical change, keep its status and validation current, and link related PRs.
- Keep unrelated work in separate tasks and PRs. For authorized publication, push a feature branch and open a PR targeting the integration branch; never push directly to main.
- Explain purpose, behavior, implementation, validation, risks, and task reference in each PR. Do not mark a task Done before its required acceptance and merge conditions are met.
- Reuse existing code and the approved design system. Handle loading, error and empty states where applicable. Keep behavior accessible and responsive when there is a UI.
- Keep secrets out of source and documentation. Validate inputs and enforce permissions. Do not assume a particular framework, database, or service.
- Update the affected brief and append its Timeline when approved decisions change. Keep facts separate from proposals.

## Project commands

Observed commands on this Mac:

- `JAVA_HOME=/opt/homebrew/opt/openjdk@21 sh build.sh`
- `JAVA_HOME=/opt/homebrew/opt/openjdk@21 sh build-recents-helper.sh`
- Android platform-tools ADB under the installed Android SDK; preserve the original signing certificate for update installs.
- `sh -n` for changed shell scripts and `git diff --check`; add meaningful JVM/integration checks for new qualification and recovery logic.
- Use the existing Personal Projects / Upcoming Tasks Notion board and feature branches/PRs targeting the repository's actual default branch. Never push directly to main.
- Read-only device inspection and the authorized implementation/build work may continue. Ask before new TV settings grants, accepting a new debugging identity, package disabling/uninstallation, clearing app data or rebooting the TV. Existing consent is reusable for its actual scope.
- Preserve both home layouts, app data, remote behavior and signing identity; show actual TV screenshots after installed changes. No fake personal history or claims of universal streaming support.

## Interview questions — ask only after the first three documents

Current answers are established by the shared Notion instructions, existing verified workflow and this conversation. Routine Java implementation choices may follow the existing stack. New TV permissions are activation gates; new UI work needs explicit scope; the current Couch TV-based v3 is explicitly approved. Ask about materially new product/security decisions when needed, without repeating settled approvals.

## V3 — approved scope

The owner authorized copying Couch TV into v3 and making it the default. UI deferral applies only to the earlier playback work; the v3 design is now explicitly selected. Preserve all Overlook custom controls and versions, upstream license notices, signing key and data. Do not run upstream installers or automatic grant/package-change steps.

## Timeline

- 2026-11-10 23:01 IST: Integrated and validated v3 on the existing Mi TV, with v1/v2 fallback, stronger gradient, imported wallpaper, original controls and no automatic TV grants/package changes.
- 2026-11-10 22:28 IST: The owner authorized copying Couch TV into v3 and making it the default. UI deferral applies only to the earlier playback work; the v3 design is now explicitly selected. Preserve all Overlook custom controls and versions, upstream license notices, signing key and data. Do not run upstream installers or automatic grant/package-change steps.
- 2026-11-10 20:54 IST: Reconciled the current feature workflow with verified local commands, shared task/PR policy, and the owner’s TV permission, data-preservation and deferred-UI constraints.
- 2026-11-01: Created project agent workflow and interview scaffold.

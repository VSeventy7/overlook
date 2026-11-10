# Android TV / Google TV

One shared launcher for Android TV and Google TV devices. HDMI inputs appear when the device provides them; streaming sticks do not need a separate version.

Build with `sh build.sh` and install using `sh install.sh <TV_IP> devices/android-google-tv/home.conf`. Customize `home.conf` for your installed services. No personal name, location or device address is required.

Choose **v3 / v2 / v1** in the top-right controls. Your selection persists across restarts, and the original app setup is preserved when moving to v3. V3 is now the default; your later selection persists. V3 includes Couch TV’s own settings and retains original Overlook controls. See [v3](versions/v3/README.md).

- [v3 · Couch TV](versions/v3/README.md)
- [v1 · Classic home](versions/v1/README.md)
- [v2 · Continue watching](versions/v2/README.md)

Version folders document layouts and configuration. Native v1/v2 source remains in `src/`; v3 adaptations live in `v3/`, with attributed source under `vendor/couch-tv`. These layout names are separate from the APK's release version.

Device-specific cleanup commands are not universal. Inspect a device before disabling any packages. Historical device records are kept in [legacy](../legacy/README.md).

## Timeline

- 2026-11-10 23:02 IST: Added default v3 to the generic version documentation.
- 2026-11-10 23:01 IST: Updated generic configuration and newest-first selector for default v3.

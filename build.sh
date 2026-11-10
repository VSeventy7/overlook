#!/bin/sh
# Preserve the original Java build and signing identity, then package v3 alongside it.
set -eu
cd "$(dirname "$0")"
sh build-native.sh
if [ ! -f vendor/couch-tv/remote/dist/index.html ]; then
  (cd vendor/couch-tv/remote && npm ci --no-audit --no-fund && npm run build)
fi
ANDROID_HOME=${ANDROID_HOME:-$HOME/Library/Android/sdk}; export ANDROID_HOME
(cd v3/android && ./gradlew --no-daemon --console=plain assembleRelease)
BT=$(ls -d "$ANDROID_HOME"/build-tools/* | sort -V | tail -1)
"$BT/zipalign" -f 4 v3/android/app/build/outputs/apk/release/app-release-unsigned.apk out/v3-aligned.apk
"$BT/apksigner" sign --ks "${KEYSTORE:-key.jks}" --ks-pass "pass:${KEYSTORE_PASS:-tvhome123}" --out out/tvhome.apk out/v3-aligned.apk
echo "Built out/tvhome.apk with v1, v2 and v3"

#!/usr/bin/env bash
set -euo pipefail
destination=${1:?Use a private temporary output directory}
mkdir -p "$destination"
while read -r digest version; do
  jar="android-all-instrumented-$version.jar"
  output="$destination/$jar"
  if [[ -f "$output" ]] && printf '%s  %s\n' "$digest" "$output" | sha256sum --status -c; then
    continue
  fi
  for repository in https://maven-central.storage-download.googleapis.com/maven2 \
                    https://repo.maven.apache.org/maven2 https://repo1.maven.org/maven2; do
    if curl --fail --silent --show-error --retry 1 --connect-timeout 10 --max-time 120 \
        --output "$output.part" "$repository/org/robolectric/android-all-instrumented/$version/$jar" &&
        printf '%s  %s\n' "$digest" "$output.part" | sha256sum -c; then
      mv "$output.part" "$output"
      break
    fi
  done
  printf '%s  %s\n' "$digest" "$output" | sha256sum --status -c
done <<'MANIFEST'
dbb1c88987b54cef3132695a1b4d2f4851ba06286a6fa7d15772f98fd1f0cdf7 9-robolectric-4913185-2-i7
1c3fd182c03d47626686ca3b39c4ec5bdb0898b1056f4eb8f96f32c6209a5c04 13-robolectric-9030017-i7
3fdac2d463f431c6b8d83b9d0ca739e795de75fffedee648c6761bd4bd864701 14-robolectric-10818077-i7
33c2d7cc4186d4605269d5d411fa1818972c5b30a9f70bfbbd3d430b076af52f 15-robolectric-12650502-i7
MANIFEST

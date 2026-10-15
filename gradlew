#!/bin/sh
##############################################################################
#
#   Gradle start up script for POSIX generated for Tile Forge
#
##############################################################################

# Attempt to locate a working gradle executable
if command -v gradle >/dev/null 2>&1; then
    exec gradle "$@"
fi

# Fallback to gradle binary in standard locations
for g in /opt/gradle/bin/gradle /usr/local/bin/gradle /usr/bin/gradle; do
    if [ -x "$g" ]; then
        exec "$g" "$@"
    fi
done

echo "Error: 'gradle' was not found in your PATH." >&2
echo "Please install Gradle or set up a Gradle distribution to build the project." >&2
exit 1

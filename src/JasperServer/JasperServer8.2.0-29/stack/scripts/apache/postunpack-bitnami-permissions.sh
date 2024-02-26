#!/bin/bash
# Copyright VMware, Inc.
# SPDX-License-Identifier: APACHE-2.0

# shellcheck disable=SC1091

set -o errexit
set -o nounset
set -o pipefail
# set -o xtrace # Uncomment this line for debugging purposes

# Load libraries
. /opt/bitnami/scripts/libapache.sh
. /opt/bitnami/scripts/libfs.sh
. /opt/bitnami/scripts/libos.sh

# Load Apache environment
. /opt/bitnami/scripts/apache-env.sh

# Allow the "bitnami" user to write to commonly used Apache directories, for improved developer experience
if user_exists "bitnami"; then
    chown -R bitnami "$APACHE_HTDOCS_DIR" "$APACHE_CONF_DIR"
fi

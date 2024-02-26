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
. /opt/bitnami/scripts/libos.sh

# Load Apache environment
. /opt/bitnami/scripts/apache-env.sh

machine_size="$(get_machine_size "$@")"
ln -sf "memory/httpd-${machine_size}.conf" "${APACHE_CONF_DIR}/bitnami/httpd.conf"

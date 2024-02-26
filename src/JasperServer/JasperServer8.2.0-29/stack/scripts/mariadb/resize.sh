#!/bin/bash
# Copyright VMware, Inc.
# SPDX-License-Identifier: APACHE-2.0

# shellcheck disable=SC1091

set -o errexit
set -o nounset
set -o pipefail
# set -o xtrace # Uncomment this line for debugging purposes

# Load libraries
. /opt/bitnami/scripts/libmariadb.sh
. /opt/bitnami/scripts/libos.sh
. /opt/bitnami/scripts/libservice.sh
. /opt/bitnami/scripts/libversion.sh

# Load MariaDB environment variables
. /opt/bitnami/scripts/mariadb-env.sh

machine_size="$(get_machine_size "$@")"
ln -sf "memory/my-${machine_size}.conf" "${DB_CONF_DIR}/bitnami/memory.conf"

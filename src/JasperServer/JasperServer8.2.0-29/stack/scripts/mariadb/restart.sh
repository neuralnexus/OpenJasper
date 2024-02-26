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

# Load MariaDB environment variables
. /opt/bitnami/scripts/mariadb-env.sh

/opt/bitnami/scripts/mariadb/stop.sh
/opt/bitnami/scripts/mariadb/start.sh

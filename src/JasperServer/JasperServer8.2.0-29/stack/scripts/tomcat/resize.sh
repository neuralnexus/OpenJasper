#!/bin/bash
# Copyright VMware, Inc.
# SPDX-License-Identifier: APACHE-2.0

# shellcheck disable=SC1091

set -o errexit
set -o nounset
set -o pipefail
# set -o xtrace # Uncomment this line for debugging purposes

# Load libraries
. /opt/bitnami/scripts/libtomcat.sh
. /opt/bitnami/scripts/libos.sh

# Load Tomcat environment
. /opt/bitnami/scripts/tomcat-env.sh

machine_size="$(get_machine_size "$@")"
ln -sf "memory-${machine_size}.sh" "${TOMCAT_CONF_DIR}/bitnami/memory.sh"

#!/bin/bash
# Copyright VMware, Inc.
# SPDX-License-Identifier: APACHE-2.0
#
# Bitnami installation configuration

export BITNAMI_METADATA_DIR="/opt/bitnami/var"
export BITNAMI_SERVICE_MANAGER="monit"

# Load environment variables specified in user-data
# shellcheck disable=SC1090,SC1091
[[ ! -f "${BITNAMI_METADATA_DIR}/user-data-env.sh" ]] || . "${BITNAMI_METADATA_DIR}/user-data-env.sh"

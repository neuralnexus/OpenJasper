#!/bin/bash
# Copyright VMware, Inc.
# SPDX-License-Identifier: APACHE-2.0

# shellcheck disable=SC1090,SC1091

set -o errexit
set -o nounset
set -o pipefail
# set -o xtrace # Uncomment this line for debugging purposes

# Load JasperReports environment
. /opt/bitnami/scripts/jasperreports-env.sh

# Load MySQL Client environment for 'mysql_remote_execute' (after 'jasperreports-env.sh' so that MODULE is not set to a wrong value)
if [[ -f /opt/bitnami/scripts/mysql-client-env.sh ]]; then
    . /opt/bitnami/scripts/mysql-client-env.sh
elif [[ -f /opt/bitnami/scripts/mysql-env.sh ]]; then
    . /opt/bitnami/scripts/mysql-env.sh
elif [[ -f /opt/bitnami/scripts/mariadb-env.sh ]]; then
    . /opt/bitnami/scripts/mariadb-env.sh
fi

# Load PostgreSQL client environment for 'postgresql_remote_execute' (after 'jasperreports-env.sh' so that MODULE is not set to a wrong value)
if [[ -f /opt/bitnami/scripts/postgresql-client-env.sh ]]; then
    . /opt/bitnami/scripts/postgresql-client-env.sh
elif [[ -f /opt/bitnami/scripts/postgresql-env.sh ]]; then
    . /opt/bitnami/scripts/postgresql-env.sh
fi

# Load libraries
. /opt/bitnami/scripts/libjasperreports.sh

# Ensure JasperReports environment variables are valid
jasperreports_validate
# Load additional libraries
# shellcheck disable=SC1090,SC1091
. /opt/bitnami/scripts/libwebserver.sh

# Load web server environment for web_server_* functions
. "/opt/bitnami/scripts/$(web_server_type)-env.sh"

# Ensure JasperReports is initialized
jasperreports_initialize

# Update web server configuration with runtime environment
web_server_update_app_configuration "jasperreports"

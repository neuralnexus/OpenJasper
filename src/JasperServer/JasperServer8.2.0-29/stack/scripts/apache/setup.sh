#!/bin/bash
# Copyright VMware, Inc.
# SPDX-License-Identifier: APACHE-2.0

# shellcheck disable=SC1091

set -o errexit
set -o nounset
set -o pipefail
# set -o xtrace # Uncomment this line for debugging purposes

# Load libraries
. /opt/bitnami/scripts/liblog.sh
. /opt/bitnami/scripts/libapache.sh

# Load Apache environment
. /opt/bitnami/scripts/apache-env.sh

# Ensure Apache environment variables are valid
apache_validate

# Ensure Apache daemon user exists when running as 'root'
am_i_root && ensure_user_exists "$APACHE_DAEMON_USER" --group "$APACHE_DAEMON_GROUP"

# Generate SSL certs (without a passphrase)
ensure_dir_exists "${APACHE_CONF_DIR}/bitnami/certs"
if [[ ! -f "${APACHE_CONF_DIR}/bitnami/certs/server.crt" ]]; then
    info "Generating sample certificates"
    SSL_KEY_FILE="${APACHE_CONF_DIR}/bitnami/certs/server.key"
    SSL_CERT_FILE="${APACHE_CONF_DIR}/bitnami/certs/server.crt"
    SSL_CSR_FILE="${APACHE_CONF_DIR}/bitnami/certs/server.csr"
    SSL_SUBJ="/CN=example.com"
    SSL_EXT="subjectAltName=DNS:example.com,DNS:www.example.com,IP:127.0.0.1"
    rm -f "$SSL_KEY_FILE" "$SSL_CERT_FILE"
    openssl genrsa -out "$SSL_KEY_FILE" 4096
    # OpenSSL version 1.0.x does not use the same parameters as OpenSSL >= 1.1.x
    if [[ "$(openssl version | grep -oE "[0-9]+\.[0-9]+")" == "1.0" ]]; then
        openssl req -new -sha256 -out "$SSL_CSR_FILE" -key "$SSL_KEY_FILE" -nodes -subj "$SSL_SUBJ"
    else
        openssl req -new -sha256 -out "$SSL_CSR_FILE" -key "$SSL_KEY_FILE" -nodes -subj "$SSL_SUBJ" -addext "$SSL_EXT"
    fi
    openssl x509 -req -sha256 -in "$SSL_CSR_FILE" -signkey "$SSL_KEY_FILE" -out "$SSL_CERT_FILE" -days 1825 -extfile <(echo -n "$SSL_EXT")
    rm -f "$SSL_CSR_FILE"
fi
# Load SSL configuration
if [[ -f "${APACHE_CONF_DIR}/bitnami/bitnami.conf" ]] && [[ -f "${APACHE_CONF_DIR}/bitnami/bitnami-ssl.conf" ]]; then
    ensure_apache_configuration_exists "Include \"${APACHE_CONF_DIR}/bitnami/bitnami-ssl.conf\"" "bitnami-ssl\.conf" "${APACHE_CONF_DIR}/bitnami/bitnami.conf"
fi

# Update ports in configuration
[[ -n "$APACHE_HTTP_PORT_NUMBER" ]] && info "Configuring the HTTP port" && apache_configure_http_port "$APACHE_HTTP_PORT_NUMBER"
[[ -n "$APACHE_HTTPS_PORT_NUMBER" ]] && info "Configuring the HTTPS port" && apache_configure_https_port "$APACHE_HTTPS_PORT_NUMBER"

# Configure ServerTokens with user values
[[ -n "$APACHE_SERVER_TOKENS" ]] && info "Configuring Apache ServerTokens directive" && apache_configure_server_tokens "$APACHE_SERVER_TOKENS"

# Avoid exit code of previous commands to affect the result of this script
true

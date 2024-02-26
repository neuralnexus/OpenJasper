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
. /opt/bitnami/scripts/liblog.sh

########################
# Sets up the default Bitnami configuration
# Globals:
#   APACHE_*
# Arguments:
#   None
# Returns:
#   None
#########################
apache_setup_bitnami_config() {
    local template_dir="${BITNAMI_ROOT_DIR}/scripts/apache/bitnami-templates"

    # Enable Apache modules
    local -a modules_to_enable=(
        "deflate_module"
        "negotiation_module"
        "proxy[^\s]*_module"
        "rewrite_module"
        "slotmem_shm_module"
        "socache_shmcb_module"
        "ssl_module"
        "status_module"
        "version_module"
    )
    for module in "${modules_to_enable[@]}"; do
        apache_enable_module "$module"
    done

    # Disable Apache modules
    local -a modules_to_disable=(
        "http2_module"
        "proxy_hcheck_module"
        "proxy_html_module"
        "proxy_http2_module"
    )
    for module in "${modules_to_disable[@]}"; do
        apache_disable_module "$module"
    done

    # Bitnami customizations
    ensure_dir_exists "${APACHE_CONF_DIR}/bitnami"
    render-template "${template_dir}/bitnami.conf.tpl" > "${APACHE_CONF_DIR}/bitnami/bitnami.conf"
    render-template "${template_dir}/bitnami-ssl.conf.tpl" > "${APACHE_CONF_DIR}/bitnami/bitnami-ssl.conf"

    # Add new configuration only once, to avoid a second postunpack run breaking Apache
    local apache_conf_add
    apache_conf_add="$(cat <<EOF
Include "${APACHE_CONF_DIR}/extra/httpd-default.conf"
PidFile "${APACHE_PID_FILE}"
TraceEnable Off
ServerTokens ${APACHE_SERVER_TOKENS}
Include "${APACHE_CONF_DIR}/deflate.conf"
IncludeOptional "${APACHE_VHOSTS_DIR}/*.conf"
Include "${APACHE_CONF_DIR}/bitnami/bitnami.conf"
EOF
)"
    ensure_apache_configuration_exists "$apache_conf_add" "${APACHE_CONF_DIR}/bitnami/bitnami.conf"

    # Configure the default ports since the container is non root by default
    apache_configure_http_port "$APACHE_DEFAULT_HTTP_PORT_NUMBER"
    apache_configure_https_port "$APACHE_DEFAULT_HTTPS_PORT_NUMBER"

    # Patch the HTTPoxy vulnerability - see: https://docs.bitnami.com/general/security/security-2016-07-18/
    apache_patch_httpoxy_vulnerability

    # Remove unnecessary directories that come with the tarball
    rm -rf "${BITNAMI_ROOT_DIR}/certs" "${BITNAMI_ROOT_DIR}/conf"
}

########################
# Patches the HTTPoxy vulnerability - see: https://docs.bitnami.com/general/security/security-2016-07-18/
# Globals:
#   APACHE_CONF_FILE
# Arguments:
#   None
# Returns:
#   None
#########################
apache_patch_httpoxy_vulnerability() {
    # Apache HTTPD includes the HTTPoxy fix since 2016, so we only add it if not present
    if ! grep -q "RequestHeader unset Proxy" "$APACHE_CONF_FILE"; then
        cat >>"$APACHE_CONF_FILE" <<EOF
<IfModule mod_headers.c>
  RequestHeader unset Proxy
</IfModule>
EOF
    fi
}

# Load Apache environment
. /opt/bitnami/scripts/apache-env.sh

apache_setup_bitnami_config

# Ensure non-root user has write permissions on a set of directories
for dir in "$APACHE_TMP_DIR" "$APACHE_CONF_DIR" "$APACHE_LOGS_DIR" "$APACHE_VHOSTS_DIR" "$APACHE_HTACCESS_DIR" "$APACHE_HTDOCS_DIR"; do
    ensure_dir_exists "$dir"
    chmod -R g+rwX "$dir"
done

# Create 'apache2' symlink pointing to the 'apache' directory, for compatibility with Bitnami Docs guides
ln -sf apache "${BITNAMI_ROOT_DIR}/apache2"

# Load additional required libraries
# shellcheck disable=SC1091
. /opt/bitnami/scripts/libos.sh
. /opt/bitnami/scripts/libservice.sh

info "Creating Apache daemon user"
ensure_user_exists "$APACHE_DAEMON_USER" --group "$APACHE_DAEMON_GROUP"

# Enable extra service management configuration
if [[ "$BITNAMI_SERVICE_MANAGER" = "monit" ]]; then
    generate_monit_conf "apache" "$APACHE_PID_FILE" /opt/bitnami/scripts/apache/start.sh /opt/bitnami/scripts/apache/stop.sh
elif [[ "$BITNAMI_SERVICE_MANAGER" = "systemd" ]]; then
    generate_systemd_conf "apache" \
        --name "Apache HTTP Server" \
        --exec-start "${APACHE_BIN_DIR}/httpd -f ${APACHE_CONF_FILE}" \
        --exec-reload "${APACHE_BIN_DIR}/apachectl -k graceful" \
        --pid-file "$APACHE_PID_FILE"
else
    error "Unsupported service manager ${BITNAMI_SERVICE_MANAGER}"
    exit 1
fi
generate_logrotate_conf "apache" "${APACHE_LOGS_DIR}/*log"

# Create configuration files for setting Apache optimization parameters depending on the instance size
# Default to micro configuration until a resize is performed
ensure_dir_exists "${APACHE_CONF_DIR}/bitnami/memory"
ln -sf "memory/httpd-micro.conf" "${APACHE_CONF_DIR}/bitnami/httpd.conf"
ensure_apache_configuration_exists "# Memory settings
Include \"${APACHE_CONF_DIR}/bitnami/httpd.conf\""

# Enable mpm_event worker for improved performance
apache_enable_module mpm_event_module
apache_disable_module mpm_prefork_module

# By default we enable HTTP2 module when enabling also mpm_event
apache_enable_module http2_module

# Enable landing page, if it exists
if [[ -f "${APACHE_HTDOCS_DIR}/landing-page.html" ]]; then
    mv "${APACHE_HTDOCS_DIR}/landing-page.html" "${APACHE_HTDOCS_DIR}/index.html"
fi

# Allow the "bitnami" user to write to commonly used Apache directories, for improved developer experience
if user_exists "bitnami"; then
    chown -R bitnami "$APACHE_HTDOCS_DIR" "$APACHE_CONF_DIR"
fi

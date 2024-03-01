package com.jaspersoft.jasperserver.api.logging.audit.domain;

public enum AuditEventType {
    LOGIN("login"),
    LOGOUT("logout"),

    RUN_REPORT("runReport"),

    SCHEDULE_REPORT("scheduleReport"),
    DELETE_REPORT_SCHEDULING("deleteReportScheduling"),
    UPDATE_REPORT_SCHEDULING("deleteReportScheduling"),
    PAUSE_REPORT_SCHEDULING("pauseReportScheduling"),
    RESUME_REPORT_SCHEDULING("resumeReportScheduling"),

    CREATE_ROLE("createRole"),
    UPDATE_ROLE("updateRole"),
    DELETE_ROLE("deleteRole"),
    DELETE_ROLES("deleteRoles"),

    CREATE_USER("createUser"),
    UPDATE_USER("updateUser"),
    ENABLE_ALL_USERS("enableAllUsers"),
    DISABLE_ALL_USERS("enableAllUsers"),
    DELETE_USER("deleteUser"),
    DELETE_USERS("deleteUsers"),
    SWITCH_USER("switchUser"),
    EXIT_SWITCHED_USER("exitSwitchedUser"),

    CREATE_ORG("createOrganization"),
    UPDATE_ORG("updateOrganization"),
    DELETE_ORG("deleteOrganization"),
    DELETE_ORGS("deleteOrganizations"),

    SAVE_RESOURCE("saveResource"),
    UPDATE_RESOURCE("updateResource"),
    OVERWRITE_RESOURCE("overwriteResource"),
    DELETE_RESOURCE("deleteResource"),
    COPY_RESOURCE("copyResource"),
    MOVE_RESOURCE("moveResource"),
    ACCESS_RESOURCE("accessResource"),

    CREATE_FOLDER("createFolder"),
    UPDATE_FOLDER("updateFolder"),
    DELETE_FOLDER("deleteFolder"),
    COPY_FOLDER("copyFolder"),
    MOVE_FOLDER("moveFolder"),

    CREATE_PERMISSION("createPermission"),
    UPDATE_PERMISSION("updatePermission"),
    DELETE_PERMISSION("deletePermission"),

    CREATE_ATTR("createAttribute"),
    UPDATE_ATTR("updateAttribute"),
    DELETE_ATTR("deleteAttribute"),

    INPUT_CONTROLS_QUERY("inputControlsQuery"),
    QUERY_EXECUTING("adhocQueryExecuting"),

    AD_HOC_EDITING("adHocEditing"),
    UPDATE_AD_HOC_OPTIONS("updateAdhocOptions"),
    CLEAR_AD_HOC_CACHE_ENTRY("clearAdhocCacheEntry"),

    DASHBOARD_EDITING("dashboardEditing"),

    OLAP_VIEW("olapView"),

    UPDATE_ANALYSIS_OPTIONS("updateAnalysisOptions"),

    EXPORT("export"),
    IMPORT("import"),

    AWS_CONF_SETTINGS_UPDATE("awsConfigurationSettingsUpdate");

    private String eventTypeName;

    AuditEventType(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    @Override
    public String toString() {
        return eventTypeName;
    }
}

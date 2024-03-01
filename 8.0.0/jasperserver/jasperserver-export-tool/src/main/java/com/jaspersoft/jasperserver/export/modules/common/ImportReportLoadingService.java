package com.jaspersoft.jasperserver.export.modules.common;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.ReportLoadingService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControlsContainer;

import java.util.List;

public class ImportReportLoadingService extends ReportLoadingService {
    public List<InputControl> getInputControls(final ExecutionContext context, InputControlsContainer container) {
        return getInputControls(context, container, null);
    }
}

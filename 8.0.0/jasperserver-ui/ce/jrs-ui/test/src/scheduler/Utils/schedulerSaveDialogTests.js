import $ from 'jquery';
import sinon from 'sinon';
import schedulerSaveDialog from 'src/scheduler/util/schedulerSaveDialog';
import schedulerUtils from 'src/scheduler/util/schedulerUtils';

describe('Dashboard confirmation dialog', function () {
    const paramsMap = {
            'reportUnitURI': '%2Fpublic%2FSamples%2FReports%2F01._Geographic_Results_by_Segment_Report',
            'resourceType': "DashboardModelResource",
            'parentDashboardUnitURI': '%2Fpublic%2FSamples%2FReports%2F01._Geographic_Results_by_Segment_Report',
            'decorate': 'no',
            'schedulerAccelerator': 'schedule'
        },
        save = sinon.spy();
    it('should render dialog box', function () {
        let saveDialog = $('<div id="saveConfirmation"></div>');
        schedulerSaveDialog.saveDialogConfirmationBox();
        expect(saveDialog).toHaveId('saveConfirmation');
    });
    it('should open overlay if previous state is false', function () {
        let previousState = false;
        let paramsMappingStub = sinon.stub(schedulerUtils, '_ParamMapping');
        schedulerSaveDialog.scheduleDialogBox(previousState, paramsMap);
        expect(paramsMappingStub).toHaveBeenCalledWith(paramsMap);
        paramsMappingStub.restore();
    });
    it('should dialog pop up on dashboard/report state change', function () {
        let previousState = true;
        let saveScheduleDialogSpy = sinon.spy(schedulerSaveDialog, 'saveScheduleDialog');
        let saveDialogConfirmationBoxSpy = sinon.spy(schedulerSaveDialog, 'saveDialogConfirmationBox');
        schedulerSaveDialog.scheduleDialogBox(previousState, paramsMap, save);
        expect(saveScheduleDialogSpy).toHaveBeenCalledWith(paramsMap);
        expect(saveDialogConfirmationBoxSpy).toHaveBeenCalled();
        saveScheduleDialogSpy.restore();
        saveDialogConfirmationBoxSpy.restore();
    });
    it('should trigger cancel btn on dialog cancel click', function () {
        const dashDesigner = '<div id="dashboard"><div id="frame"><div class="content"></div></div></div>';
        $('body').append(dashDesigner);
        let previousState = true;
        schedulerSaveDialog.scheduleDialogBox(previousState, paramsMap, save);
        $('#saveConfirmationCancel').trigger('click');
        expect($('#saveConfirmation').length).toEqual(0);
    });
    it('should trigger ignore btn on dialog ignore click', function () {
        const dashDesigner = '<div id="dashboard"><div id="frame"><div class="content"></div></div></div>';
        $('body').append(dashDesigner);
        let previousState = true;
        let paramsMappingStub = sinon.stub(schedulerUtils, '_ParamMapping');
        schedulerSaveDialog.scheduleDialogBox(previousState, paramsMap, save);
        $('#saveConfirmationIgnore').trigger('click');
        $("#saveConfirmation").hide();
        expect($('#saveConfirmationIgnore').length).toEqual(0);
        expect(paramsMappingStub).toHaveBeenCalled();
        paramsMappingStub.restore();
    });
    it('should trigger save btn on save dialog click', function () {
        const dashDesigner = '<div id="dashboard"><div id="frame"><div class="content"></div></div></div>';
        $('body').append(dashDesigner);
        let previousState = true;
        let paramsMappingStub = sinon.stub(schedulerUtils, '_ParamMapping');
        schedulerSaveDialog.scheduleDialogBox(previousState, paramsMap, save);
        $('#saveConfirmationOK').trigger('click');
        expect(paramsMappingStub).toHaveBeenCalled();
        expect(save).toHaveBeenCalled();
        paramsMappingStub.restore();
    });
    it('should display correct text for report', function () {
        paramsMap.resourceType = "ReportUnit";
        let saveDialog = $('<div id="saveConfirmation"></div>');
        schedulerSaveDialog.saveScheduleDialog(paramsMap);
        expect(saveDialog).toHaveAttr('id');
    });
});
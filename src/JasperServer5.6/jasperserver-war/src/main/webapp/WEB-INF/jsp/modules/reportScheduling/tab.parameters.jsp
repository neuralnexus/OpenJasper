<js:parametersForm reportName="" renderJsp="${controlsDisplayForm}" />

<!-- The next block needed just to create standart for Scheduler behavior, when we are switching to the
tab where the error happened. To indicate what on this tab an error happened we introduce dummy control, which may have
special error class. The warning element is also dummy -->
<label class="hidden">
    <input type="text" name="parametersErrorNotifierStub" class="" />
    <span class="message warning" data-field="parametersErrorNotifierStub"></span>
</label>

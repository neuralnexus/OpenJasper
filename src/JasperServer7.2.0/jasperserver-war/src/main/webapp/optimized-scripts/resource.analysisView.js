var resourceAnalysisView={LABEL_ID:"labelID",RESOURCE_ID_ID:"nameID",DESCRIPTION_ID:"addFileInputDescription",NEXT_BUTTON_ID:"next",_canGenerateId:!0,initialize:function(e){this._form=$(document.body).select("form")[0],this._label=$(this.LABEL_ID),this._resourceId=$(this.RESOURCE_ID_ID),this._description=$(this.DESCRIPTION_ID),this._nextButton=$(this.NEXT_BUTTON_ID),this._isEditMode=e.isEditMode,this._label.validator=resource.labelValidator.bind(this),this._resourceId.validator=resource.resourceIdValidator.bind(this),this._description.validator=resource.descriptionValidator.bind(this),this._initEvents()},_initEvents:function(){this._nextButton.observe("click",function(e){this._isDataValid()||e.stop()}.bindAsEventListener(this)),this._form.observe("keyup",function(e){var i=e.element();[this._label,this._resourceId,this._description].include(i)&&(ValidationModule.validate(resource.getValidationEntries([i])),i==this._resourceId&&this._resourceId.getValue()!=resource.generateResourceId(this._label.getValue())&&(this._canGenerateId=!1),i==this._label&&!this._isEditMode&&this._canGenerateId&&(this._resourceId.setValue(resource.generateResourceId(this._label.getValue())),ValidationModule.validate(resource.getValidationEntries([this._resourceId]))))}.bindAsEventListener(this))},_isDataValid:function(){var e=[this._label,this._resourceId,this._description];return ValidationModule.validate(resource.getValidationEntries(e))}};"undefined"==typeof require&&document.observe("dom:loaded",function(){resourceAnalysisView.initialize(localContext.initOptions)});
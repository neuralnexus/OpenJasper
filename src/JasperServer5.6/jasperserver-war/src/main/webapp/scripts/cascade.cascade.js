/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @version: $Id: cascade.cascade.js 47331 2014-07-18 09:13:06Z kklein $
 */

var showProgressTimeout = null;
var updateCascadeTimer = null;
var controlClicked = null;
var controlClickedControlType = null;
var wrappersUUID = null;
var resetButtonLabel = null;
var maxMultiSelectSize = null;
var reportOptionsClicked = false;

function Map(){
    // members
    this.keyArray = new Array(); // Keys
    this.valArray = new Array(); // Values

    // methods
    this.put = put;
    this.get = get;
    this.size = size;
    this.clear = clear;
    this.keySet = keySet;
    this.valSet = valSet;
    this.showMe = showMe;   // returns a string with all keys and values in map.
    this.findIt = findIt;
    this.remove = remove;
}

function put( key, val ){
    var elementIndex = this.findIt( key );

    if( elementIndex == (-1) ){
        this.keyArray.push( key );
        this.valArray.push( val );
    } else {
        this.valArray[ elementIndex ] = val;
    }
}

function get( key ){
    var result = null;
    var elementIndex = this.findIt( key );

    if( elementIndex != (-1) ){
        result = this.valArray[ elementIndex ];
    }
    return result;
}

function remove( key ){
    var result = null;
    var elementIndex = this.findIt( key );

    if( elementIndex != (-1) ){
        this.keyArray = this.keyArray.removeAt(elementIndex);
        this.valArray = this.valArray.removeAt(elementIndex);
    }

    return ;
}

function size(){
    return (this.keyArray.length);
}

function clear() {
    while (this.keyArray.length > 0) { this.keyArray.pop(); this.valArray.pop(); }
}

function keySet(){
    return (this.keyArray);
}

function valSet(){
    return (this.valArray);
}

function showMe(){
    var result = "";

    for( var i = 0; i < this.keyArray.length; i++ ){
        result += "Key: " + this.keyArray[ i ] + "\tValues: " + this.valArray[ i ] + "\n";
    }
    return result;
}

function findIt( key ){
    var result = (-1);

    for( var i = 0; i < this.keyArray.length; i++ ){
        if( this.keyArray[ i ] == key ){
            result = i;
            break;
        }
    }
    return result;
}

function removeAt( index ){
    var part1 = this.slice( 0, index);
    var part2 = this.slice( index+1 );

    return( part1.concat( part2 ) );
}

Array.prototype.removeAt = removeAt;

var controlMap = new Map();

function initAggregate(cPrefix, cName, cType, isDisabled, initialValue){
	// do nothing any more. Turned off for IC refactoring project
	return;

    //controlMap.put(cName, initializeEnvelope(cPrefix, cName, cType, isDisabled, initialValue));
}

function initCascade(reportURI){
	// do nothing any more. Turned off for IC refactoring project
	return;

    //RemoteCascadeDirector.initialize(reportURI, getControlsWithSelectedOptionsOnly(controlMap.valSet()), refreshAllControls);
}

function fireCascade(reportURI, cPrefix, cName, cType, isDisabled){
    // Clear possibly setted timeout by previous
    // fireCascade or autoCascade call
    clearTimeout(updateCascadeTimer);
    if (updateCascadeTimer != null) {
        cancelProgressDialog();
        updateCascadeTimer = null;
    }

    controlClicked = cName;
    controlClickedControlType = cType;
    updateCascadeTimer = setTimeout("showProgressDialog()", 1000);

    controlMap.put(cName, populateEnvelope(cPrefix, cName, cType, isDisabled));

    var sessionErrorHandler = function(message, ex) {
        if (ex.javaClassName == 'java.lang.SecurityException') {
            document.location = '.';
        }
    }

    dwr.engine.setErrorHandler(sessionErrorHandler);

    RemoteCascadeDirector.handleEvents(reportURI, getControlsWithSelectedOptionsOnly(controlMap.valSet()), refreshAllControls);

    // function resetReportOptionsSelect()  - taken from report-options.js
    resetReportOptionsSelect();

    setTimeout('updateCascadeTimer',0);
}

function resetReportOptionsSelect() {
    var select = $("savedValues");
    if (select)
    {
        select.selectedIndex = 0;
    }
}

function autoCascade(reportURI, optionsUri){
    // Clear possibly setted timeout by previous
    // fireCascade or autoCascade call
    clearTimeout(updateCascadeTimer);
    if (updateCascadeTimer != null) {
        cancelProgressDialog();
        updateCascadeTimer = null;
    }

    controlClicked = null;
    controlClickedControlType = null;
    reportOptionsClicked = true;
    updateCascadeTimer = setTimeout("showProgressDialog()", 1000);
    RemoteCascadeDirector.autoPopulate(reportURI, getControlsWithSelectedOptionsOnly(controlMap.valSet()), optionsUri, refreshAllControls);
    setTimeout('updateCascadeTimer',0);
}

function refreshAllControls(allEnvelopes){
    if (undefined != allEnvelopes){
        for (var travEnv = 0; travEnv < allEnvelopes.length; travEnv++){

            var env = allEnvelopes[travEnv];

            // do not refresh the clicked control

            // for checkboxes and radios:
            // if control is clicked and there are present controls -
            // do not refresh them
            if (env.controlType == 8 || env.controlType == 9 || env.controlType == 10 || env.controlType == 11) {
                if (env.controlName == controlClicked) {
                    var checkBoxesCount = document.getElementsByName(env.controlName).length;
                    if (checkBoxesCount > 0) {
                        continue;
                    }
                }
            }

            if (env.controlType == 6 || env.controlType == 7) {
                var currentList = document.getElementById(env.controlName).options;
                //do not update control if it was clicked and has not empty list
                // of options
                if (env.controlName == controlClicked && currentList.length > 0) {
                    continue;
                }

                // do not refresh if the list size and selections are not changed
                // but still refresh if report options was changed
                var updatedList = env.options;
                if (!reportOptionsClicked && currentList.length == updatedList.length) {
                    var doRefresh = false;
                    for (var i=0; i<currentList.length;i++){
                        if (currentList[i].value != updatedList[i].value || currentList[i].selected != updatedList[i].selected ) {
                            doRefresh = true;
                            break;
                        }
                    }
                    if (!doRefresh) {
                        continue;
                    }
                }
            }
            refreshControl(env);
        }
    }

    if (isItDashboard() && controlClicked) {
        designerBase = window.designerBase;
        var controlFrame = null;

        //radio buttons & check boxes
        if (controlClickedControlType == 8 || controlClickedControlType == 9 || controlClickedControlType == 10 || controlClickedControlType == 11){
            var options = null;
            var numberOfOptions = null;
            //radio
            if(controlClickedControlType == 8 || controlClickedControlType == 9 ){
                options = $$("input[type=radio][name='" + controlClicked + "']");
            }else{
                //check boxes
                options = $$("input[type=checkbox][name='" + controlClicked + "']");
            }

            // use the first one to handle case with reset button when no one is selected but data needs to be sent however
            // #21262, 11/25/10
            controlClicked = options[0];
//            numberOfOptions = options.length;
//            for(var index = 0; index < numberOfOptions; index++){
//                if(options[index].checked){
//                    controlClicked = options[index];
//                    break;
//                }
//            }
        }

        if ($(controlClicked)) {
            var frameName = $(controlClicked).readAttribute("data-frameName");
            if(frameName){
                if (localContext.getMode() == designerBase.DASHBOARD) {
                    controlFrame = localContext._getFrameByNameAndType(localContext.CONTROL_FRAME, frameName);
                    if (controlFrame) {
                        localContext.updateInputControlParams(controlFrame);
                    }
                } else {
                    controlFrame = localContext._getControlFrameObjectByName(frameName);
                    localContext._controlParamsUpdated(controlFrame);
                }
            }
        }
        
        /* Reset info about last clicked controls to null */
        controlClicked = null;
        controlClickedControlType = null;
    }

    clearTimeout(updateCascadeTimer);
    if (updateCascadeTimer != null) {
        cancelProgressDialog();
        updateCascadeTimer = null;
    }
    reportOptionsClicked = false;
}

function isItDashboard() {
    return localContext && (window.designerBase) && localContext.getMode && (localContext.getMode() == window.designerBase.DASHBOARD || localContext.getMode() == window.designerBase.DASHBOARD_RUNTIME);
}

function refreshControl(envelope){

    var controlName = envelope.controlName;
    
    if ( envelope.controlType == 3 || envelope.controlType == 4 ||
            envelope.controlType == 6 || envelope.controlType == 7) {
        /*
         Single Select List of Values = 3
         Single Select Query = 4
         Multi  Select List of Values = 6
         Multi  Select Query = 7
         */
        if (!envelope.permanent) {
            dwr.util.removeAllOptions(controlName);
            dwr.util.addOptions(controlName, envelope.options, "value", "label");
        }
        //Change size of multiselect list if necessary
        if ((envelope.controlType == 6 || envelope.controlType == 7) && maxMultiSelectSize) {
            var currentSelectElement = document.getElementById(envelope.controlName);
            var currentList = document.getElementById(envelope.controlName).options;
            currentSelectElement.size = Math.min(currentList.length, maxMultiSelectSize);
        }

        if ( envelope.controlType == 3 ||
                envelope.controlType == 4) {
            // single, single select query
            dwr.util.setValue(controlName, envelope.controlValue);
        } else {
            // multiselect, multiselect query
            dwr.util.setValue(controlName, envelope.selections);
        }

        var control = document.getElementById(controlName);
        //adjust for enabled/disabled.
        control.disabled = envelope.disabled;
    } else if (envelope.controlType == 1) {
        /*
         Boolean = 1
         */
        dwr.util.setValue(envelope.controlName, envelope.controlValue == "true");
    } else if (envelope.controlType == 2) {
        /*
         Single Value = 2
         */
        dwr.util.setValue(envelope.controlName, envelope.controlValue);
    } else if (envelope.controlType == 8 || envelope.controlType == 9 ||
            envelope.controlType == 10 || envelope.controlType == 11) {
        /*
         Single Select List of Values (Radio) = 8
         Single Select Query (Radio) = 9
         Multi Select List of Values (Checkbox) = 10
         Multi Select Query (Checkbox) = 11
         */

        if (!envelope.permanent) {
            if (isItDashboard()) {
                updateRadiosAndCheckboxes(envelope, getParamsForDashboardCheckBoxOrRadioCreation, createDashboardCheckBoxOrRadioElement, dashboardResetOnclick, localContext.resetButton);
            } else {
                var  runtimeResetOnclick = function(envelope) {
                    if (resetRadio(this.form[envelope.controlName])) {
                        fireCascade($F('reportUnitURI'), envelope.resourceUriPrefix,
                                envelope.controlName, envelope.controlType, envelope.disabled);
                    }
                }.curry(envelope);

                updateRadiosAndCheckboxes(envelope, getParamsForRuntimeCheckBoxOrRadioCreation, createRuntimeCheckBoxOrRadioElement, runtimeResetOnclick, resetButtonLabel);
            }
        } else {
            if ( envelope.controlType == 8 ||
                    envelope.controlType == 9) {
                // single, single select query
                dwr.util.setValue(controlName, envelope.controlValue);
            } else {
                // multiselect, multiselect query
                dwr.util.setValue(controlName, envelope.selections);
            }
        }
    } else {
        /*
         *  public static final byte TYPE_MULTI_VALUE = 5;
         */
    }

    //For View Report Runtime - hive non visible elements
    var catControlName = $("jsCtrl_" + controlName);
    if (catControlName) {
        var datePickerButtons = $(catControlName).select('button.picker');
        datePickerButtons.each(function(elem) {
            envelope.disabled ? elem.addClassName(layoutModule.HIDDEN_CLASS) : elem.removeClassName(layoutModule.HIDDEN_CLASS);
        });

        envelope.visible
                ? $(catControlName).removeClassName(layoutModule.HIDDEN_CLASS)
                : $(catControlName).addClassName(layoutModule.HIDDEN_CLASS);
    }

    // sync the control back to model
    var modelEnv = controlMap.get(envelope.controlName);
    modelEnv.controlValue = dwr.util.getValue(envelope.controlName);
    modelEnv.options = getSelections(envelope.controlName, envelope.controlType);
    
    var ctrl = $(controlName);
    ctrl && ctrl.show();
}

function createRuntimeCheckBoxOrRadioElement(envelope, option) {
    var input = new Element('input', {
        'style': 'position: relative', //Due to bad layout in IE an Chrome
        'class': '',
        'name': envelope.controlName,
        'type': envelope.controlType ==  8 || envelope.controlType == 9 ? 'radio' : 'checkbox',
        'value': option.value
    });
    if (option.selected) input.writeAttribute('checked', 'checked');
    if (envelope.disabled) input.writeAttribute('disabled', 'disabled');
    input.observe(isIE() ? 'click' : 'change', function() {fireCascade($("reportUnitURI").value, envelope.resourceUriPrefix, envelope.controlName, envelope.controlType, envelope.disabled)});

    var label =  new Element('label', {'class': 'control ' +
            envelope.controlType ==  8 || envelope.controlType == 9 ? 'radio' : 'checkBox'
    });
    var span = new Element('span', {'class': ''/*cannot use wrap here*/}).update(option.label);

    label.insert(input);
    label.insert(span);

    var li = new Element('li', {'class': ''/*cannot use leaf*/});
    li.insert(label);

    return li;
};

function getParamsForRuntimeCheckBoxOrRadioCreation(envelope) {
    var list = $("jsCtrl_" + envelope.controlName).select('ul')[0];
    if (!list) return null;

    return {
        parentElement: list
    }
};


function dashboardResetOnclick() {
    var container = this.up("div");
    var divs = container.select('div');
    var frameName ;
    divs.each(function(div) {
        var attr = div.readAttribute("data-frameName");
        if (attr) {
            frameName = attr;
        }
    });

    var inputs = this.up('ul').select('input');
    if (resetRadio(inputs)) {
        if (inputs.length > 0 && frameName) {
            controlClicked = inputs[0];
            localContext._inputControlOnChange('controlFrameOverlay_' + frameName);
        }
    }
}

function createDashboardCheckBoxOrRadioElement(envelope, option, params) {
    var input = new Element('input', {
        'class': 'dynamicInputControl',
        'data-frameName': params.frameName,
        'data-frameType':'controlFrame',
        'name': envelope.controlName,
        'type': envelope.controlType ==  8 || envelope.controlType == 9 ? 'radio' : 'checkBox',
        'value': option.value
    });
    if (option.selected) input.writeAttribute('checked', 'checked');
    if (params.readOnly) input.writeAttribute('disabled', 'disabled');
    input.observe('click', function() {return localContext._inputControlOnChange('controlFrameOverlay_' + params.frameName)});

    var label =  new Element('label', {'class': 'control ' +
            envelope.controlType ==  8 || envelope.controlType == 9 ? 'radio' : 'checkBox'
    });
    var span = new Element('span', {'class': 'wrap'}).update(option.label);

    label.insert(input);
    label.insert(span);

    var li = new Element('li', {'class': 'leaf'});
    li.insert(label);

    return li;
};

function getParamsForDashboardCheckBoxOrRadioCreation(envelope) {
    var frameDatas = $$("div[data-inputControlName='" + envelope.controlName + "']");
    if (frameDatas.length == 0) return null;
    var frame = frameDatas[0].up();
    if (!frame) return null;
    var uls = frame.select('ul');

    return {
        frame: frame,
        frameName: frameDatas[0].readAttribute("data-frameName"),
        readOnly: frameDatas[0].readAttribute("data-isReadOnly") === 'true',
        parentElement: uls.first()  
    }
};

function updateRadiosAndCheckboxes(envelope, getParams, createElement, resetButtonClick, resetButtonText) {
    var params = getParams(envelope);

    if (params) {
        if (params.parentElement) {
            params.parentElement.update('');

            envelope.options.each(function(option) {
                var li = createElement(envelope, option, params);
                params.parentElement.insert(li);
            });

            //Reset Button
            if (envelope.options.first() && !envelope.mandatory && (envelope.controlType ==  8 || envelope.controlType == 9)) {
                var lis = params.parentElement.select('li');
                var li = new Element('li', {'class': 'leaf'});
                var button = new Element('button', {'class': 'button action up', 'type': 'button'});
                button.observe('click', resetButtonClick);
                var span = new Element('span', {'class': 'wrap'}).update(resetButtonText);
                lis[0].insert({'before': li.insert(button.insert(span))});
            }
        }
    }
}

function getSelections(cName, cType){
    var optionsArray = [];
    if ( cType == 3 || cType == 4 || cType == 6 || cType == 7 ) {
        var element = document.getElementById(cName);
        for (var i=0; undefined != element && i < element.options.length; i++){
            var messageOption = {
                selected: element.options[i].selected,
                label: element.options[i].text,
                value: element.options[i].value
            };
            optionsArray.push(messageOption);
        }
    } else if (cType >= 8 && cType <= 11) {
        var checkboxOrRadioGroup = document.getElementsByName(cName);

        if (undefined == checkboxOrRadioGroup) {
            return optionsArray;
        }

        if (checkboxOrRadioGroup[0]) { // if array
            for (i=0; i < checkboxOrRadioGroup.length; i++){
                var messageOption = {
                    selected: checkboxOrRadioGroup[i].checked,
                    label: checkboxOrRadioGroup[i].value,
                    value: checkboxOrRadioGroup[i].value
                };
                optionsArray.push(messageOption);
            }
        } else {
            var messageOption = {
                selected: checkboxOrRadioGroup.checked,
                label: checkboxOrRadioGroup.value,
                value: checkboxOrRadioGroup.value
            };
            optionsArray.push(messageOption);
        }
    }
    return optionsArray;
}

function populateEnvelope(cPrefix, cName, cType, isDisabled){
    var optionsArray = getSelections(cName, cType);

    var envelope = {
        controlName:        cName,
        controlValue:       dwr.util.getValue(cName),
        resourceUriPrefix:  cPrefix,
        controlType:        cType,
        options:            optionsArray,
        visible:            true,
        disabled:           isDisabled,
        wrappersUUID:       wrappersUUID
    };

    return envelope;
}

// refreshes controMap if changes was made outside
// normal cascade logic i.e. controls in DOM was updated by hand
function refreshControlsMap() {
    for (var i = 0; i < controlMap.valArray.length; i++) {
        var envelope = controlMap.valArray[i];
        controlMap.put(envelope.controlName, populateEnvelope(envelope.resourceUriPrefix, envelope.controlName, envelope.controlType, envelope.disabled));
    }
}

// initialize with the given values
function initializeEnvelope(cPrefix, cName, cType, isDisabled, initialValue) {

    var envelope = populateEnvelope(cPrefix, cName, cType, isDisabled);

    var value = "";
    if ( cType == 6 || cType == 7 || cType == 10 || cType == 11 ) {
        // multi select list encoded as:
        // { value=true, value=true, ... }
        // convert to options

        var optionsMap = new Map();

        if (initialValue.length > 2) {
            // remove start and end brackets
            var contents = initialValue.substring(1,initialValue.length - 1);

            // split into parts to get the values
            var parts = contents.split("=true,");

            for (var i=0; i < parts.length; i++) {
                var option = parts[i];

                // trim blanks from both ends
                option  = option.replace(/^\s+|\s+$/g,"");

                // remove =true from the last option
                if (option.lastIndexOf("=true") > -1) {
                    option = option.substring(0, option.length - 5);
                }

                if (option.length > 0) {
                    var messageOption = {
                        selected: true,
                        label: option,
                        value: option
                    };
                    optionsMap.put(option, messageOption);
                }
            }

// commented it out because the CIC still were broken when setting the default values.
// We can not determine the CICs here, thus we should always overwrite the optionsList. Scope of bug #22892.
/*
            if (envelope.optionsList.length > 0) {
                // There are existing items in the list
                // select them if they are in the optionsMap

                for (var j=0; j < envelope.optionsList.length; j++) {
                    var current = envelope.optionsList[j];

                    if (optionsMap.get(current.value) != null) {
                        current.selected = true;
                    }
                }
            } else {
*/
                // otherwise, pass the values through so they can be
                // possibly used for cascading selects
                envelope.options = optionsMap.valSet();
//            }
        }
    } else {

        value = initialValue;
    }

    envelope.controlValue = value;

    return envelope;
}

// strips out non-selected options to reduce traffic
function getControlsWithSelectedOptionsOnly(controlValues) {
    var newControlValues = new Array();
    for (var i = 0; i < controlValues.length; i++) {
        var ctl = controlValues[i];
        var envelope = {
            controlName:        ctl.controlName,
            controlValue:       ctl.controlValue,
            resourceUriPrefix:  ctl.resourceUriPrefix,
            controlType:        ctl.controlType,
            options:            new Array(),
            visible:            ctl.visible,
            disabled:           ctl.disabled,
            wrappersUUID:       wrappersUUID
        };
        if (ctl.options) {
            for (var k = 0; k < ctl.options.length; k++) {
                var opt = ctl.options[k];
                if (opt.selected) {
                    var newOpt = {
                        selected: opt.selected,
                        label: opt.label,
                        value: opt.value
                    };
                    envelope.options.push(newOpt);
                }
            }
        }
        newControlValues.push(envelope);
    }

    return newControlValues;
}


function showProgressDialog() {
    showProgressTimeout = null;
    dialogs.popup.show($(ajax.LOADING_ID), true);
    //	pushTotalOverlayObject("haze", 95);
    /*
     var progressDiv = document.getElementById("busyMessage");
     if (progressDiv) {
     progressDiv.style.display = "block";
     centerLayer(progressDiv);
     }
     */

}

function cancelProgressDialog() {
    var timeout = showProgressTimeout;
    if (timeout != null) {
        // the progress dialog has not yet been shown
        showProgressTimeout = null;
        clearTimeout(timeout);
    } else {
        // hide the progress dialog
        hideProgressDialog();
    }
}

function hideProgressDialog() {
    dialogs.popup.hide($(ajax.LOADING_ID));
    /*
     var progressDiv = document.getElementById("busyMessage");
     if (progressDiv && progressDiv.style.display == "block") {
     document.getElementById("busyMessage").style.display = "none";
     //		popOverlayObject();
     }
     */
}


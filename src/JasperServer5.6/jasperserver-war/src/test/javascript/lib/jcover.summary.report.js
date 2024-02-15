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
 * @author: inesterenko
 * @version: $Id: jcover.summary.report.js 47331 2014-07-18 09:13:06Z kklein $
 */

function makeCoverageTable() {
  var coverage = _$jscoverage[jscoverage_currentFile];
  var branchData = _$jscoverage.branchData[jscoverage_currentFile];
  var lines = coverage.source;

  // this can happen if there is an error in the original JavaScript file
  if (! lines) {
    lines = [];
  }

  var rows = ['<table id="sourceTable">'];
  var i = 0;
  var progressBar = document.getElementById('progressBar');
  var tableHTML;
  var currentConditionalEnd = 0;

  function joinTableRows() {
    tableHTML = rows.join('');
    ProgressBar.setPercentage(progressBar, 60);
    /*
    This may be a long delay, so set a timeout of 100 ms to make sure the
    display is updated.
    */
    setTimeout(function() {appendTable(jscoverage_currentFile);}, 100);
  }

  function appendTable(jscoverage_currentFile) {
    var sourceDiv = document.getElementById('sourceDiv');
    sourceDiv.innerHTML = tableHTML;
    ProgressBar.setPercentage(progressBar, 80);
    setTimeout(jscoverage_scrollToLine, 0);
  }

  while (i < lines.length) {
    var lineNumber = i + 1;

    if (lineNumber === currentConditionalEnd) {
      currentConditionalEnd = 0;
    }
    else if (currentConditionalEnd === 0 && coverage.conditionals && coverage.conditionals[lineNumber]) {
      currentConditionalEnd = coverage.conditionals[lineNumber];
    }

    var row = '<tr>';
    row += '<td class="numeric">' + lineNumber + '</td>';
    var timesExecuted = coverage[lineNumber];
    if (timesExecuted !== undefined && timesExecuted !== null) {
      if (currentConditionalEnd !== 0) {
        row += '<td class="y numeric">';
      }
      else if (timesExecuted === 0) {
        row += '<td class="r numeric" id="line-' + lineNumber + '">';
      }
      else {
        row += '<td class="g numeric">';
      }
      row += timesExecuted;
      row += '</td>';
    }
    else {
      row += '<td></td>';
    }

    if (_$jscoverage.branchData[jscoverage_currentFile] !== undefined && _$jscoverage.branchData[jscoverage_currentFile].length !== undefined) {
        var branchClass = '';
        var branchText = '&#160;';
        var branchLink = undefined;
        if (branchData[lineNumber] !== undefined && branchData[lineNumber] !== null) {
            branchClass = 'g';
            for (var conditionIndex = 0; conditionIndex < branchData[lineNumber].length; conditionIndex++) {
                if (branchData[lineNumber][conditionIndex] !== undefined && branchData[lineNumber][conditionIndex] !== null && !branchData[lineNumber][conditionIndex].covered()) {
                    branchClass = 'r';
                    break;
                }
            }

        }
        if (branchClass === 'r') {
            branchText = '<a href="#" onclick="alert(buildBranchMessage(_$jscoverage.branchData[\''+jscoverage_currentFile+'\']['+lineNumber+']));">info</a>';
        }
        row += '<td class="numeric '+branchClass+'"><pre>' + branchText + '</pre></td>';
    }

    row += '<td><pre>' + lines[i] + '</pre></td>';
    row += '</tr>';
    row += '\n';
    rows[lineNumber] = row;
    i++;
  }
  rows[i + 1] = '</table>';
  ProgressBar.setPercentage(progressBar, 40);
  joinTableRows();
  return tableHTML;
}


function jscoverage_createLink(file, line) {
  var link = document.createElement("a");
  var strs = file.split("/");
  var fileName =  strs[strs.length - 1];
  if (!(/.html$/).test(fileName)){
      fileName += ".html";
  }
  link.href =  fileName;

  var text;
  if (line) {
    text = line.toString();
  }
  else {
    text = file;
  }

  link.appendChild(document.createTextNode(text));

  return link;
}



(function (){

    function q(selector){
        return document.querySelector(selector);
    }

    function waitForTests(callback){
        var intervalId = setInterval(function(){
            var status = frames[0].jasmine.__console_reporter_status__;
            if (status == "success" || status == "fail"){
                clearInterval(intervalId);
                callback();
            }
        }, 1000);
    }

    function generateSourceCoverageReport(resource){
       jscoverage_currentFile = resource;
       return "<html>" +
           document.head.innerHTML +
           "<body><div id='mainDiv'><div id='tabPages' class='TabPages'>" +
                "<div class='selected TabPage' style='overflow: scroll'>" +
                    "<div id='fileDiv'>" + resource + "</div>" +
                    makeCoverageTable() +
           "</div></div></div></body></html>";
    }

    function getJsCoverageFiles(){
        var files = [];
        for (var file in window._$jscoverage) {
            if (file === 'branchData')
                continue;
            files.push(file);
          }
          return files;
    }

    function getJsCoverageChildren(){
        var children = [];
        var files =  getJsCoverageFiles();
        for(var i = 0; i < files.length; i++){
            var fileName = files[i];
           children.push({
               name : fileName + ".html",
               markup: generateSourceCoverageReport(fileName),
               children: []
           });
        }
        return children;
    }

    function prepareSummaryReport(){
        q("#tabs").setAttribute("style", "display:none");
        q("#checkbox").setAttribute("style", "display:none");
        q(".selected.TabPage label").setAttribute("style", "display:none");
        return "<html>"+ document.documentElement.innerHTML + "</html>"
    }

    function storeCoverageReport(){
        jscoverage_selectTab("summaryTab");
        jscoverage_recalculateSummaryTab();
        __code_coverage_reports__ = [
            {
                    name: "index.html",
                    markup:prepareSummaryReport(),
                    children: getJsCoverageChildren()
            }
        ];
        //notify that coverage report is ready
        console.log("Coverage report is ready");
    }

    window.generateCoverageReport = function (url){
        console.log("Waiting that all markup is loaded...");
        setTimeout(function(){
                var input = q("#location");
                input.value = url;
                jscoverage_openInFrameButton_click();
                console.log("Waiting for tests execution...");
                waitForTests(storeCoverageReport);
         }, 5000);
    }

})();

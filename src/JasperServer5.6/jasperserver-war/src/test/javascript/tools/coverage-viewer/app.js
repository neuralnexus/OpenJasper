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
 * @version: $Id: app.js 47331 2014-07-18 09:13:06Z kklein $
 */

(function ($, _) {

    var domReady = new $.Deferred();
    $(document).ready(function (p) {domReady.resolve(p);});


    function CoverageSource(settings){

        this.settings  = settings;

        this.getCoverageReports =  function(){
            var deferred = new $.Deferred();

            var fetchReports = $.ajax(settings).pipe(function(response){
                var results = [];
                var items = $(response).find('tr');
                items.find('a').each(function(index, elem){
                    var text = $(elem).find('tt').text();
                    var srcName =  String(text).replace("tests/","js.gcov.html");
                    if (text.indexOf('tests') !== -1) {
                        results.push({
                            name : text.replace(".tests/",""),
                            uri: $(elem).attr('href') + "src/" + srcName
                        });
                    }
                });
                return results
            });

            fetchReports.then(function(reports){
                 var params = _.clone(settings);
                 var queue = [];
                 _(reports).each(function(report, index){
                     params.url = report.uri;
                     var dfr = new $.Deferred();
                     function findCoverageMetric(elem){
                         return elem.length > 0 && elem.text().match(/%/)
                     }
                     $.ajax(params).
                         done(function (response) {
                             var elem;
                             var dom = $(response);
                             if (findCoverageMetric(dom.find(".headerCovTableEntryMed"))) {
                                 elem = dom.find(".headerCovTableEntryMed");
                             } else if (findCoverageMetric(dom.find(".headerCovTableEntryHi"))) {
                                 elem = dom.find(".headerCovTableEntryHi");
                             } else if (findCoverageMetric(dom.find(".headerCovTableEntryLo"))) {
                                 elem = dom.find(".headerCovTableEntryLo");
                             }
                             elem && (report.coverage = parseFloat(elem.text().replace("%", "")));
                             dfr.resolve(report);
                         })
                         .fail(function(){
                             if (!report.coverage) {
                                 report.failed = true;
                                 report.coverage = 0;
                             }
                             dfr.resolve(report);
                         }
                     );
                     queue.push(dfr);
                 });

                $.when.apply($, queue).then(function(){
                    deferred.resolve(_.toArray(arguments));
                });

            });

            return deferred;
        };
    }

    function ReportEntryView(report){


        var elem = $("<div class='entry'><h3></h3></div>");
        var header = elem.find("h3");
        header.text(report.name);

        if (report.failed){
            header.addClass("fail")
        }else if (report.coverage < 75){
            header.addClass("low");
        } else if(report.coverage < 90){
            header.addClass("med");
        }else{
            header.addClass("high");
        }


        this.getElem = function(){
            return elem;
        };

        header.click(function () {


            if (!report.failed){
                $("#list iframe").hide();

                $(".entry").each(function(i, e){
                    $(e).removeClass("selected");
                });
                elem.addClass("selected");

                $("iframe[src='"+report.uri+"']").show();
            }
        });
    }

    function ReportView(report){
        var frame = $("<iframe class='report' scrolling='auto' style='display: none;'></iframe>");
        frame.attr("src", report.uri);
        this.getElem = function(){
            return frame;
        };
    }

    function FiltersPanel(){

        var elem = $('<div class="filters"><div class="filterInput"><span>Filter:</span><input type="text" /></div></div>');
        elem.append($('<div class="filterControls"><div data-type="fail" class="failFilter"></div><div data-type="low"  class="lowFilter"></div><div data-type="med" class="medFilter"></div><div data-type="high" class="highFilter"></div><button>Reset</button></div>'));

        var filterStatus = "all", filterText = "";

        function filterEntries(params){

            console.log(params);

            _.each(elem.find(".filterControls div"),function(el){
                $(el).removeClass("selected");
            });

            if(params.status != "all"){
                elem.find("div[data-type='"+params.status+"']").addClass("selected");
            }

            var expr = new RegExp(_.isEmpty(params.text) ? ".*" : params.text, 'i');
            _.chain($(".entry"))
                .filter(function(entry){
                    if (params.status == "all") return true;
                    var headerClassName = $(entry).find("h3").attr("class");
                    if (params.status.match(new RegExp(headerClassName))){
                        return true;
                    }else{
                         $(entry).hide();
                    }
                })
                .each(function (header) {
                    if (!$(header).text().match(expr)) {
                        $(header).hide();
                    }else{
                        $(header).show();
                    }
                }
            );
        }

        this.getElem = function(){
            return elem;
        };

        elem.on("keyup", "input",function (event) {
            filterText = $(event.target).val();
            filterEntries({status:filterStatus,text:filterText});
        });

        elem.on("click",".filterControls div",function(event){
            if ($(event.target).attr("data-type") == filterStatus){
                filterStatus = "all";
            }else{
                filterStatus = $(event.target).attr("data-type");
            }
            filterEntries({
                status:filterStatus,
                text:filterText
            });
        });

        elem.on("click",".filterControls button", function(){
            filterStatus = "all";
            filterText = "";
            elem.find("input").val(filterText);
            filterEntries({status:filterStatus,text:filterText});
        });

    }

     var coverageSource = new  CoverageSource({
         type:'GET',
         url:'../',
         contentType:'text/html'
     });

        $.when(coverageSource.getCoverageReports(), domReady).then(function(reports){


            var entries = $('.entries');
            var list = $('#list');
            var sidebar = $("#sidebar");

            _(reports).each(function (report) {
                var reportEntry = new ReportEntryView(report);
                var reportView = new ReportView(report);
                entries.append(reportEntry.getElem());
                list.append(reportView.getElem());
            });
            var filtersPanel = new FiltersPanel();
            sidebar.prepend(filtersPanel.getElem());
        });

})(
    jQuery,
    _
);


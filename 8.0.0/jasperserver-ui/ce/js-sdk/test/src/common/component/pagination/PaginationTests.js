/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import sinon from 'sinon';
import PaginationView from 'src/common/component/pagination/Pagination';
import Backbone from 'backbone';
import $ from 'jquery';

describe("Pagination View", function(){
    var paginationView;


    beforeEach(function(){
        paginationView = new PaginationView({current: 2, total: 20});
        paginationView.render();
    });

    afterEach(function(){
        paginationView.remove();
    });

    it('should be Backbone.View instance', function(){
        expect(typeof PaginationView).toBe('function');
        expect(PaginationView.prototype instanceof Backbone.View).toBeTruthy();
    });

    it("should be properly initialized", function(){
        expect(paginationView.firstPage).toBeDefined();
        expect(paginationView.prevPage).toBeDefined();
        expect(paginationView.currentPage).toBeDefined();
        expect(paginationView.nextPage).toBeDefined();
        expect(paginationView.lastPage).toBeDefined();
        expect(paginationView.hide).toBeDefined();
        expect(paginationView.show).toBeDefined();
        expect(paginationView.resetSetOptions).toBeDefined();
    });

    it("should trigger function event on click", function(){
        sinon.spy(paginationView, "trigger");

        paginationView.$el.find("button.first").trigger("click");

        expect(paginationView.trigger).toHaveBeenCalledWith("pagination:change", paginationView.model.get("current"));

        paginationView.trigger.restore();
    });

    it("should trigger function event on input change", function(){
        sinon.spy(paginationView, "trigger");

        paginationView.$el.find(".current").trigger("change");

        expect(paginationView.trigger).toHaveBeenCalledWith("pagination:change", paginationView.model.get("current"));

        paginationView.trigger.restore();
    });

    it("should set current and total pages", function(){
        expect(paginationView.$el.find(".current").val()).toEqual('2');
        expect(paginationView.$el.find(".total_pages").text()).toEqual('20');
    });

    it("should set current and total pages to 1 if undefined", function(){
        paginationView.remove();

        paginationView = new PaginationView();
        paginationView.render();
        expect(paginationView.$el.find(".current").val()).toEqual('1');
        expect(paginationView.$el.find(".total_pages").text()).toEqual('');
    });

    it("should disable buttons if current equal total", function(){
        paginationView.remove();

        paginationView = new PaginationView({current: 20, total: 20});
        paginationView.render();
        expect(paginationView.$el.find("button.next").prop("disabled")).toBeTruthy();
        expect(paginationView.$el.find("button.last").prop("disabled")).toBeTruthy();
    });

    it("should disable buttons if current equal 1", function(){
        paginationView.remove();

        paginationView = new PaginationView({current: 1, total: 20});
        paginationView.render();
        expect(paginationView.$el.find(".prev").prop("disabled")).toBeTruthy();
        expect(paginationView.$el.find(".first").prop("disabled")).toBeTruthy();
    });

    it("should use custom error handler", function(){
        var error = {current: {code: "error.code"}};

        sinon.spy(paginationView, "trigger");

        paginationView.model.trigger("validated:invalid", paginationView.model, error);

        expect(paginationView.trigger).toHaveBeenCalledWith("pagination:error", error);

        paginationView.trigger.restore();
    });

    it("should append pagination correctly", function(){
        $("body").append(paginationView.el);

        var $pagination = $("body").find(".paginationControlWrapper");

        expect($pagination.find("button").length).toEqual(4);
        expect($pagination.find("input").length).toEqual(1);
    });

    it("should hide pagination", function(){
        $("body").append(paginationView.el);

        var $pagination = $("body").find(".paginationControlWrapper");

        paginationView.hide();

        expect($pagination).toBeHidden();
    });

    it("should show pagination", function(){
        $("body").append(paginationView.el);

        var $pagination = $("body").find(".paginationControlWrapper");

        paginationView.hide();

        expect($pagination).toBeHidden();

        paginationView.show();

        expect($pagination).toBeVisible();
    });

    it("should reset pagination set options", function(){
        expect(paginationView.options).toBeDefined();
        expect(paginationView.options.silent).toBe(false);
        expect(paginationView.options.validate).toBe(true);

        paginationView.resetSetOptions({silent: true, validate: false});

        expect(paginationView.options.silent).toBe(true);
        expect(paginationView.options.validate).toBe(false);

        paginationView.resetSetOptions();

        expect(paginationView.options.silent).toBe(false);
        expect(paginationView.options.validate).toBe(true);
    });
});
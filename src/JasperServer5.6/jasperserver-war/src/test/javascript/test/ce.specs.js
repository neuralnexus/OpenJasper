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
 * @version: $Id: ce.specs.js 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */

define([
    "tests/compare.jasmine.sinon.tests",
    "tests/templates.suite.ce",
    "tests/actionModel.modelGenerator.tests",
    "tests/actionModel.primaryNavigation.tests",
    "tests/attributes.model.tests",
    "tests/attributes.view.tests", //depends from orgModule
    "tests/core.ajax.tests",
    "tests/core.events.tests",
    "tests/core.layout.tests",
    "tests/components.about.tests",
    "tests/components.authoritymodel.tests",
    "tests/components.authoritypickerview.tests",
    "tests/components.ajaxuploader.tests",
    "tests/components.ajaxdownloader.tests",
    "tests/components.dialogs.tests",
    "tests/components.dialog.tests",
    "tests/components.searchBox.tests",
    "tests/components.state.tests",
    "tests/components.stateview.tests",
    "tests/components.statecontrollertrait.tests",
    "tests/components.systemnotificationview.tests",
    "tests/components.layout.tests",
    "tests/components.templateengine.tests",
    "tests/components.toolbarButtons.tests",
    "tests/components.tooltip.tests",
    "tests/components.servererrorsbackbonetrait.tests",
    "tests/controls.core.tests",
//    TODO: fix it
//    "tests/export.tests",
    "tests/export.statecontroller.tests",
    "tests/export.formmodel.tests",
    "tests/export.extendedformview.tests",
    "tests/export.shortformview.tests",
    "tests/export.app.tests",
//    TODO: fix it
//    "tests/import.tests",
    "tests/import.extendedformview.tests",
    "tests/import.formmodel.tests",
    "tests/import.app.tests",
    "tests/junitxml.jasmine.reporter.tests",
    "tests/list.base.tests",
    "tests/dynamicTree.tree.tests",
    "tests/dynamicTree.treenode.tests",
    "tests/dynamicTree.treesupport.tests",
    "tests/controls.basecontrol.tests",
    "tests/controls.components.tests",
    "tests/controls.viewmodel.tests",
    "tests/controls.controller.tests",
    "tests/controls.datatransfer.tests",
    "tests/validation.system.tests",
    "tests/mng.main.tests",
    "tests/mng.common.tests",
    "tests/mng.common.action.tests",
    "tests/org.role.mng.main.tests",
    "tests/org.role.mng.components.tests",
    "tests/org.user.mng.main.tests",
    "tests/org.user.mng.actions.tests",
    "tests/org.user.mng.components.tests",
	"tests/dataSource/DataSourceControllerTests",
	"tests/dataSource/view/BaseDataSourceViewTests",
	"tests/dataSource/view/JdbcDataSourceViewTests",
	"tests/dataSource/view/AwsDataSourceViewTests",
	"tests/dataSource/view/BeanDataSourceViewTests",
	"tests/dataSource/view/HiveDataSourceViewTests",
	"tests/dataSource/view/JndiDataSourceViewTests",
	"tests/dataSource/view/MongoDataSourceViewTests",
	"tests/dataSource/view/CustomDataSourceViewTests",
	"tests/dataSource/model/BaseDataSourceModelTests",
	"tests/dataSource/model/BeanDataSourceModelTests",
	"tests/dataSource/model/JndiDataSourceModelTests",
	"tests/dataSource/model/JndiDataSourceModelTests",
	"tests/dataSource/model/MongoDbDataSourceModelTests",
	"tests/dataSource/integrationTests/DataSourceTests",
	"tests/dataSource/integrationTests/view/AwsDataSourceViewTests",
	"tests/dataSource/integrationTests/view/BaseDataSourceViewTests",
	"tests/dataSource/integrationTests/view/BeanDataSourceViewTests",
	"tests/dataSource/integrationTests/view/CustomDataSourceViewTests",
	"tests/dataSource/integrationTests/view/HiveDataSourceViewTests",
	"tests/dataSource/integrationTests/view/JdbcDataSourceViewTests",
	"tests/dataSource/integrationTests/view/JndiDataSourceViewTests",
	"tests/dataSource/integrationTests/view/MongoDataSourceViewTests",
	"tests/dataSource/integrationTests/view/VirtualDataSourceViewTests",
	"tests/dataSource/util/settingsUtilityTests",
    "tests/common/model/BaseModelTests",
    "tests/common/model/RepositoryResourceModelTests",
    "tests/common/validation/backboneValidationExtensionTests",
    "tests/common/util/i18nMessageTests",
    "tests/common/util/ScopeCheckerTests",
    "tests/common/util/parse/numberTests",
    "tests/common/util/parse/dateTests",
    "tests/common/util/parse/timeTests",
    "tests/common/validation/ValidationErrorMessageTests",
    "tests/common/component/menu/MenuTests",
    "tests/common/component/menu/HoverMenuTests",
    "tests/common/component/option/OptionViewTests",
    "tests/common/logging/appender/ConsoleAppenderTests",
    "tests/common/logging/LevelTests",
    "tests/common/logging/LogItemTests",
    "tests/common/logging/LogTests",
    "tests/common/logging/LoggerManagerTests",
    "tests/common/logging/loggerTests",

    "tests/bi/component/ResourcesSearchTests",
    "tests/bi/component/InputControlsTests",
    "tests/bi/component/AuthenticationTests",
    "tests/bi/component/schema/TableColumnSchemaTests",
    "tests/bi/error/BiComponentErrorTests",
    "tests/bi/error/JavaScriptExceptionBiComponentErrorTests",
    "tests/bi/error/SchemaValidationBiComponentErrorTests",
    "tests/bi/error/ContainerNotFoundBiComponentErrorTests",
    "tests/bi/error/RequestBiComponentErrorTests",
    "tests/bi/error/ReportStatusErrorTests",
    "tests/bi/error/biComponentErrorFactoryTests",

    //input controls
    "tests/inputControl/model/InputControlModelTests",
    "tests/inputControl/model/InputControlStateModelTests",
    "tests/inputControl/model/InputControlOptionModelTests",
    "tests/inputControl/collection/InputControlOptionCollectionTests",
    "tests/inputControl/collection/InputControlCollectionTests",

    //Scalable controls components
    "tests/common/component/list/view/ScalableListTests",
    "tests/common/component/list/view/ListWithSelectionTests",
    "tests/common/component/singleSelect/view/SingleSelectTests",
    "tests/common/component/singleSelect/view/SingleSelectNewTests",
    "tests/common/component/multiSelect/view/AvailableItemsListTests",
    "tests/common/component/multiSelect/view/new/AvailableItemsListNewTests",
    "tests/common/component/multiSelect/view/SelectedItemsListTests",
    "tests/common/component/singleSelect/dataprovider/DataProviderNewTests"

],function(){
    console.log("Main specs suite for CE loaded.");
});

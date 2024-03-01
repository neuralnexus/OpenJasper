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

import $ from 'jquery';
import assetsToIncludeExtendedVersionTemplate from '../template/assetsToIncludeExtendedVersionTemplate.htm';
import eventsToIncludeTemplate from '../template/eventsToIncludeTemplate.htm';
import exportDataFileTemplate from '../template/exportDataFileTemplate.htm';
import rolesAndUsersToIncludeTemplate from '../template/rolesAndUsersToIncludeTemplate.htm';
import exportOptionsContainerTemplate from '../template/exportOptionsContainerTemplate.htm';
import repositoryExportTemplate from '../template/repositoryExportTemplate.htm';
import exportTypesEnum from '../enum/exportTypesEnum';
import secretKeyTemplate from '../../templates/secretKeyTemplate.htm'

var exportTemplateByType = {};
exportTemplateByType[exportTypesEnum.ROOT_TENANT] = [
    rolesAndUsersToIncludeTemplate,
    assetsToIncludeExtendedVersionTemplate,
    eventsToIncludeTemplate
].join('');
exportTemplateByType[exportTypesEnum.TENANT] = [
    rolesAndUsersToIncludeTemplate,
    assetsToIncludeExtendedVersionTemplate
].join('');
exportTemplateByType[exportTypesEnum.SERVER_PRO] = [
    rolesAndUsersToIncludeTemplate,
    assetsToIncludeExtendedVersionTemplate,
    eventsToIncludeTemplate
].join('');
exportTemplateByType[exportTypesEnum.SERVER_CE] = [
    rolesAndUsersToIncludeTemplate,
    assetsToIncludeExtendedVersionTemplate,
    eventsToIncludeTemplate
].join('');
exportTemplateByType[exportTypesEnum.REPOSITORY] = [
    repositoryExportTemplate
].join('');
export default function (options) {
    options = options || {};    // eslint-disable-next-line xss/htmlOutputRule
    // eslint-disable-next-line xss/htmlOutputRule
    return exportDataFileTemplate + secretKeyTemplate + $(exportOptionsContainerTemplate).append(exportTemplateByType[options.type])[0].outerHTML;
}
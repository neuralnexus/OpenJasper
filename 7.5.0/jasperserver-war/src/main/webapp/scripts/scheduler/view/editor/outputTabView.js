define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var _ = require('underscore');

var i18n = require("bundle!all");

var config = require("runtime_dependencies/js-sdk/src/jrs.configs");

var Backbone = require('backbone');

var picker = require('../../../components/components.pickers');

var resource = require('../../../resource/resource.base');

var outputTabTemplate = require("text!../../template/editor/outputTabTemplate.htm");

var RepositoryChooserDialogFactory = require("runtime_dependencies/bi-repository/src/bi/repository/dialog/resourceChooser/RepositoryChooserDialogFactory");

var repositoryResourceTypes = require("runtime_dependencies/bi-repository/src/bi/repository/enum/repositoryResourceTypes");

var settings = require("settings!treeComponent");

/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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
module.exports = Backbone.View.extend({
  events: {
    'click .ftp-test': 'testFTPConnection',
    'click [name=outputRepositoryButton]': 'outputRepositoryButtonClick',
    'click [name=sshKeyPathButton]': 'sshKeyPathButtonClick',
    'change [name=outputRepository]': 'fixUriInput',
    'change [name=sshKeyPath]': 'fixUriInput'
  },
  binding: [{
    attr: 'baseOutputFilename',
    control: 'baseOutputFilename'
  }, {
    attr: 'outputTimeZone',
    control: 'timeZone'
  }, {
    attr: 'outputLocale',
    control: 'outputLocale'
  }, {
    attr: 'outputFormats/outputFormat',
    control: 'outputFormats'
  }, {
    attr: 'repositoryDestination/outputDescription',
    control: 'outputDescription'
  }, {
    attr: 'repositoryDestination/overwriteFiles',
    control: 'overwriteFiles'
  }, {
    attr: 'repositoryDestination/sequentialFilenames',
    control: 'sequentialFilenames'
  }, {
    attr: 'repositoryDestination/timestampPattern',
    control: 'timestampPattern'
  }, {
    attr: 'repositoryDestination',
    control: 'timestampPattern',
    depends: 'repositoryDestination/sequentialFilenames'
  }, {
    attr: 'repositoryDestination/saveToRepository',
    control: 'outputToRepository'
  }, {
    attr: 'repositoryDestination',
    control: 'outputRepository, outputRepositoryButton',
    depends: 'repositoryDestination/saveToRepository'
  }, {
    attr: 'repositoryDestination/folderURI',
    control: 'outputRepository'
  }, {
    attr: 'repositoryDestination',
    control: 'outputHostFileSystem',
    depends: 'repositoryDestination/outputLocalFolder',
    disabled: !(config.enableSaveToHostFS === 'true' || config.enableSaveToHostFS === true)
  }, {
    attr: 'repositoryDestination/outputLocalFolder',
    control: 'outputToHostFileSystem',
    getter: function getter(value) {
      if (false === value) return null;
      return '';
    },
    setter: function setter(value) {
      return value === '' || !!value;
    }
  }, {
    attr: 'repositoryDestination/outputLocalFolder',
    control: 'outputHostFileSystem'
  }, {
    attr: 'repositoryDestination/outputFTPInfo/enabled',
    control: 'outputToFTPServer',
    getter: function getter(value) {
      if (value === false) {
        // clear the errors in this block in case when this block has some errors and user decided to disable it
        this.$el.find('#ftpServerOutput').find('.error').removeClass('error');
      }

      return value;
    }
  }, {
    attr: 'repositoryDestination/outputFTPInfo',
    control: 'ftpAddress, ftpDirectory, ftpUsername, ftpPassword, ftpTestButton, ftpPort, ftpProtocol, sshKeyEnabled, sshKeyPath, sshKeyPathButton, sshPassphrase',
    depends: 'repositoryDestination/outputFTPInfo/enabled'
  }, {
    attr: 'repositoryDestination/outputFTPInfo/serverName',
    control: 'ftpAddress'
  }, {
    attr: 'repositoryDestination/outputFTPInfo/folderPath',
    control: 'ftpDirectory'
  }, {
    attr: 'repositoryDestination/outputFTPInfo/userName',
    control: 'ftpUsername',
    setter: function setter(value) {
      if (value == 'anonymous') return null;
      return value;
    }
  }, {
    attr: 'repositoryDestination/outputFTPInfo/password',
    control: 'ftpPassword'
  }, {
    attr: 'repositoryDestination/outputFTPInfo/port',
    control: 'ftpPort'
  }, {
    attr: 'repositoryDestination/outputFTPInfo/type',
    control: 'ftpProtocol',
    setter: function setter(value) {
      this.$el.find('.control[data-ftp-type]').addClass('hidden');

      if (value && value === 'sftp') {
        this.$el.find('.control[data-ftp-type=sftp]').removeClass('hidden');
      }

      return value;
    }
  }, {
    attr: 'repositoryDestination/outputFTPInfo/sshKeyEnabled',
    control: 'sshKeyEnabled'
  }, {
    attr: 'repositoryDestination/outputFTPInfo/sshKey',
    control: 'sshKeyPath'
  }, {
    attr: 'repositoryDestination/outputFTPInfo/sshPassphrase',
    control: 'sshPassphrase'
  }, {
    attr: 'repositoryDestination/outputFTPInfo',
    control: 'sshKeyPath, sshKeyPathButton, sshPassphrase',
    depends: 'repositoryDestination/outputFTPInfo/sshKeyEnabled',
    setter: function setter(value) {
      return value && !!this.$el.find('[name=outputToFTPServer]').filter(':checked').map(function () {
        return $(this).val();
      }).get()[0];
    }
  }],
  // set available output formats from JRS xml config
  availableFormats: config.availableReportJobOutputFormats || [],
  // initialize view
  initialize: function initialize(options) {
    this.options = _.extend({}, options); // save link to context
    // save link to context

    var self = this;
    this.model.on('change:repositoryDestination', function (model, value) {
      var rp = model.get('repositoryDestination');
      self.$el.find('[name=ftpPort]').val(rp.outputFTPInfo.port); // self.$el.find("[name=ftpProtocol]").val(rp.outputFTPInfo.type);
    });
  },
  isFormatAvailable: function isFormatAvailable(formatName) {
    return _.contains(this.availableFormats, formatName);
  },
  getFolderChooserDialog: function getFolderChooserDialog() {
    if (this.folderChooserDialog) {
      return this.folderChooserDialog;
    }

    var self = this;
    var Dialog = RepositoryChooserDialogFactory.getDialog('folder');
    this.folderChooserDialog = new Dialog({
      treeBufferSize: parseInt(settings.treeLevelLimit, 10),
      setMinSizeAsSize: true
    });
    this.listenTo(this.folderChooserDialog, 'close', function () {
      var resourceUri;

      if (!this.folderChooserDialog.selectedResource) {
        return;
      }

      if (!this.folderChooserDialog.selectedResource.resourceUri) {
        return;
      }

      resourceUri = this.folderChooserDialog.selectedResource.resourceUri;
      self.$el.find('[name=outputRepository]').val(resourceUri).trigger('change');
    });
    this.folderChooserDialog.setDefaultSelectedItem(this.model.get('repositoryDestination').folderURI);
    return this.folderChooserDialog;
  },
  outputRepositoryButtonClick: function outputRepositoryButtonClick() {
    this.getFolderChooserDialog().open();
  },
  getSshKeyChooserDialog: function getSshKeyChooserDialog() {
    if (this.sshKeyChooserDialog) {
      return this.sshKeyChooserDialog;
    }

    var self = this;
    var Dialog = RepositoryChooserDialogFactory.getDialog('item');
    this.sshKeyChooserDialog = new Dialog({
      disableListTab: true,
      treeBufferSize: parseInt(settings.treeLevelLimit, 10),
      resourcesTypeToSelect: [repositoryResourceTypes.SECURE_FILE]
    });
    this.listenTo(this.sshKeyChooserDialog, 'close', function () {
      var resourceUri;

      if (!this.sshKeyChooserDialog.selectedResource) {
        return;
      }

      if (!this.sshKeyChooserDialog.selectedResource.resourceUri) {
        return;
      }

      resourceUri = this.sshKeyChooserDialog.selectedResource.resourceUri;
      self.$el.find('[name=sshKeyPath]').val(resourceUri).trigger('change');
    });
    var ftpInfo = this.model.get('repositoryDestination').outputFTPInfo,
        sshKeyPath = (ftpInfo || {}).sshKey;
    this.sshKeyChooserDialog.setDefaultSelectedItem(sshKeyPath);
    return this.sshKeyChooserDialog;
  },
  sshKeyPathButtonClick: function sshKeyPathButtonClick() {
    this.getSshKeyChooserDialog().open();
  },
  // This is workaround of missing validation criteria on incorrect URI format.
  fixUriInput: function fixUriInput(event) {
    var input = $(event.currentTarget),
        value = input.val();
    if (!this.model.isValidUri(value)) return; // Add "/" prefix if missing and trigger change again
    // Add "/" prefix if missing and trigger change again

    if (!(value === '' || _.startsWith(value, '/'))) {
      input.val('/' + value).trigger('change');
      return;
    } // Remove "//" in beginning
    // Remove "//" in beginning


    if (_.startsWith(value, '//')) {
      input.val(value.substring(1, value.length)).trigger('change');
      return;
    } // Remove "/" in the end
    // Remove "/" in the end


    if (value.length > 1 && _.endsWith(value, '/')) {
      input.val(value.substring(0, value.length - 1)).trigger('change');
      return;
    }
  },
  render: function render() {
    this._renderTemplate();

    this._initializeBinding();
  },
  _renderTemplate: function _renderTemplate() {
    var templateData = _.extend({}, {
      _: _,
      i18n: i18n,
      availableFormats: this.availableFormats,
      timeZones: config.timeZones,
      locales: config.avaliableLocales,
      localesName: config.avaliableLocalesFullName
    }, this.model.attributes);

    this.setElement($(_.template(outputTabTemplate, templateData)));
  },
  _initializeBinding: function _initializeBinding() {
    var self = this; // adjust this checkbox into proper state depending on the server-side variable
    // adjust this checkbox into proper state depending on the server-side variable

    this.$el.find('[name=outputToHostFileSystem]').attr('disabled', config.enableSaveToHostFS === 'true' || config.enableSaveToHostFS === true ? false : 'disabled');
    this.map = _.map(this.binding, _.clone);

    _.each(this.map, function (data) {
      // get control by name
      data.control = data.control.split(',');
      data.control = data.control.map(function (a) {
        return '[name=' + a + ']';
      });
      data.control = self.$el.find(data.control.join(','));
      if (data.attr) data.attr = data.attr.split('/');

      if (data.depends) {
        if (data.depends[0] === '!') {
          data.invert = true;
          data.depends = data.depends.substr(1);
        }
      }

      if (data.depends && !data.attr) {
        data.depends = self.$el.find('[name=' + data.depends + ']');

        var change = function change() {
          var val = data.setter ? data.setter.call(self, data.depends) : data.depends.val();
          var isDisabled = data.disabled || (data.invert ? !!val : !val);
          data.control.attr('disabled', isDisabled ? 'disabled' : false);
        };

        data.depends.on('change', change);
        return change();
      } // handle model change and update element
      // handle model change and update element


      self.model.on('change:' + data.attr[0], function (model, value) {
        value = model.value(data.attr.join('/'));
        if (data.setter) value = data.setter.call(self, value);

        if (data.depends) {
          var val = model.value(data.depends);
          if (data.setter) val = data.setter.call(self, val);else if (val === '') val = true;
          var isDisabled = data.disabled || (data.invert ? !!val : !val);
          return data.control.attr('disabled', isDisabled ? 'disabled' : false);
        }

        if (data.control.is('[type=checkbox]') && data.control.length === 1) data.control.prop('checked', value);else if (data.control.is('[type=radio]')) data.control.filter('[value=' + value + ']').prop('checked', true);else data.control.val(value);
      }); // handle element change and update model
      // handle element change and update model

      if (data.control.length && !data.depends) {
        data.control.on('change', function () {
          var key, value, update, target;
          value = data.control.is('[type=checkbox]') ? data.control.filter(':checked').map(function () {
            return $(this).val();
          }).get() : data.control.val();
          if (data.control.is('[type=checkbox]') && data.control.length === 1) value = !!value[0];
          if (data.control.is('[type=radio]')) value = data.control.filter(':checked').val();
          if (data.getter) value = data.getter.call(self, value, _.clone(self.model.value(data.attr.join('/'))));

          if (data.attr.length > 1) {
            target = update = _.clone(self.model.get(data.attr[0]));

            for (var i = 1, l = data.attr.length - 1; i < l; i++) {
              key = data.attr[i];
              target[key] = target[key] ? _.clone(target[key]) : {};
              target = target[key];
            }

            target[data.attr[data.attr.length - 1]] = value; // TODO: move this from here
            // TODO: move this from here

            if (data.attr.join('/') === 'repositoryDestination/outputFTPInfo/type') {
              target['port'] = self.model.ftpPortDefaults[value];
            }
          }

          self.model.update(data.attr[0], update || value);
        });
      }
    });
  },
  testFTPConnection: function testFTPConnection() {
    $('#ftpTestButton').addClass('disabled'); // clear errors in FTP output section
    // clear errors in FTP output section

    this.$el.find('#ftpServerOutput').find('.error').removeClass('error');
    this.model.testFTPConnection(function () {
      $('#ftpTestButton').removeClass('disabled');
    });
  }
});

});
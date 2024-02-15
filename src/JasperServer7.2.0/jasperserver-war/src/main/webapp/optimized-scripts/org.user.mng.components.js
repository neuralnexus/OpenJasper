define(["require","jquery","org.user.mng.main","mng.common.actions","common/util/encrypter","xregexp"],function(e,t,a,s,i,r){return orgModule.userManager.userList={CE_LIST_TEMPLATE_ID:"tabular_twoColumn",CE_ITEM_TEMPLATE_ID:"tabular_twoColumn:leaf",PRO_LIST_TEMPLATE_ID:"tabular_threeColumn",PRO_ITEM_TEMPLATE_ID:"tabular_threeColumn:leaf",USER_ID_PATTERN:".ID > a",USER_NAME_PATTERN:".name",USER_ORGANIZATION_PATTERN:".organization",initialize:function(e){orgModule.entityList.initialize({listTemplateId:isProVersion()?this.PRO_LIST_TEMPLATE_ID:this.CE_LIST_TEMPLATE_ID,itemTemplateId:isProVersion()?this.PRO_ITEM_TEMPLATE_ID:this.CE_ITEM_TEMPLATE_ID,onLoad:function(){orgModule.fire(orgModule.userManager.Event.SEARCH_NEXT,{})},onSearch:function(e){orgModule.fire(orgModule.userManager.Event.SEARCH,{text:e})},toolbarModel:e.toolbarModel,text:e.text}),orgModule.entityList._createEntityItem=function(e){var t=new dynamicList.ListItem({label:e.userName,value:e});return t.processTemplate=function(e){var t=e.select(orgModule.userManager.userList.USER_ID_PATTERN)[0],a=e.select(orgModule.userManager.userList.USER_NAME_PATTERN)[0];t.update(xssUtil.hardEscape(this.getValue().userName)),a.update(xssUtil.hardEscape(this.getValue().fullName));var s=this.getValue().tenantId;if(isProVersion()&&s){e.select(orgModule.userManager.userList.USER_ORGANIZATION_PATTERN)[0].update(xssUtil.hardEscape(s))}return e},t}}},orgModule.userManager.properties={USER_NAME_PATTERN:"#userName",USER_ID_PATTERN:"#propUserID",EMAIL_PATTERN:"#email",ENABLE_USER_PATTERN:"#enableUser",EXTERNAL_USER_PATTERN:"#externalUser",PASSWORD_PATTERN:"#password",PASSWORD_CONFIRM_PATTERN:"#confirmPassword",_LOGIN_AS_USER_BUTTON:"loginAsUser",user:null,initialize:function(e){orgModule.properties.initialize(e._.extend({},e,{viewAssignedListTemplateDomId:"list_type_attributes",viewAssignedItemTemplateDomId:"list_type_attributes:role",searchAssigned:!1,showAssigned:!0,attributes:{context:{urlGETTemplate:"rest_v2/attributes?includeInherited=true&holder=user:{{if (tenantId) { }}/{{-tenantId}}{{ } }}/{{-userName}}&group=custom&excludeGroup=serverSettings",urlPUTTemplate:"rest_v2{{ if (tenantId) { }}/organizations/{{-tenantId}}{{ } }}/users/{{-userName}}/attributes?_embedded=permission"}}}));var a=$(orgModule.properties._id);this.name=a.select(this.USER_NAME_PATTERN)[0],this.id=a.select(this.USER_ID_PATTERN)[0],this.email=a.select(this.EMAIL_PATTERN)[0],this.enabled=a.select(this.ENABLE_USER_PATTERN)[0],this.external=a.select(this.EXTERNAL_USER_PATTERN)[0],this.pass=a.select(this.PASSWORD_PATTERN)[0],this.confirmPass=a.select(this.PASSWORD_CONFIRM_PATTERN)[0],this.email.blurValidator=orgModule.createRegExpValidator(this.email,"invalidEmail",r(orgModule.Configuration.emailRegExpPattern)),this.pass.inputValidator=orgModule.createRegExpValidator(this.pass,"passwordIsWeak",r(orgModule.Configuration.passwordPattern)),this._validators=[this.email.blurValidator,this.pass.inputValidator,orgModule.createSameValidator(this.confirmPass,this.pass,"invalidConfirmPassword")],this._initCustomEvents();var s=e.currentUser;orgModule.properties.setProperties=function(e){var t=orgModule.userManager.properties;t.name.setValue(e.fullName),t.id.setValue(e.userName),t.email.setValue(e.email),t.enabled.checked=e.enabled,t.external.checked=e.external,t.pass.setValue(""),t.confirmPass.setValue(""),e.external?t.external.up("fieldset").removeClassName(layoutModule.HIDDEN_CLASS):t.external.up("fieldset").addClassName(layoutModule.HIDDEN_CLASS),this.setAssignedEntities(e.roles),e.enabled&&e.getNameWithTenant()!=s?buttonManager.enable(t._LOGIN_AS_USER_BUTTON):buttonManager.disable(t._LOGIN_AS_USER_BUTTON),e.getNameWithTenant()!==s?buttonManager.enable(this._DELETE_BUTTON_ID):buttonManager.disable(this._DELETE_BUTTON_ID)},orgModule.properties._deleteEntity=function(){invokeClientAction("delete",{entity:this._value})},orgModule.properties._loginAsUser=function(){invokeUserManagerAction("login",{user:this._value})},orgModule.properties._editEntity=function(){var e=orgModule.userManager.properties;this._value.external?(t(e.PASSWORD_PATTERN).parent().addClass(layoutModule.HIDDEN_CLASS),t(e.PASSWORD_CONFIRM_PATTERN).parent().addClass(layoutModule.HIDDEN_CLASS)):(t(e.PASSWORD_PATTERN).parent().removeClass(layoutModule.HIDDEN_CLASS),t(e.PASSWORD_CONFIRM_PATTERN).parent().removeClass(layoutModule.HIDDEN_CLASS)),t(e.PASSWORD_PATTERN).val(""),t(e.PASSWORD_CONFIRM_PATTERN).val(""),this.resetValidation([e.USER_NAME_PATTERN,e.EMAIL_PATTERN,e.PASSWORD_CONFIRM_PATTERN]),this.changeReadonly(!0,[e.USER_NAME_PATTERN,e.EMAIL_PATTERN]);var a=this._value.getNameWithTenant()!==s;this.changeDisable(a,[e.ENABLE_USER_PATTERN])},orgModule.properties._showEntity=function(){var e=orgModule.userManager.properties;this.resetValidation([e.USER_NAME_PATTERN,e.EMAIL_PATTERN,e.PASSWORD_CONFIRM_PATTERN]),this.changeReadonly(!1,[e.USER_NAME_PATTERN,e.EMAIL_PATTERN]),this.changeDisable(!1,[e.ENABLE_USER_PATTERN])},orgModule.properties.validate=function(){var e=orgModule.userManager.properties;return!!ValidationModule.validateLegacy(e._validators)||(dialogs.systemConfirm.show(orgModule.messages.validationErrors,2e3),!1)},orgModule.properties.isChanged=function(){var e=orgModule.userManager.properties,t=this._value,a=e._toUser(),s=orgModule.properties.attributesFacade,i=s&&s.containsUnsavedItems();return this.isEditMode&&(i||t.fullName!=a.fullName||t.email!=a.email||t.enabled!=a.enabled||t.password!=a.password||t.confirmPassword!=a.confirmPassword||this.getAssignedEntities().length>0||this.getUnassignedEntities().length>0)},orgModule.properties.save=function(){var e=orgModule.userManager.properties,t=this;e._toUser(function(e,a){t._value.tenantId&&(e.tenantId=t._value.tenantId,a.tenantId=t._value.tenantId),e.roles=t.getAssignedEntities(),a.roles=t.getAssignedEntities(),invokeServerAction("update",{entityName:t._value.getNameWithTenant(),entity:e,unencryptedEntity:a,assigned:t.getAssignedEntities(),unassigned:t.getUnassignedEntities()}),orgModule.properties.changeMode(!1)})},orgModule.properties.cancel=function(){var e=new t.Deferred;return this.setProperties(this._value),this.attributesFacade?this.attributesFacade.cancel():e.resolve()}},_initCustomEvents:function(e){var a=$(orgModule.properties._id),s=t("#moveButtons, #userEnable"),i=t("#editRoles").find("#assigned, #available");this.id.regExp=new RegExp(orgModule.Configuration.userNameNotSupportedSymbols),this.id.unsupportedSymbols=new RegExpRepresenter(orgModule.Configuration.userNameNotSupportedSymbols).getRepresentedString(),a.observe("keyup",function(e){var t=e.element();t.inputValidator&&ValidationModule.validateLegacy(t.inputValidator),t==this.id&&(ValidationModule.validateLegacy([orgModule.createInputRegExValidator(t)]),e.stop()),orgModule.properties._toggleButton()}.bindAsEventListener(this)),s.on("click",function(e){orgModule.properties._toggleButton()}),i.on("dblclick",function(){orgModule.properties._toggleButton()}),this.email.observe("blur",function(e){var t=e.element();if(t.blurValidator){if(!ValidationModule.validateLegacy(t.blurValidator))return t.inputValidator=t.blurValidator,t.focus(),!1;t.inputValidator&&(t.inputValidator=null)}}),$(orgModule.properties._BUTTONS_CONTAINER_ID).observe("click",function(e){var t=matchAny(e.element(),[layoutModule.BUTTON_PATTERN],!0),a=$(this._LOGIN_AS_USER_BUTTON);t!=a||buttonManager.isDisabled(a)||orgModule.properties._loginAsUser()}.bindAsEventListener(this))},_toUser:function(e){if(!e)return new orgModule.User({fullName:this.name.getValue(),userName:this.id.getValue(),email:this.email.getValue(),enabled:this.enabled.checked,external:this.external.checked,password:this.pass.getValue(),confirmPassword:this.confirmPass.getValue()});var a=new orgModule.User({fullName:this.name.getValue(),userName:this.id.getValue(),email:this.email.getValue(),enabled:this.enabled.checked,external:this.external.checked,password:"",confirmPassword:""});if(isEncryptionOn){var s=this,r={};r[this.pass.id]=this.pass.getValue(),r[this.confirmPass.id]=this.confirmPass.getValue(),i.encryptData(r,function(i){if(!i)throw new Error("No encrypted data found.");t("#"+s.pass.id).val(i[s.pass.id]),t("#"+s.confirmPass.id).val(i[s.confirmPass.id]);var r=new orgModule.User({fullName:s.name.getValue(),userName:s.id.getValue(),email:s.email.getValue(),enabled:s.enabled.checked,external:s.external.checked,password:i[s.pass.id]});e(r,a)})}else a.password=this.pass.getValue(),e(a,a)}},orgModule.userManager.addDialog={_ADD_USER_ID:"addUser",_ADD_BUTTON_ID:"addUserBtn",_CANCEL_BUTTON_ID:"cancelUserBtn",_FULL_NAME_ID:"addUserFullName",_USER_NAME_ID:"addUserID",_USER_EMAIL_ID:"addUserEmail",_ENABLE_USER_ID:"addUserEnableUser",_PASSWORD_ID:"addUserPassword",_CONFIRM_PASSWORD_ID:"addUserConfirmPassword",_ADD_BUTTON_TITLE_PATTERN:".wrap",initialize:function(){this.addUser=$(this._ADD_USER_ID),this.addBtn=$(this._ADD_BUTTON_ID),this.cancelBtn=$(this._CANCEL_BUTTON_ID),this.fullName=$(this._FULL_NAME_ID),this.userName=$(this._USER_NAME_ID),this.userEmail=$(this._USER_EMAIL_ID),this.enableUser=$(this._ENABLE_USER_ID),this.password=$(this._PASSWORD_ID),this.confirmPassword=$(this._CONFIRM_PASSWORD_ID),this.userName.regExp=new RegExp(orgModule.Configuration.userNameNotSupportedSymbols),this.userName.regExpForReplacement=new RegExp(orgModule.Configuration.userNameNotSupportedSymbols,"g"),this.userName.unsupportedSymbols=new RegExpRepresenter(orgModule.Configuration.userNameNotSupportedSymbols).getRepresentedString(),this.userName.inputValidator=orgModule.createInputRegExValidator(this.userName),this.userEmail.blurValidator=orgModule.createRegExpValidator(this.userEmail,"invalidEmail",r(orgModule.Configuration.emailRegExpPattern)),this.password.inputValidator=orgModule.createRegExpValidator(this.password,"passwordIsWeak",r(orgModule.Configuration.passwordPattern)),this.confirmPassword.blurValidator=orgModule.createSameValidator(this.confirmPassword,this.password,"invalidConfirmPassword"),this._validators=[orgModule.createBlankValidator(this.userName,"userNameIsEmpty"),orgModule.createBlankValidator(this.password,"passwordIsEmpty",function(e){e.element.inputValidator=null},function(e){e.element.inputValidator=e}),this.confirmPassword.blurValidator,this.userEmail.blurValidator,this.password.inputValidator,this.userName.inputValidator],this.addUser.observe("keyup",function(e){var t=e.element();t.inputValidator&&ValidationModule.validateLegacy(t.inputValidator),t==this.userName?t.changedByUser||(t.changedByUser=t.getValue()!=t.prevValue):t==this.fullName&&(this.userName.changedByUser||(this.userName.regExp.test(this.fullName.getValue())?this.userName.setValue(t.getValue().replace(this.userName.regExpForReplacement,"_")):this.userName.setValue(t.getValue())),e.stop())}.bindAsEventListener(this)),[this.userEmail,this.confirmPassword].invoke("observe","blur",function(e){var t=e.element();if(t.blurValidator){if(!ValidationModule.validateLegacy(t.blurValidator))return t.inputValidator=t.blurValidator,!1;t.inputValidator&&(t.inputValidator=null)}}),this.addUser.observe("keydown",function(e){var t=e.element();this.userName==t&&(this.userName.prevValue=this.userName.getValue())}.bindAsEventListener(this)),this.addUser.observe("click",function(e){var t=matchAny(e.element(),[layoutModule.BUTTON_PATTERN],!0);t==this.addBtn?this._doAdd():t==this.cancelBtn&&this.hide()}.bindAsEventListener(this))},show:function(e){this.organization=e,[this.userName,this.password,this.confirmPassword,this.userEmail].each(function(e){ValidationModule.hideError(e),e.changedByUser=!1});var t=this.addBtn.select(this._ADD_BUTTON_TITLE_PATTERN)[0];if(t){var a=this.organization&&!this.organization.isRoot()?orgModule.getMessage("addUserTo",{organizationName:orgModule.truncateOrgName(this.organization.id)}):orgModule.getMessage("addUser");t.update(a)}this.addBtn.title=this.organization&&!this.organization.isRoot()?orgModule.getMessage("addUserTo",{organizationName:this.organization.id}):orgModule.getMessage("addUser"),dialogs.popup.show(this.addUser,!0);try{this.fullName.focus()}catch(e){}},hide:function(){dialogs.popup.hide(this.addUser),this.userName.setValue(""),this.fullName.setValue(""),this.userEmail.setValue(""),this.enableUser.checked=!0,this.password.setValue(""),this.confirmPassword.setValue("")},_validate:function(){return ValidationModule.validateLegacy(this._validators)},_autoFill:function(){},_doAdd:function(){if(this._validate()){var e,t=this;isEncryptionOn&&(e={pswd:this.password.getValue()}),i.encryptData(e,function(e){var a=t.password.getValue();e&&e.pswd&&(a=e.pswd);var s=orgModule.Configuration.userDefaultRole,i=new orgModule.User({userName:t.userName.getValue(),fullName:t.fullName.getValue(),email:t.userEmail.getValue(),enabled:t.enableUser.checked,password:a,roles:s?[{roleName:s}]:[]});t.organization&&!t.organization.isRoot()&&(i.tenantId=t.organization.id),invokeServerAction(orgModule.ActionMap.EXIST,{entity:i,onExist:function(){ValidationModule.showError(this.userName,orgModule.messages.userNameIsAlreadyInUse)}.bind(t),onNotExist:function(){ValidationModule.hideError(t.userName),invokeServerAction(orgModule.ActionMap.CREATE,{entity:i})}.bind(t)})})}}},orgModule});
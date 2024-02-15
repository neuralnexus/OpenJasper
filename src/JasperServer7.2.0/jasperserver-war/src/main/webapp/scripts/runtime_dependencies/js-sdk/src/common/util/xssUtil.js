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

/*global window.xssUtil */
/*global __jrsConfigs__ */
/*global console */

/**
 * This is a standalone module.  jQuery,prototype, etc. - depend on this
 * @author: Borys Kolesnykov
 *
 */
;(function(factory) {
    if (typeof define === 'function' && define.amd) {
        define(["jrs.configs", "require"],factory);
    } else {
        //loading via <script> before rerquire's load in commonScripts.jsp.  This creates a global xssUtil in JRS UI.
        window.xssUtil = factory(__jrsConfigs__);
    }
})(function(jrsConfigs, require){
    var htmlTagWhiteList = 'a,abbr,acronym,address,animate,animateMotion,animateTransform,area,article,aside,b,bdi,bdo,big,blockquote,body,br,button,' +
        'canvas,caption,center,circle,cite,clipPath,code,col,colgroup,color-profile,dd,defs,desc,details,dfn,discard,div,dl,dt,ellipse,em,' +
        'feBlend,feColorMatrix,feComponentTransfer,feComposite,feConvolveMatrix,feDiffuseLighting,feDisplacementMap,feDistantLight,feFlood,feFuncA,feFuncB,feFuncG,feFuncR,feGaussianBlur,feImage,feMerge,feMergeNode,feMorphology,feOffset,fePointLight,feSpecularLighting,feSpotLight,feTile,feTurbulence,' +
        'fieldset,filter,font,footer,form,h1,h2,h3,h4,h5,h6,head,' +
        'header,hr,html,i,g,iframe,image,img,input,js-templateNonce,label,legend,li,line,linearGradient,main,map,mark,marker,mask,menu,menuitem,meta,metadata,mpath,nav,ol,option,p,path,pattern,polygon,polyline,' +
        'pre,radialGradient,rect,section,select,set,small,span,stop,strike,strong,style,sub,summary,sup,svg,switch,symbol,table,tbody,td,text,textPath,textarea,tfoot,th,thead,title,tr,tspan,u,ul,use,view';

    // None of the chars in the values of the map should appear as the map key to
    // avoid multiple escaping in a case like xssUtil.hardEscape(xssUtil.hardEscape(str))
    // Also, the escapeMap keys should NOT contain any initial values of unescapeMap.map.
    // The canonicalization would be broken.  Eg. if escapeMap has a key 'c',
    // 'javas&#99;ript' won't be canonicalized to 'javascript', because canonicExclusionMap
    // would have {'&#99;': '*&*#*9*9*;*'}. '&#99;' would be excluded from canonic.
    var escapeMap = {
        '(': '&#40;',
        ')': '&#41;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    };

    // here, we are escaping all the characters in str that are special in regex's.
    // The str returned here is used to further construct a regex as follows: (?: str ).
    // Eg. str = "a{}" results in "a\{\}" after replace().
    var makeStringRegex = function(str){
        return str == null ? '' : str.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&')
    };

    // Need to escape chars like ) and ( in order to construct correct regex later /(?: \)|\( )/g
    var regexKeys = function(map) {
        var arr = [];
        for (var k in map) {
            if (Object.prototype.hasOwnProperty.call(map, k))
                arr.push(makeStringRegex(k));
        }
        return arr;
    };

    // unescapeMap is made from reverse of escapeMap + extra chars.
    var unescapeMap = (function() {
        var map = {
            '&#111;': 'o',
            '&#110;': 'n',
            '&#115;': 's',
            '&#114;': 'r',
            '&#99;': 'c',
            '&#100;': 'd',
            '&amp;': '&'
        }, k;
        for (k in escapeMap) {
            if (Object.prototype.hasOwnProperty.call(escapeMap, k))
                map[escapeMap[k]] = k;
        }

        return map;
    })();

    //'hard' escape regex
    var hardEscapeRegex = RegExp('(?:' + regexKeys(escapeMap).join('|') + ')', 'g');

    /*
        'str' func argument should not be escaped (func returns false) if:
        - str is not a String
        - str is not an HTML; does not contain < and >
     */
    var _isHTMLString = function (str) {
        if (!(typeof(str) === 'string' || str instanceof String))
            return false;

        // not an HTML string
        if (str.indexOf('<') < 0 && str.indexOf('>') < 0)
            return false;

        return true;
    };

    // This func. takes a string on input and retuns the string obfuscated it with an input char (obfChar).
    // It intersperses the obfuscation char around all the input string chars.
    // E.g.  &#40; is converted to *&*#*4*0*;* value
    var _stringCanonicObfuscator = function (str, obfChar) {
        var obfuscatedStr = obfChar;
        for (var i = 0; i < str.length; ++i) {
            obfuscatedStr += str.charAt(i);
            obfuscatedStr += obfChar;
        }

        return obfuscatedStr;
    };

    // The keys of canonicExclusionMap are the html encoded characters that are not canonicalized during soft
    // escape (They are the values from escapeMap used in hardEscape func).  The values of the canonicExclusionMap
    // are its modified keys such that canonicalization does not decode the map keys found in the string.
    // It should be unlikely that these mutant values would be found in the application data stream.
    // If the values are found in the string by accident, they would be replaced by the map keys (danger)
    var canonicExclusionMap = (function() {
        var map = {}, key;

        for (key in escapeMap) {
            if (Object.prototype.hasOwnProperty.call(escapeMap, key)) {
                var obfuscStr = escapeMap[key];
                map[obfuscStr] = _stringCanonicObfuscator(obfuscStr, '*');
            }
        }

        //textarea needs to be obfuscated because we are inserting into <textarea> elem. to canonicalize.  Firefox breaks in this case.
        var addCanonicObfuscArr = ['<textarea', '</textarea>'];
        for (var c = 0; c < addCanonicObfuscArr.length; ++c) {
            var addObfuscStr = addCanonicObfuscArr[c];
            map[addObfuscStr] = _stringCanonicObfuscator(addObfuscStr, '*');
        }

        return map;
    })();

    var reverseCanonicExclusionMap = (function () {
        var map = {}, key;
        for (key in canonicExclusionMap) {
            if (Object.prototype.hasOwnProperty.call(canonicExclusionMap, key)) {
                map[canonicExclusionMap[key]] = key;
            }
        }

        return map;
    })();

    var canonicExclusionRegex = RegExp('(?:' + regexKeys(canonicExclusionMap).join('|') + ')', 'g');
    var reverseCanonicExclusionRegex = RegExp('(?:' + regexKeys(reverseCanonicExclusionMap).join('|') + ')', 'g');

    /**
     * Canonicalize/decode the string from any HTML encoding to ASCII.
     * Hard escaped characters listed as keys in escapeMap are not canonicalized to prevent soft
     * escape from reversing the hard escape as in xssUtil.softHtmlEscape(xssUtil.hardEscape(str))
     *
     * @param string
     * @returns canonicalized {string}
     * @private
     */
    function _canonicalize(string) {
        string = string == null ? '' : string;

        // exclude potentially hard escaped chars from escapeMap values
        string = canonicExclusionRegex.test(string) ? string.replace(canonicExclusionRegex, function(match) { return canonicExclusionMap[match]; }) : string;

        var canonicElem = document.createElement('textarea');
        canonicElem.innerHTML = string; // eslint-disable-line
        string = canonicElem.value;

        // revert the chars excluded above to their original values
        string = reverseCanonicExclusionRegex.test(string) ? string.replace(reverseCanonicExclusionRegex, function(match) { return reverseCanonicExclusionMap[match]; }) : string;

        return string;
    }

    /**
     * In visualize, jquery & xssUtil dependency are loaded before 'settings';
     * hence, it's possible for jrsConfigs.xssNonce to be undefined till jrsConfigs is
     * populated from 'settings'.
     *
     * @returns xssNonce
     * @private
     */
    function _getXssNonce() {
        if (require !== undefined && jrsConfigs.xssNonce === undefined) {
            jrsConfigs = require("jrs.configs");
        }
        return jrsConfigs.xssNonce;
    }

    /**
     * noncePrefix is used to prefix the result of xssUtil.softHtmlEscape(str, {whitelist: ['img']}), so that
     * the result is not overwritten during the subsequent call for html output -> xssUtil.softHtmlEscape(str)
     *
     * @returns nonce prefix
     * @private
     */
    function _getNoncePrefix() {
        if (_getNoncePrefix.noncePrefix === undefined) {
            var xssNonce = _getXssNonce();
            if (!xssNonce)
                return null;
            _getNoncePrefix.noncePrefix = '<!--@' + xssNonce + '@-->'
        }

        return _getNoncePrefix.noncePrefix;
    }

    /**
     * In visualize, jquery & xssUtil dependency are loaded before 'settings';
     * hence, it's possible for jrsConfigs.xssHtmlTagWhiteList to be undefined till jrsConfigs is
     * populated from 'settings'.
     *
     * @returns configured htmlTagWhiteList (on the server)
     * @private
     */
    function _getConfigHtmlTagWhitelist() {
        var configHtmlTagWhitelist = jrsConfigs.xssHtmlTagWhiteList;
        if (require !== undefined && configHtmlTagWhitelist === undefined) {
            jrsConfigs = require("jrs.configs");
            configHtmlTagWhitelist = jrsConfigs.xssHtmlTagWhiteList;
        }

        configHtmlTagWhitelist = (typeof configHtmlTagWhitelist === 'string' ? configHtmlTagWhitelist : "");
        configHtmlTagWhitelist = configHtmlTagWhitelist.replace(/\s/g,'');   //remove the spaces from xss.soft.html.escape.tag.whitelist in security-config.properties
        return configHtmlTagWhitelist;
    }

    /**
     * If htmlTagWhiteList is configured on the server, replace/modify htmlTagWhiteList with the server value.
     * if the 'configured htmlTagWhiteList' starts with +, the tags in config are added to the hard-coded htmlTagWhiteList;
     * otherwise, 'configured htmlTagWhiteList' replaces htmlTagWhiteList
     *
     * If there is no configuration on the server, return the default hard-coded htmlTagWhiteList.
     *
     * @returns htmlTagWhiteList
     * @private
     */
    function _getHtmlTagWhitelist() {
        var configHtmlTagWhitelist = _getConfigHtmlTagWhitelist();

        // if jrsConfigs.xssHtmlTagWhiteList starts with +, the tags are added to the htmlTagWhiteList;
        // o/w, jrsConfigs.xssHtmlTagWhiteList replaces htmlTagWhiteList
         if (configHtmlTagWhitelist.length > 0 && !_getHtmlTagWhitelist.whitelistInitialized) {
            htmlTagWhiteList =
                (configHtmlTagWhitelist.startsWith("+") ? htmlTagWhiteList + ',' + configHtmlTagWhitelist.substr(1) : configHtmlTagWhitelist);
             _getHtmlTagWhitelist.whitelistInitialized = true;
         }

         return htmlTagWhiteList;
    }

    /**
     * Construct left HTML tag regext from htmlTagWhiteList.
     *
     * @return {RegExp}
     * @private
     */
    function _getWhitelistLeftRegex() {
        if (_getWhitelistLeftRegex.leftTagRegexp !== undefined)
            return _getWhitelistLeftRegex.leftTagRegexp;

        var whitelist = _getHtmlTagWhitelist();
        var whitelistRegexInsert = whitelist.replace(/,/g,'\\b|');
        _getWhitelistLeftRegex.leftTagRegexp =  RegExp('<(?!/|' + whitelistRegexInsert + '\\b|!--)', 'ig');

        return _getWhitelistLeftRegex.leftTagRegexp;
    }

    /**
     * Construct right HTML tag regext from htmlTagWhiteList.
     *
     * @return {RegExp}
     * @private
     */
    function _getWhitelistRightRegex() {
        if (_getWhitelistRightRegex.rightTagRegexp !== undefined)
            return _getWhitelistRightRegex.rightTagRegexp;

        var whitelist = _getHtmlTagWhitelist();
        var whitelistRegexInsert = whitelist.replace(/,/g,'\\b|');
        _getWhitelistRightRegex.rightTagRegexp = RegExp('</(?!' + whitelistRegexInsert + '\\b)', 'ig');

        return _getWhitelistRightRegex.rightTagRegexp;
    }

    //regex map used to escape HTML tag attributes which produce javascript context.
    // During 'soft' html escape, the map keys are converted into Regexp(\b<key>\b, 'gi')
    // and replaced with the corresponding map values.
    var _defaultAttribSoftEscapeMap = {
        'regex': [
            /\bjavascript:/ig,
            /\bon(\w+?)\s*=/ig,
            /\bsrcdoc\s*=/ig
        ],
        'replacement': [
                '',
                '&#111;&#110;$1=',
                '&#115;&#114;&#99;doc='
            ]
        };

    function _getConfigAttribSoftHtmlEscapeMap() {
        var configAttribSoftHtmlEscapeMap = jrsConfigs.xssAttribSoftHtmlEscapeMap;
        if (require !== undefined && configAttribSoftHtmlEscapeMap === undefined) {
            var config = require("jrs.configs");
            configAttribSoftHtmlEscapeMap = config.xssAttribSoftHtmlEscapeMap;
            jrsConfigs.xssAttribSoftHtmlEscapeMap = configAttribSoftHtmlEscapeMap;
        }

        return configAttribSoftHtmlEscapeMap;
    }

    function _getAttribSoftHtmlEscapeMap() {
        if (_getAttribSoftHtmlEscapeMap.attribSoftEscapeArr !== undefined)
            return _getAttribSoftHtmlEscapeMap.attribSoftEscapeArr;

        var configAttribSoftHtmlEscapeMap = _getConfigAttribSoftHtmlEscapeMap();
        if (!configAttribSoftHtmlEscapeMap) {
            _getAttribSoftHtmlEscapeMap.attribSoftEscapeArr = _defaultAttribSoftEscapeMap;
            return _getAttribSoftHtmlEscapeMap.attribSoftEscapeArr;
        }

        try {
            var configRegexArr = [];
            var configReplArr = [];
            for (var k in configAttribSoftHtmlEscapeMap) {
                if (configAttribSoftHtmlEscapeMap.hasOwnProperty(k)) {
                    configRegexArr.push(RegExp( k, 'ig'));
                    configReplArr.push(configAttribSoftHtmlEscapeMap[k]);
                }
            }

            _getAttribSoftHtmlEscapeMap.attribSoftEscapeArr = {
                'regex': configRegexArr,
                'replacement': configReplArr
            };

            return _getAttribSoftHtmlEscapeMap.attribSoftEscapeArr
        }
        catch (err) {
            console.warn("Unable to parse xss.soft.html.escape.attrib.map.  Using _defaultAttribSoftEscapeMap.");
            return _getAttribSoftHtmlEscapeMap.attribSoftEscapeArr;
        }
    }

    /*
            _xssSoftHtmlEscape performs 'soft HTML escape': an escape of html such that no Javascript is executed in the browser.
            _xssSoftHtmlEscape escapes parenthesis and <.  < is escaped only if it's not followed by a valid html tag as defined by
            htmlTagWhiteList or options.whitelist.  _xssSoftHtmlEscape also escapes some HTML attributes switching the HTML context
            to Javascript.

            If the 'string' input is not an HTML or contains a session nonce, soft HTML escape is not applied.
            Session nonce marks the HTML as safe; that HTML does not require escaping against XSS.  This lets us
            mark our html templates as safe (allows to include javascript and gives a performance boost).

            Note: it's important that applying this function more than once does not escape the string multiple times.
            Multiple escapes will break UI.
            E.g. < is replaced with &lt;.  None of the chars & l t or ; should be escaped. If, say, & were replaced with
            &amp;, < would become &amp;lt; after two escapes instead of staying &lt;.

            Note2: The regular expressions are defined outside the function to improve performance.  The regex's
            persist on the page; they have an internal state parameter lastIndex (last match loc.).  It's important
            to reset lastIndex=0 before using each regex, so that the regex exec. results are correct.  Alternatively,
            one can recreate the regex on each function call.

            Parameters:
            string - string to be purged of XSS via 'soft' HTML escape.
            options.whitelist - [array] if defined, substitutes htmlTagWhiteList tags.  Tags in whitelist won't be escaped.
                                This option is useful when we want to prevent the text input rendered as HTML.
                                E.g. options.whitelist=['a'].  <a> won't be escaped. <img> will be escaped as &lt;img>.
            options.escapeTags - [array] the tags excluded from htmlTagWhiteList; those tags would be escaped.
                                E.g. options.escapeTags = ['iframe'].  <iframe > would become &lt;iframe >.
                                This option is available only when whitelist is not specified.
         */
    var _xssSoftHtmlEscape = function(string, options) {
        string = string == null ? '' : string;
        options = options || { };

        if (!_isHTMLString(string))
            return string;
        
        // The case in which options.whitelist escape was applied first; string starts with nonce.
        // Avoid the subsequent escape during html output on the page.
        // E.g. jquery.html(xssUtil.softHtmlEscape(str, {whitelist:['img']})).  If not for the following statement,
        // subsequent call to xssUtil.softHtmlEscape inside jquery.html would use the default htmlTagWhiteList
        // and would write over options.whitelist escape.  noncePrefix is an html comment to avoid showing it on the page.
        var noncePref = _getNoncePrefix();
        if (noncePref && string.indexOf(noncePref) === 0)
            return string.substring(noncePref.length);

        // If the string contains nonce (not noncePrefix in the 1st pos-n), it comes from JRS; it's safe to exec javascript.
        var xssNonce = _getXssNonce();
        if (xssNonce && string.indexOf(xssNonce) > 0)
            return string;

        string = _canonicalize(string);

        //avoid escaping < or > in <TAG> or </TAG>, where TAG is white-listed
        if (options.whiteList && options.whiteList instanceof Array && options.whiteList.length > 0) {
            //escape <TAG>
            var whitelistRegexStr = options.whiteList.join('\\b|');
            var rtLeftTagRegexp = RegExp('<(?!/|' + whitelistRegexStr + '\\b|!--)', 'ig');
            string = rtLeftTagRegexp.test(string) ? string.replace(rtLeftTagRegexp, '&lt;') : string;

            //escape </TAG>
            var rtRightTagRegexp = RegExp('</(?!' + whitelistRegexStr + '\\b)', 'ig');
            string = rtRightTagRegexp.test(string) ? string.replace(rtRightTagRegexp, '&lt;/') : string;

            string = noncePref + string;
        }
        else {
            var tmpLeftTagRegexp = _getWhitelistLeftRegex(), tmpRightTagRegexp = _getWhitelistRightRegex();
            tmpLeftTagRegexp.lastIndex = 0;
            tmpRightTagRegexp.lastIndex = 0;
   
            if (options.escapeTags && options.escapeTags instanceof Array) {
                var tmpTagWhiteList = _getHtmlTagWhitelist();
                for (var i = 0; i < options.escapeTags.length; ++i) {
                    tmpTagWhiteList = tmpTagWhiteList.replace(options.escapeTags[i] + ',', '')
                }

                tmpLeftTagRegexp = RegExp('<(?!/|' + tmpTagWhiteList.replace(/,/g, '\\b|') + '\\b|!--)', 'ig');
                tmpRightTagRegexp = RegExp('</(?!' + tmpTagWhiteList.replace(/,/g, '\\b|') + '\\b)', 'ig');
            }

            //escape <TAG>
            string = tmpLeftTagRegexp.test(string) ? string.replace(tmpLeftTagRegexp, '&lt;') : string;

            //escape </TAG>
            string = tmpRightTagRegexp.test(string) ? string.replace(tmpRightTagRegexp, '&lt;/') : string;
        }

        var attribSoftHtmlEscapeMap = _getAttribSoftHtmlEscapeMap();
        var regexArr = attribSoftHtmlEscapeMap['regex'];
        var replArr = attribSoftHtmlEscapeMap['replacement'];

        for (var k=0; k<regexArr.length; ++k) {
            var regex = regexArr[k];
            regex.lastIndex = 0;

            var replacement = replArr[k];
            string = regex.test(string) ? string.replace(regex, replacement) : string;
        }

        return string;
    };

    /*
         Unlike 'soft' HTML escape (_xssSoftHtmlEscape), this function simply escapes based on escapeMap key-value pairs.
         It will break all html and javasctipt.
     */
    var _xssHardEscape = function(string) {
        string = string == null ? '' : string;

        if (!(typeof(string) === 'string' || string instanceof String))
            return string;

        // The case in which soft html escape with options.whitelist was applied first; string starts with nonce.
        // Remove nonce prefix.  Hard escape happens anyways.
        var noncePref = _getNoncePrefix();
        if (noncePref && string.indexOf(noncePref) === 0)
            string = string.substring(noncePref.length);

        hardEscapeRegex.lastIndex = 0;
        string = hardEscapeRegex.test(string) ? string.replace(hardEscapeRegex, function(match) { return escapeMap[match]; }) : string;
        return string;
    };

    //Unescape function
    var _xssUnescape = function(string) {
        string = string == null ? '' : string;

        if (!(typeof(string) === 'string' || string instanceof String)) {
            return string;
        }

        // after soft escape with options.whitelist, the string will have a nonce prefix.
        // Remove it to unescape the string in xssUtil.unescape(xssUtil.softHtmlEscape(string, {whitelist: ['a','div']})).
        var noncePref = _getNoncePrefix();
        if (noncePref && string.indexOf(noncePref) === 0)
            string = string.substring(noncePref.length);

        // If the string contains nonce, it comes from JRS: was not escaped
        var xssNonce = _getXssNonce();
        if (xssNonce && string.indexOf(xssNonce) >= 0)
            return string;

        var unescapeRegexp = RegExp('(?:' + regexKeys(unescapeMap).join('|') + ')', 'ig');
        return unescapeRegexp.test(string) ? string.replace(unescapeRegexp, function(match) { return unescapeMap[match]; }) : string;
    };

    return {
        softHtmlEscape: _xssSoftHtmlEscape,
        hardEscape: _xssHardEscape,
        unescape: _xssUnescape
    };
});

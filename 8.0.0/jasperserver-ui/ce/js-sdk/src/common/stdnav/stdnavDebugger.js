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

/**
 * @author: ${username}
 * @version: $Id$
 */

/* Standard Navigation library (stdnav) extension
 * ------------------------------------
 * Internal debugging helper
 *
 */

import $ from 'jquery';

export default {
    // ===INTERNAL DEBUGGING FUNCTIONS=====================================
    updateDebugInfo: function(){
        if (!this.debugElementID){
            return;
        }
        // DO NOT CACHE THIS
        var $dbg=$('#'+this.debugElementID),
            $super=$('.superfocus'),
            $focus=$(document.activeElement),
            $sub=$('.subfocus'),
            txt='[sup:';
        if ($dbg.length<1){
            return;
        }
        txt+=$super.length;
        if ($super.length>0){
            txt+=':'+$super[0].nodeName+'#'+$super[0].id;
        } else {
            txt+='?';
        }
        txt+=', foc:';
        txt+=$focus.length;
        if ($focus.length>0){
            txt+=':'+$focus[0].nodeName+'#'+$focus[0].id;
        } else {
            txt+='?';
        }
        txt+=', sub:';
        txt+=$sub.length;
        if ($sub.length>0){
            txt+=':'+$sub[0].nodeName+'#'+$sub[0].id;
        } else {
            txt+='?';
        }

        // Priors
        // DO NOT CACHE THIS
        $super=$(this._priorSuperfocus),
        $focus=$(this._priorFocus),
        $sub=$(this._priorSubfocus);
        txt+=' :: psup:';
        if ($dbg.length<1){
            return;
        }
        txt+=$super.length;
        if ($super.length>0){
            txt+=':'+$super[0].nodeName+'#'+$super[0].id;
        } else {
            txt+='?';
        }
        txt+=', pfoc:';
        txt+=$focus.length;
        if ($focus.length>0){
            txt+=':'+$focus[0].nodeName+'#'+$focus[0].id;
        } else {
            txt+='?';
        }
        txt+=', psub:';
        txt+=$sub.length;
        if ($sub.length>0){
            txt+=':'+$sub[0].nodeName+'#'+$sub[0].id;
        } else {
            txt+='?';
        }

        txt+=']';
        $dbg.text(txt);
    }

}
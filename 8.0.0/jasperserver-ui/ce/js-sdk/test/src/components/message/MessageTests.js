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
import Message from 'src/components/message/Message';

var message;

var messageDefault = {
    visible: true,
    icon: false,
    text: "Text",
    title: "Title",
    type: "Info"
};

var messageOptions = {
    visible: true,
    icon: true,
    title: "Where does it come from?",
    text: "The standard chunk of Lorem Ipsum used </br> since the 1500s is reproduced below for those interested.",
    type: Message.Type.Info
};

describe("Message", function () {

    afterEach(function(){
        message.remove();
    });

    it("should heve default options", function(){
        message = new Message();

        expect(message.model.attributes).toEqual(messageDefault);
    });

    it("should set correct options", function(){
        message = new Message(messageOptions);

        expect(message.model.attributes).toEqual(messageOptions);
    });
});
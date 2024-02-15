/* 
* Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
* http://www.jaspersoft.com.
* Licensed under commercial JasperSoft Subscription License Agreement
*/

        importPackage(java.io);
        
        function writeFile(path, contents) {
            var fw = new FileWriter(path);
            fw.write(contents);
            fw.close();
        }

        print('hi, this is js');
        var appConFile = '../target/jasperserver-pro/WEB-INF/applicationContext-adhoc.xml';
        var appCon = new XML(readFile(appConFile));
        appCon.appendChild(<bean id="mybean" class="com.jaspersoft.ji.SomeBean">
            <property name="foo" value="bar"/>
        </bean>);
        writeFile('newAppCon.xml', appCon.toString());
      


  
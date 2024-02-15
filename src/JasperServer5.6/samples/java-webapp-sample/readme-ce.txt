samples/java-webapp-sample/readme-ce.txt
----------------------------------------

Sections
--------

  Run Java Webapp Sample
  Build the Java Webapp Sample Files 


Run Java Webapp Sample
----------------------

To run the java webapp sample, first locate the sample war 
file:

  <js-install>/samples/java-webapp-sample/target/jasperserver-ws-sample.war

  copy it to your application server location

  (for instance tomcat) <tomcat>/webapps

To change the Web Services URL target location:

  Config file location (in the .war file) is:

    jasperserver-ws-sample.war/WEB-INF/web.xml

  Config definition seen in web.xml is like this:

    <context-param>
      <param-name>webServiceUrl</param-name>
      <param-value>http://127.0.0.1:8080/jasperserver/services/repository</param-value>
    </context-param>

  To change the target URL location to "my-test-server":

    (Your Java JDK should be on your systems path in order to run the "jar" command)
 
    cd <js-install>/samples/java-webapp-sample/target
 
    jar xf jasperserver-ws-sample.war WEB-INF/web.xml    (extract web.xml file)
 
    edit WEB-INF/web.xml

    change param-value

      from:  127.0.0.1

      to:    my-test-server

    jar uf jasperserver-ws-sample.war WEB-INF/web.xml    (update web.xml into .war file)


(Note: Alternatively, you can use a zip utility to unpack the .war file, make your 
       edits and repack the .war file. WAR files use a zip compression format.)


Connect to the webapp like so:

  http://localhost:8080/jasperserver-ws-sample



Build the Java Webapp Sample Files 
----------------------------------

To build the java webapp sample files, you will first need to get 
the JasperReports Server source code environment in place.  

For information on how to obtain, setup, and build the JasperReports
Server source code please see the instructions in the Source Build Guide:

  docs/JasperReports-Server-CP-Source-Build-Guild.pdf

Once the source code has been built, all of the dependencies required
by the Java Webapp Samples will be in place. 

As part of the JasperReports Server source code build, you will have
used the buildomatic "auto-build" ant scripts, and you will have 
setup your default_master.properties file. 

The default_master.properties file will be found here:

  <js-ce-src>/buildomatic/default_master.properties


Next, you can build the CE Java Webapp Sample:

  cd <js-ce-src>/buildomatic

  ./js-ant build-sample-ws-app-ce


After building the CE Java Webapp Sample files, you will find output
in the following locations:

  <js-ce-src>/samples/java-webapp-sample/

             jasperserver-ws-sample-jar/target/jasperserver-ws-sample.jar

             jasperserver-ws-sample-war/target/jasperserver-ws-sample.war



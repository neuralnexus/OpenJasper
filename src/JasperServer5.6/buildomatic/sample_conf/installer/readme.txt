buildomatic/installer/readme.txt
--------------------------------

2010-09-23
----------

- added buildomatic/sample_conf/installer folder

- copied from inst-mysql_master.properties to 
  template-mysql_master.properties

- in template file, add filter tags so that these patterns
  can be replaced with information from the user at installation
  time by the installer

- the idea is that we want to get buildomatic correctly configured
  at installation-time so that it can be used by the installer 
  itself, and so that after the installation is complete, buildomatic
  will be correclty configured for the end user. 

2011-07-22
----------

- add BUILDOMATIC_DB_HOSTNAME substitution value to
  template-postgresql_master.properties    
  - this is support for installing to remote db

- removed old mysql file

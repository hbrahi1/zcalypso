================================================================================
===        Calypso application - Database Configuration      ===
================================================================================

@author 

--------------------------------------------------------------------------------

In its default configuration, Calypso uses an in-memory database (HSQLDB) which
gets populated at startup with data. A similar setup is provided for Mysql in case
a persistent database configuration is needed.
Note that whenever the database type is changed, the data-access.properties file needs to
be updated.

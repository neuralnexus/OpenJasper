@ECHO OFF

rem ///
rem /// JasperReports Server Pro script that performs migrate by updating existing database.
rem ///
rem /// Usage: js-migrate-to-split-samedb.bat {option:(<EMPTY>|help|test)}
rem ///

CALL "bin/do-js-migrate.bat" pro inDatabase %*

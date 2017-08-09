call mvn clean install
FOR /R %%G in (target\*.jar) DO copy "%%G" .
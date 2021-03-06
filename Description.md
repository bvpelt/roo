# ROO

## Database creation

``` 
# -- become root 
$ sudo /bin/bash

# -- switch to user postgres
$ su postgres

# -- create database roo
$ createdb roo

# -- start roo database
$ psql roo
psql (12.8 (Ubuntu 12.8-0ubuntu0.20.04.1))
Type "help" for help.

roo=# grant all privileges on database ambtsdb to bvpelt;
GRANT
roo=# grant all privileges on database ambtsdb to testuser;
GRANT

```

## Database commandline

```bash
$ psql -h localhost roo
```

# Liquibase

See

- https://github.com/PeterHenell/liquibase-multimodule-deploy, only deployment
- https://github.com/cardil/liquibase-multimodule, include table definitions from different modules
- https://stackoverflow.com/questions/59681305/using-project-version-with-liquibase-in-a-multimodule-maven-project

# Documentation

See

- https://maven.apache.org/plugins/maven-site-plugin/index.html

# API

See

- https://stackoverflow.com/questions/55938207/how-to-generate-openapi-3-0-spec-from-existing-spring-boot-app
- http://localhost:8080/v3/api-docs
- http://localhost:8080/swagger-ui.html

# Jar content

```bash
$ jar tvf service/target/service-application-0.0.1-SNAPSHOT.jar | awk '{ printf("%d\t%s\n", $1, $8); }'
$ jar tvf service/target/service-application-0.0.1-SNAPSHOT.jar | awk '{ print $8 }' | sort -u    
```

# Rest

- see https://spring.io/guides/tutorials/rest/

# Date

- see https://www.javacodemonk.com/java-8-date-time-json-formatting-with-jackson-5fe5ff13

# Vulnerabilities

See

- https://itnext.io/owasp-dependency-check-maven-vulnerabilities-java-898a9cf99f5e?gi=3c707c72d4e0
- https://www.geekyhacker.com/2020/01/08/how-to-configure-maven-owasp-dependency-check-plugin/

In current project execute by:

```bash
$ mvn -P owasp-dependency-check org.owasp:dependency-check-maven:check
```

# Maven

See

- https://dzone.com/articles/solving-dependency-conflicts-in-maven for resolving maven dependencie checks

proces for updating
```bash
# Step 1
# Determine state using
$ mvn enforcer:enforce

# Step 2
# If any warnings exclude specific libraries and where needed declare correct version in top of pom.xml so it will be read first
# Rerun step 1 until no more warnings

```

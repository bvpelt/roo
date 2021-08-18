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

```sql
$
psql -h localhost roo
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


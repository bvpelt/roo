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

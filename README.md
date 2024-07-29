# snowflake-hibernate

## Brief

This project is an SQL dialect definition for Hibernate Core enabling Java developers to reuse ORM features with Snowflake database.

## Hibernate Core Guides

For extensive Hibernate Core documentation refer this project page:
https://hibernate.org/orm/documentation/6.4/

## snowflake-hibernate specific usage guidelines 

### Dependencies

Ensure to add in your project necessary minimal dependencies:
  * Hibernate Core 6.4.x
  * Snowflake JDBC Driver 3.13.31+

### Features
  
| *Feature*           | *snowflake-hibernate Dialect Switch*                        | *Values*        | *Default* |           
|---------------------|-------------------------------------------------------------|-----------------|-----------|
| JDBC Driver Version | hibernate.dialect.snowflake.allow_unrecommended_jdbc_driver | false/true      | false     | 
| Table Type          | hibernate.dialect.snowflake.table_type                      | HYBRID/STANDARD | HYBRID    |
| Logging             | hibernate.dialect.snowflake.development_mode                | false/true      | false     |

#### JDBC Driver Version 

| Always use latest driver version and look for updates |
|-------------------------------------------------------|

Make sure to use latest, vulnerability-free JDBC driver version:  
https://mvnrepository.com/artifact/net.snowflake/snowflake-jdbc
and not older than 3.13.31 (initial version with ORM-specific required features 
necessary for OLTP operations: [HTAP](https://www.snowflake.com/guides/htap-hybrid-transactional-and-analytical-processing/)

If for any reason you are forced to use unrecommended, non-latest version of the driver it might not work properly and if a version is 
lower than the lowest recommended you'll notice an exception:
`Using driver in version X.YY.ZZ must be forced - recommended driver version should be at least 3.13.31`
To force using such a version you need to toggle the switch `hibernate.dialect.snowflake.allow_unrecommended_jdbc_driver=true`.

#### Table Type

| Use HTAP tables for better performance and OLTP-features |
|----------------------------------------------------------|

ORM solutions are supposed to work on tables enforcing unique and foreign keys thus using it along with 
[HTAP](https://www.snowflake.com/guides/htap-hybrid-transactional-and-analytical-processing/)-ready tables is necessary.
In some cases you may need to force using STANDARD tables with a Snowflake-Hibernate dialect switch:
`hibernate.dialect.snowflake.table_type=STANDARD`
This may however cause multiple issues with some queries execution or in some cases result in data corruption or uniqueness/foreign keys issues.
Developers might still want to use this project on OLAP (STANDARD) tables within OLTP projects.  

You can learn about differences between the above two kinds of tables here: https://docs.snowflake.com/en/user-guide/tables-hybrid
Although Hybrid tables are a perfect choice for your OLTP, ORM-ready solutions they have some limitations:
https://docs.snowflake.com/en/user-guide/tables-hybrid-limitations

### Logging

| Do not enable logging bindings on productions environment |
|-----------------------------------------------------------| 

For debug purposes you may want to use extensive logging using hibernate switches: `org.hibernate.orm.jdbc.bind` or `org.hibernate.orm.jdbc.extract`. 
Be aware that those kind of logs may disclose user names, passwords and other secrets within your application domain and your executed query requests. 

If you turn on the above logging you must ensure that development_mode is set:  
`hibernate.dialect.snowflake.development_mode=true`
Otherwise, you'll encounter below errors in the log:
* `Statement parameter bindings logging is enabled - it's recommended to turn it off on production environment`
* `Extracted in select data logging is enabled - it's recommended to turn it off on production environment`

# Developer Guides

## Run tests

Add database access configuration as snowflake properties

```bash
export SNOWFLAKE_TEST_ACCOUNT=...
export SNOWFLAKE_TEST_DATABASE=...
export SNOWFLAKE_TEST_PASSWORD=...
export SNOWFLAKE_TEST_SCHEMA=...
export SNOWFLAKE_TEST_USER=...
export SNOWFLAKE_TEST_WAREHOUSE=...
```

Run maven tests:

```bash
./mvnw clean test
```

Run performance tests:

```bash
./mvnw clean test -Pperf
```

### Run Spring Boot samples

**SpringBoot 3.2.0 that supports Hibernate 6.4 needs Java 17 whereas Hibernate 6.4 needs Java 11, so we want to build `snowflake-hibernate` with Java 11 but test springboot with Java 17**  

1. Set `SNOWFLAKE_TEST_*` environment variables
2. Set java version to 11 (e.g. using sdkman)
3. Build snowflake-hibernate `./mvnw clean package`
4. Install snowflake-hibernate in private maven repo
   ```
   ./mvnw org.apache.maven.plugins:maven-install-plugin:3.1.1:install-file -Dfile=./snowflake-hibernate/target/snowflake-hibernate-*-SNAPSHOT.jar -DpomFile=./snowflake-hibernate-pom.xml
   ```
   
#### Spring Boot samples

1. Go to [sample/springboot](sample/springboot-hibernate) `cd ./sample/springboot`
2. Set java version to 17 (e.g. using sdkman)
3. Run tests: `../../mvnw clean test`

#### Spring Boot with Flyway samples

1. Go to [sample/springboot-flyway](sample/springboot-hibernate) `cd ./sample/springboot-flyway`
2. Set java version to 17 (e.g. using sdkman)
3. Run tests: `../../mvnw clean test`

## Prepare distribution for PrPr

Run `./buildPrPrArtifact.sh`

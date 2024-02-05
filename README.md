# snowflake-hibernate

## Hibernate Core Guides

https://hibernate.org/orm/documentation/6.4/

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

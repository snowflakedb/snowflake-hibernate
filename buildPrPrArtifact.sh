#!/bin/bash -e

version=0.0.2
artifact_name=snowflake-hibernate
target_dir=dist
target_artifact=${artifact_name}-${version}.zip

rm -rf ${target_dir}
mkdir -p ${target_dir}

./mvnw clean package
cp ${artifact_name}/target/${artifact_name}-${version}-SNAPSHOT.jar ${target_dir}/${artifact_name}.jar
cp ${artifact_name}-pom.xml ${target_dir}

cd $target_dir
zip -r ${target_artifact} *
cd -
rm -r ${target_dir}/*.jar ${target_dir}/*.xml

echo Created ${target_dir}/${target_artifact}
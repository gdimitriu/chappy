#!/bin/bash
 aws s3 cp s3://gabrieldimitriuaxway/chappy-distribution-1.0.0-SNAPSHOT.zip c.zip
 unzip c.zip
 yum install java-1.8.0-openjdk.x86_64 -y
 cd chappy/bin
 java8 -jar chappy-program.jar ../config/SystemConfiguration.xml

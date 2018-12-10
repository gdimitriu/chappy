#!/bin/bash

exec > /tmp/start.log  2>&1
yum install java-1.8.0-openjdk.x86_64 -y
cd /home/ec2-user/
aws s3 cp s3://gabrieldimitriuaxway/chappy-distribution-1.0.0-SNAPSHOT.zip c.zip
unzip c.zip
chown -R ec2-user chappy
chgrp -R ec2-user chappy
cd chappy/bin
su ec2-user -c "java8 -jar chappy-program.jar ../config/SystemConfiguration.xml"


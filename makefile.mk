#seperate control
#e.x, in order to debug locally, : make -f makefile.mk standalone
#for sbt: sbt package
#for maven: mvn clean package
#for maven: mvn scala:run -DmainClass=TestScala
spark="/Users/yzh/Desktop/cour/parallel/spark-2.3.0-bin-hadoop2.7"
job.name=TestScala
local.master=local[4]
app.name=brain
jar.name=/Users/yzh/IdeaProjects/mlSpark/target/scala-2.11/spark_2.11-0.1.jar
pArgs="/Users/yzh/Desktop/cour/parallel/brain/files/sample1.csv copy.bz2" "/Users/yzh/Desktop/cour/parallel/brain/sample5.bz2" "/Users/yzh/Desktop/cour/parallel/brain/output2"

awsJar=spark_2.11-0.1.jar
aws.inputTrain=train
aws.inputTest=test
aws.output=outputbrain
aws.bucket.name=michaelyangcs
aws.release.label=emr-5.11.1
aws.instance.type=m4.large
aws.num.nodes=19
aws.log.dir=logBrain2

awsoutput="s3://michaelyangcs/workFold/output10"
localout="/Users/yzh/Desktop/cour/parallel/RankOutput"

.PHONY:build
build:
	cd ${project}; sbt package;

.PHONY:standalone
standalone:
	cd ${spark}; bin/spark-submit --class ${job.name} --master ${local.master} --name "${app.name}" ${jar.name} ${pArgs}


.PHONY:awsrun
awsrun:
	aws emr create-cluster \
		--name "Wiki Spark Cluster" \
		--release-label ${aws.release.label} \
		--instance-groups '[{"InstanceCount":${aws.num.nodes},"InstanceGroupType":"CORE","InstanceType":"${aws.instance.type}"},{"InstanceCount":1,"InstanceGroupType":"MASTER","InstanceType":"${aws.instance.type}"}]' \
	    --applications Name=Hadoop Name=Spark \
		--steps Type=CUSTOM_JAR,Name="${app.name}",Jar="command-runner.jar",ActionOnFailure=TERMINATE_CLUSTER,Args=["spark-submit","--deploy-mode","cluster","--class","${job.name}","s3://${aws.bucket.name}/${awsJar}","s3://${aws.bucket.name}/${aws.inputTrain}","s3://${aws.bucket.name}/${aws.inputTest}","s3://${aws.bucket.name}/${aws.output}","${num.iter}","${k}"] \
		--log-uri s3://${aws.bucket.name}/${aws.log.dir} \
		--use-default-roles \
		--ec2-attributes SubnetId=subnet-520b7f0f \
		--enable-debugging \
		--auto-terminate

.PHONY:sync
sync:
	aws s3 sync ${awsoutput} ${localout}


.PHONY:move
move:
	aws s3 mv s3://michaelyangcs/input s3://michaelyangcs/workFold/input --recursive


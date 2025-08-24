# Start docker services
In the project folder:
To create images and containers
```bash
docker-compose pull
```
To start the containers
```bash
docker-compose up -d
```
---
To check the active containers
```bash
docker ps
```
You should see containers with name like:
jupyterlab
datanode
namenode
spark-master
spark-worker
---

# Compile scala codes and make packages
After making changes in preprocessing scala code or creating a new one, run the following cmd in the project directory
```bash
sbt package
```
This generates a JAR file in the directory target/scala-2.12/<amazonreviewspreprocessing_2.12-0.1.jar>
---

# Copy the JAR into spark container
```bash
docker cp .\target\scala-2.12\amazonreviewspreprocessing_2.12-0.1.jar spark-master:/opt/bitnami/spark/
```
---

# Copy the data and put it into HDFS 
Create a directory 'data' in the namenode container
```bash
docker exec -it namenode mkdir -p /data
```
Copy the dataset from local and put into container
```bash
docker cp .\data\<filename> namenode:/data/
```
To go to the bash of hadoop namenode container (better to use another new terminal say Terminal 2)
```bash
docker exec -it namenode bash
```
Inside that bash
```bash
hdfs dfs -mkdir -p /data
hdfs dfs -put /data/<filename> /data/
```
To check if the uploaded data is there or not
```bash
hdfs dfs -ls /data
```
Create output directory and give access to spark
```bash
hdfs dfs -mkdir -p /output
hdfs dfs -chmod 777 /output
```
---

# Run preprocessing job
To go to the bash of spark-master container (better to use another new terminal say Terminal 3)
```bash
docker exec -it spark-master bash
```
To run the preprocessing scala code (for spark-shell)
```bash
spark-submit \
  --class PreprocessReviews \
  /opt/bitnami/spark/amazonreviewspreprocessing_2.12-0.1.jar \
  /data/<filename> \
  hdfs://namenode:9000/output/cleaned_reviews
```
---
# To check in hadoop namenode
in terminal 2
```bash
hdfs dfs -ls /output/cleaned_reviews
```
---
# To check the output in spark shell
(say terminal 4)
open a new terminal and go into the spark-master container's bash
```bash
docker exec -it spark-master bash
```
go into the spark shell
```bash
spark-shell
```
enter the following commands to see the output of preprocessed review
```bash
val df = spark.read.parquet("hdfs://namenode:9000/output/cleaned_reviews")
df.printSchema()
df.show(10)
```
---
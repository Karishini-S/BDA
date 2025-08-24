# ⚙️ Project Setup & Execution Guide

This guide walks through the complete setup and execution steps for the **Multi-Modal Fake Review and Product Scam Detection System**, including Docker services, Scala preprocessing, HDFS integration, and Spark job execution.

---

## 🚀 Start Docker Services

In the project root directory:

### 🔹 Pull Required Images
```bash
docker-compose pull
```

### 🔹 Start All Containers
```bash
docker-compose up -d
```

### 🔹 Verify Active Containers
```bash
docker ps
```

You should see containers named:
- `jupyterlab`
- `datanode`
- `namenode`
- `spark-master`
- `spark-worker`

---

## 🛠️ Compile Scala Code

After modifying or creating a new Scala preprocessing job:

### 🔹 Package the Scala Code
```bash
sbt package
```

This generates a JAR file at:
```
target/scala-2.12/amazonreviewspreprocessing_2.12-0.1.jar
```

---

## 📦 Copy JAR into Spark Container

Transfer the compiled JAR into the Spark Master container:
```bash
docker cp ./target/scala-2.12/amazonreviewspreprocessing_2.12-0.1.jar spark-master:/opt/bitnami/spark/
```

---

## 📁 Upload Dataset to HDFS

### 🔹 Create Local Directory in Namenode
```bash
docker exec -it namenode mkdir -p /data
```

### 🔹 Copy Dataset from Host to Namenode
```bash
docker cp ./data/<filename> namenode:/data/
```

### 🔹 Access Namenode Bash (Terminal 2 Recommended)
```bash
docker exec -it namenode bash
```

### 🔹 Inside Namenode Bash
```bash
hdfs dfs -mkdir -p /data
hdfs dfs -put /data/<filename> /data/
```

### 🔹 Verify Upload
```bash
hdfs dfs -ls /data
```

### 🔹 Create Output Directory for Spark
```bash
hdfs dfs -mkdir -p /output
hdfs dfs -chmod 777 /output
```

---

## 🧪 Run Preprocessing Job

### 🔹 Access Spark Master Bash (Terminal 3 Recommended)
```bash
docker exec -it spark-master bash
```

### 🔹 Submit Spark Job
```bash
spark-submit \
  --class PreprocessReviews \
  /opt/bitnami/spark/amazonreviewspreprocessing_2.12-0.1.jar \
  /data/<filename> \
  hdfs://namenode:9000/output/cleaned_reviews
```

---

## 📂 Verify Output in HDFS

In Terminal 2 (namenode bash):
```bash
hdfs dfs -ls /output/cleaned_reviews
```

---

## 🔍 Inspect Output in Spark Shell

### 🔹 Access Spark Master Bash (Terminal 4 Recommended)
```bash
docker exec -it spark-master bash
```

### 🔹 Launch Spark Shell
```bash
spark-shell
```

### 🔹 Load and View Preprocessed Data
```scala
val df = spark.read.parquet("hdfs://namenode:9000/output/cleaned_reviews")
df.printSchema()
df.show(10)
```

---

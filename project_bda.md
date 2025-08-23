# 🛡️ Multi-Modal Fake Review and Product Scam Detection System

## 📖 Overview

This project leverages the [Amazon Reviews 2023 dataset](https://huggingface.co/datasets/McAuley-Lab/Amazon-Reviews-2023) curated by McAuley Lab (UCSD), containing over **571 million** product reviews across diverse categories. Each review includes structured metadata, textual content, and product images.

Due to the dataset's scale, we utilize a **2GB subset** for preprocessing, analysis, and modeling, while maintaining compatibility with full-scale distributed pipelines.

### 🎯 Project Objectives

- Preprocess large-scale review data (text + metadata + product images)
- Analyze rating distributions, verified vs non-verified purchase behaviors, and textual sentiments
- Build ML/NLP models to identify patterns such as fake vs genuine reviews
- Deploy a scalable pipeline using Apache Spark (Scala + Python + SQL)

---

## 📂 Dataset Description

- **Source**: [Hugging Face – Amazon Reviews 2023](https://huggingface.co/datasets/McAuley-Lab/Amazon-Reviews-2023)
- **Format**: JSONL (JSON per line)
- **Modalities**: Textual data, review metadata, thumbnails, high-resolution product images

### 🧾 Sample Review (from "All Beauty" category)

```json
{
  "rating": 5.0,
  "title": "Such a lovely scent but not overpowering.",
  "text": "This spray is really nice. It smells really good, goes on really fine, and does the trick...",
  "images": [],
  "asin": "B00YQ6X8EO",
  "parent_asin": "B00YQ6X8EO",
  "user_id": "AGKHLEW2SOWHNMFQIJGBECAF7INQ",
  "timestamp": 1588687728923,
  "helpful_vote": 0,
  "verified_purchase": true
}
```

### 🔍 Key Features

- **Review Metadata**: rating, title, text, verified purchase flag, helpful votes, timestamp  
- **Product Metadata**: ASIN, parent ASIN, category  
- **User Metadata**: anonymized reviewer IDs  
- **Images**: thumbnails and high-resolution product visuals  

---

## 🛠️ Tech Stack

| Component            | Purpose                                      |
|---------------------|----------------------------------------------|
| Apache Spark         | Distributed data preprocessing & ML pipelines |
| Scala                | High-performance ETL jobs                    |
| PySpark (Python)     | ML/NLP modeling and visualization            |
| Spark SQL            | Structured querying                          |
| HDFS / Parquet       | Efficient data storage                       |
| Hugging Face + Spark NLP | Text embeddings & sentiment analysis     |
| Docker + Compose     | Reproducible Spark cluster setup             |

---

## ⚙️ Setup & Installation

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/your-username/amazon-reviews-bigdata.git
cd amazon-reviews-bigdata
```

### 2️⃣ Start Dockerized Spark Cluster

```bash
docker-compose up -d
```

This launches:

- Spark Master → `localhost:8080`  
- Spark Worker(s)  
- JupyterLab → `localhost:8888`  
- Scala + sbt environment  

### 3️⃣ Scala Preprocessing Job

```bash
sbt package
spark-submit \
  --class PreprocessReviews \
  target/scala-2.12/amazon-reviews_2.12-0.1.jar \
  /data/reviews.jsonl /output/cleaned_reviews.parquet
```

### 4️⃣ Python ML Pipeline

```bash
docker exec -it jupyterlab bash
python notebooks/model_phase1.py
```

---

## 📁 Repository Structure

```
📦 amazon-reviews-bigdata
 ┣ 📂 data/                  # Raw JSONL dataset (mounted)
 ┣ 📂 output/                # Cleaned Parquet files
 ┣ 📂 notebooks/             # Python ML + Visualization
 ┣ 📂 src/main/scala/        # Scala preprocessing jobs
 ┣ 📜 docker-compose.yml     # Spark + Jupyter cluster config
 ┣ 📜 build.sbt              # Scala build definition
 ┣ 📜 requirements.txt       # Python dependencies
 ┗ 📜 README.md
```

---

## ✅ Project Milestones

### Phase 1: Data Preprocessing
- Load JSONL reviews into Spark  
- Clean & filter missing/invalid entries  
- Convert to Parquet for efficient querying  

### Phase 2: Exploratory Data Analysis
- Ratings distribution  
- Verified vs non-verified purchases  
- Temporal trends  

### Phase 3: Modeling
- Text embeddings (BERT/Sentence Transformers)  
- Fake vs genuine review classification  
- Sentiment analysis  

### Phase 4: Visualization
- User/product distributions  
- Rating & sentiment heatmaps  
- Comparative dashboards  

---

## 🔒 Code

- ✅ Fully Dockerized Spark cluster

---

## 📊 Dataset Categories Used

| Category                  | Reviews (R) | Metadata (M) |
|--------------------------|-------------|--------------|
| All_Beauty               | 311         | 203          |
| Health_and_Personal_Care | 216         | 115          |
| Handmade_Products        | 275         | 380          |

---

## 📜 License
Dataset © McAuley Lab (UCSD) – used for research purposes only.

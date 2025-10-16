# 🛡️ Multi-Modal Fake Review and Product Scam Detection System

## 📖 Overview

This project addresses the challenge of detecting fake reviews and product scams using the [Amazon Reviews 2023 dataset](https://huggingface.co/datasets/McAuley-Lab/Amazon-Reviews-2023) curated by McAuley Lab (UCSD). The dataset contains over **571 million** reviews across diverse product categories, each enriched with textual content, structured metadata, and user-uploaded images.

To manage this scale, we use a **representative subset** for development and testing, while ensuring the pipeline is scalable to full distributed processing using Apache Spark and Hadoop.

---

## 🎯 Project Objectives

- Build a robust preprocessing pipeline for multimodal review data (text, metadata, images)
- Analyze behavioral patterns such as rating distributions and verified purchase trends
- Engineer features from text, images, and metadata for machine learning
- Train models to classify fake vs genuine reviews
- Deploy a scalable, reproducible big data pipeline using Spark and HDFS

---

## 📂 Dataset Description

### 🔹 Data Source & Scale
- **Source**: [Hugging Face – Amazon Reviews 2023](https://huggingface.co/datasets/McAuley-Lab/Amazon-Reviews-2023)
- **Format**: JSONL (JSON per line)
- **Total Scale**: 571+ million reviews
- **Development Subset**: 2GB representative sample

### 🔹 Data Modalities

- **Review Data**: Includes rating, title, review text, timestamp, verified purchase flag, helpful votes, and user-uploaded images
- **Product Metadata**: Contains product title, description, brand, price, categories, and official product images
- **User Metadata**: Anonymized reviewer IDs

### 🔹 Sample Review Structure

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

### 🔹 Key Features

- **Review Metadata**: rating, title, text, verified purchase flag, helpful votes, timestamp  
- **Product Metadata**: ASIN, parent ASIN, category, brand, price  
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

## ⚙️ Quick Start & Setup

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

### 3️⃣ Run Scala Preprocessing Pipeline

```bash
sbt package
spark-submit \
  --class PreprocessReviews \
  target/scala-2.12/amazon-reviews_2.12-0.1.jar \
  /data/reviews.jsonl /output/cleaned_reviews.parquet
```

### 4️⃣ Execute Python ML Pipeline

```bash
docker exec -it jupyterlab bash
python notebooks/model_phase1.py
```

---

## 🧠 Methodology & Pipeline

### Phase 1: Data Ingestion

- Load raw JSONL files using Apache Spark
- Separate ingestion for review data and product metadata
- Validate schema and ensure compatibility across categories

### Phase 2: Review Preprocessing

- Drop reviews missing critical fields: rating, text, asin, user_id
- Filter out ratings outside the valid range (1.0 to 5.0)
- Convert timestamp from Unix epoch to human-readable format
- Normalize review text:
  - Lowercase conversion
  - Removal of HTML tags and special characters
  - Optional stopword removal for NLP tasks
- Replace null or missing titles with empty strings

### Phase 3: Image Extraction

- From the `images` array in each review, extract the best available image URL
- Priority order: large → medium → small
- Create a new column `best_image_urls` containing a list of usable image URLs
- Ensure consistent structure: empty list for reviews with no images

### Phase 4: Metadata Preprocessing

- Load product metadata JSONL files
- Extract relevant fields: title, description, brand, price, categories, images
- Normalize descriptions and flatten nested categories
- Extract main product image URLs for downstream visual analysis

### Phase 5: Join Reviews with Metadata

- Perform a left join on `asin` to enrich review data with product-level context
- Enables multimodal feature fusion for modeling

### Phase 6: Structured Output

- Select final fields required for modeling:
  - rating, title, text, asin, user_id, helpful_vote, verified_purchase, timestamp, best_image_urls
  - brand, price, categories, product image
- Save cleaned and enriched dataset in Parquet format
- Store output in HDFS for distributed access and scalability

---

## 🔍 Feature Engineering Strategy

### Text Features

- Generate embeddings using BERT or Sentence-BERT for title and review text
- Extract sentiment scores and linguistic features
- Analyze review length, readability scores, and language patterns

### Image Features

- Download images from `best_image_urls`
- Use CNN architectures (e.g., ResNet, CLIP) to extract visual embeddings
- Compare user-uploaded images with official product images

### Metadata Features

- Analyze price anomalies, brand frequency, and category distributions
- Use verified purchase flag and helpful vote count as behavioral indicators
- Extract temporal patterns and review frequency analysis

### Multimodal Fusion

- Combine text, image, and metadata features into unified vectors
- Apply attention mechanisms for feature weighting
- Feed into classification models for fake review detection

---

## 📊 Exploratory Data Analysis

- Visualize rating distributions across categories
- Compare verified vs non-verified purchase behaviors
- Analyze temporal trends in review activity
- Identify outliers and suspicious review patterns
- Study correlation between helpful votes and review authenticity

---

## 🧰 Infrastructure & Scalability

### Hadoop & HDFS Integration

- Store raw and processed data in HDFS for fault-tolerant, distributed access
- Use Hadoop YARN for resource management in Spark jobs
- Enable horizontal scalability for full dataset processing

### Dockerized Environment

- Containerized setup includes Spark Master, Spark Workers, Hadoop HDFS, and JupyterLab
- Ensures reproducibility and ease of deployment across environments
- Supports both development and production workflows

---

## 📁 Repository Structure

```
📦 amazon-reviews-bigdata
 ┣ 📂 data/                  # Raw JSONL dataset files
 ┣ 📂 output/                # Cleaned and enriched Parquet files
 ┣ 📂 notebooks/             # Python notebooks for modeling and visualization
 ┣ 📂 src/main/scala/        # Scala ETL jobs for preprocessing
 ┣ 📜 docker-compose.yml     # Spark cluster configuration
 ┣ 📜 build.sbt              # Scala build definition
 ┣ 📜 requirements.txt       # Python dependencies
 ┗ 📜 README.md
```

---

## ✅ Project Milestones

| Phase        | Description                                                  |
|--------------|--------------------------------------------------------------|
| Preprocessing| Clean, normalize, and enrich review data                     |
| EDA          | Analyze rating, sentiment, and behavioral trends             |
| Modeling     | Train ML models using multimodal features                    |
| Visualization| Build dashboards for insights and anomaly detection          |

### Phase 1: Data Preprocessing
- Load JSONL reviews into Spark  
- Clean & filter missing/invalid entries  
- Convert to Parquet for efficient querying  

### Phase 2: Exploratory Data Analysis
- Ratings distribution analysis
- Verified vs non-verified purchase patterns
- Temporal trends and seasonality effects

### Phase 3: Machine Learning & Modeling
- Text embeddings (BERT/Sentence Transformers)  
- Fake vs genuine review classification  
- Sentiment analysis and anomaly detection

### Phase 4: Visualization & Insights
- Interactive dashboards for pattern discovery
- Rating & sentiment heatmaps  
- Comparative analysis across product categories

---

## 📊 Dataset Categories Used

| Category                 | Reviews (GB) | Metadata (GB) | Size (GB) |
|--------------------------|---------|----------|-----------|
| All_Beauty               | 0.31     | 0.2      | ~0.5       |
| Baby_Products            | 2.74     | 0.65     | ~3.39      |
| Handmade_Products        | 0.31     | 0.38     | ~0.7       |
| Health_and_Personal_Care | 0.22     | 0.1      | ~0.3       |
| Software                 | 1.74     | 0.25     | ~2         |
| Video_games              | 2.49     | 0.42     | ~2.91      |
| **Total Development Set**| **~7.81** | **~2**  | **~9.8**   |

- Total size (in GB):  **~9.8**
---

## 🔒 Security & Privacy

- All user IDs are anonymized in the original dataset
- No personally identifiable information (PII) is processed
- Compliance with data protection best practices

---

## 📈 Expected Outcomes

- Robust multimodal fake review detection system
- Scalable big data pipeline for e-commerce fraud detection
- Insights into review authenticity patterns across product categories
- Framework extensible to other review platforms

---

## 📜 License

Dataset © McAuley Lab (UCSD) – used for academic and research purposes only.

---
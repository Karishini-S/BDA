from pyspark.sql import SparkSession
from pyspark.sql.functions import col

# Initialize Spark
spark = SparkSession.builder \
    .appName("AmazonReviewPreprocessing") \
    .getOrCreate()

# Input & Output Paths (mounted in Docker)
input_path = "/data/All_Beauty.jsonl"
output_path = "/output/cleaned_reviews.parquet"

# Load JSONL
df = spark.read.json(input_path, multiLine=False)

print("Schema:")
df.printSchema()

# Select relevant columns
df = df.select(
    col("rating").cast("double"),
    col("title"),
    col("text"),
    col("verified_purchase"),
    col("helpful_vote"),
    col("asin"),
    col("user_id"),
    col("timestamp")
)

# Drop missing/null values
df = df.dropna(subset=["rating", "text", "asin", "user_id"])

# Filter invalid ratings
df = df.filter((col("rating") >= 1.0) & (col("rating") <= 5.0))

# Save as Parquet
df.write.mode("overwrite").parquet(output_path)

print(f"Preprocessing complete. Saved to {output_path}")

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object PreprocessReviews {
  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      System.err.println("Usage: PreprocessReviews <input> <output>")
      System.exit(1)
    }

    val inputPath = args(0)
    val outputPath = args(1)

    val spark = SparkSession.builder()
      .appName("AmazonReviewPreprocessing")
      .getOrCreate()

    // Load JSONL
    val df = spark.read
      .option("multiLine", "false")
      .json(inputPath)

    // Select relevant columns and cast
    val cleaned = df.select(
      col("rating").cast("double"),
      col("title"),
      col("text"),
      col("verified_purchase"),
      col("helpful_vote"),
      col("asin"),
      col("user_id"),
      col("timestamp")
    ).na.drop(Seq("rating", "text", "asin", "user_id"))
      .filter(col("rating").between(1.0, 5.0))

    // Save as Parquet
    cleaned.write.mode("overwrite").parquet(outputPath)

    println(s"Preprocessing complete. Saved to $outputPath")

    spark.stop()
  }
}

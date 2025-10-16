import org.apache.spark.sql.{SparkSession, DataFrame, Row}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

// Tail recursion function to select best image URL
object ImageSelector {
  @annotation.tailrec
  def getBestImage(images: Seq[Row], priorities: List[String]): Option[String] = {
    priorities match {
      case Nil => None
      case head :: tail =>
        val found = images.flatMap { img =>
          Option(img.getAs[String](head))
        }.find(url => url != null && url.nonEmpty)
        if (found.isDefined) found else getBestImage(images, tail)
    }
  }
}

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

    import spark.implicits._

    // UDF to clean text (lowercase, remove HTML tags and special chars)
    val cleanTextUDF = udf { s: String =>
      if (s == null) ""
      else s.toLowerCase
        .replaceAll("<[^>]*>", " ") // remove HTML tags
        .replaceAll("[^\\w\\s.,!?]", " ") // remove special chars except punctuation
        .replaceAll("\\s+", " ") // normalize whitespace
        .trim
    }

    // UDF to select best image URL using tail recursion
    val bestImageUDF = udf { images: Seq[Row] =>
      if (images == null || images.isEmpty) null
      else ImageSelector.getBestImage(images, List("large_image_url", "medium_image_url", "small_image_url")).orNull
    }

    // UDF for has_image
    val hasImageUDF = udf { images: Seq[Row] =>
      images != null && images.nonEmpty
    }

    // Read JSONL
    val df = spark.read
      .option("multiLine", "false")
      .json(inputPath) 

    // Preprocessing pipeline
    val cleaned = df

      // Drop rows missing critical fields
      .na.drop(Seq("rating", "text", "asin", "user_id"))
      // Filter valid rating range
      .filter(col("rating").between(1.0, 5.0))
      // Clean and normalize text fields
      .withColumn("text", cleanTextUDF(col("text")))
      .withColumn("title", when(col("title").isNull, "").otherwise(cleanTextUDF(col("title"))))
      // Convert timestamp to readable date (optional)
      .withColumn("date", from_unixtime((col("timestamp")/1000).cast("long")))
      // Ensure helpful_vote is integer and fill nulls with 0
      .withColumn("helpful_vote", coalesce(col("helpful_vote").cast("int"), lit(0)))
      // Ensure verified_purchase is boolean
      .withColumn("verified_purchase", col("verified_purchase").cast("boolean"))
      // Image extraction
      .withColumn("has_image", hasImageUDF(col("images")))
      .withColumn("final_best_image_url", bestImageUDF(col("images")))
      // Select only required columns
      .select(
        col("rating").cast("double"),
        col("title"),
        col("text"),
        col("asin"),
        col("user_id"),
        col("helpful_vote"),
        col("verified_purchase"),
        col("timestamp"),
        col("date"),
        col("has_image"),
        col("final_best_image_url")
      )

    cleaned.write.mode("overwrite").parquet(outputPath)

    println(s"*********** Preprocessing complete. Saved to $outputPath ***********")

    spark.stop()
  }
}

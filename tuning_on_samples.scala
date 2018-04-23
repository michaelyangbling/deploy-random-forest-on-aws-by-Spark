//author Yu Tian

import org.apache.spark
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.{SparkConf, SparkContext}

object tuning_on_samples {
  val conf = new SparkConf().setMaster("local").setAppName("Simple Application")
  val sc = new SparkContext(conf)

  def main(args: Array[String]) {
    val data = sc.textFile("input/sample_data.csv").map(_.split(","))
    // Should be some file on your system
    //val splits = data.randomSplit(Array(0.7, 0.3))
    val Data = data.map { record =>
      val features = record.slice(0, record.size - 2).map(_.toDouble)
      val label = record(record.size - 1).toInt
      //LabeledPoint(label,Vectors.dense(features))
    }
  }

  //val Dataframe = spark.createDataFrame(Data).toDF("features", "label")

  val Maxbins: List[Int] = List(16, 32)
  val numTrees: List[Int] = List(5, 10, 20)
  val maxDepth: List[Int] = List(15, 30)

  val nFolds: Int = 5
  val metric: String = "rmse"

  val rf = new RandomForestClassifier()
    .setLabelCol("label")
    .setFeaturesCol("features")
    .setNumTrees(NumTrees)
    .setMaxBins

  val pipeline = new Pipeline().setStages(Array(rf))

  val paramGrid = new ParamGridBuilder().build() // No parameter search

  val evaluator = new MulticlassClassificationEvaluator()
    .setLabelCol("label")
    .setPredictionCol("prediction")
    // "f1" (default), "weightedPrecision", "weightedRecall", "accuracy"
    .setMetricName(metric)

  val cv = new CrossValidator()
    // ml.Pipeline with ml.classification.RandomForestClassifier
    .setEstimator(pipeline)
    // ml.evaluation.MulticlassClassificationEvaluator
    .setEvaluator(evaluator)
    .setEstimatorParamMaps(paramGrid)
    .setNumFolds(nFolds)

  val model = cv.fit(Data)
    }



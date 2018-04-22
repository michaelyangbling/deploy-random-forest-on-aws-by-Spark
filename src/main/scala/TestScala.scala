//standalone test: sbt package
//data not clean in training
//data not clean in testing set
//make -f makefile.mk standalone
//head -50000 /Users/yzh/Desktop/cour/parallel/brain/L6_1_965381.csv >> sample
//bzip2 -k /Users/yzh/Desktop/cour/parallel/brain/sample2.csv
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.sql.SparkSession
import org.dmg.pmml.True

import scala.util.Try
//this method store graph and PageRanksas rdd,
//optimized for memory but use a little more time
//"/Users/yzh/Desktop/cour/parallel/brain/files/sample1.csv copy.bz2" "/Users/yzh/Desktop/cour/parallel/brain/sample5.bz2" "/Users/yzh/Desktop/cour/parallel/brain/output2"
object TestScala {

  def main(args: Array[String]):Unit= {
    //val conf = new SparkConf().setMaster("local").setAppName("My App")  //for local run&debug
    val conf = new SparkConf().setAppName("My App") //for AWS run
    val sc = new SparkContext(conf)
    val train = sc.textFile(args(0))// Should be some file on your system
    val test = sc.textFile(args(1))
    //val splits = data.randomSplit(Array(0.7, 0.3))
    //val (trainingData, testData) = (splits(0), splits(1))
//    val TrainingData=trainingData.map{record=>
//      val features=record.slice(0,record.size-2).map(_.toDouble)
//      val label=record(record.size-1).toInt
//      LabeledPoint(label,Vectors.dense(features))
//    }
    def cleanData(line:String): Array[LabeledPoint] = {
      try {
        val parts = line.split(',').map(_.toDouble)
        Array(LabeledPoint(parts.last.toInt, Vectors.dense(parts.init)))
      }
      catch{
        case _: Throwable => {
          Array()
        }
      }
    }
    def filterData(in:Array[LabeledPoint]):Boolean={
      if (in.length!=0)
        true
      else {
        false
      }
    }

    val TrainingData = train.map (cleanData).filter(filterData).map(line=>line(0))


    def cleanData2(line:String): LabeledPoint = {
      try {
        val parts = line.split(',').init.map(_.toDouble)
        LabeledPoint(1, Vectors.dense(parts))
      }
      catch{
        case _: Throwable => {
          LabeledPoint(-1,Vectors.dense(1.0)) //wrong format
        }
      }
    }
    val TestingData = test.map(cleanData2)



    //      val spark: SparkSession = SparkSession.builder.master("local").getOrCreate
    //      val trainingDF=spark.createDataFrame(TrainingData)
    //val sc = spark.sparkContext // Just used to create test RDDs
    val numClasses = 2// Empty categoricalFeaturesInfo indicates all features are continuous.
    val categoricalFeaturesInfo = Map[Int, Int]()
    val numTrees =10 // Use more in practice.
    val featureSubsetStrategy = "auto" // Let the algorithm choose.
    val impurity = "gini"
    val maxDepth = 4
    val maxBins = 32
    val model = RandomForest.trainClassifier(TrainingData, numClasses, categoricalFeaturesInfo, numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)
    println("finished training")
    def predict(point:LabeledPoint):Int={
      if (point.label==1){ //indicating right format
        val prediction = model.predict(point.features)
        prediction.toInt}//
      else
        0
    }
    val Preds = TestingData.map(predict).collect
    println("finished prediction")
    for (each<-Preds){
      println(each)
    }//write to stdout of master node...
    //in aws : should be containers -> first file
//    Preds.persist()
      //Preds.coalesce(1).saveAsTextFile(args(2))
      //saveAsTextFile and save( model) bugs..duplicate write
//    println("num of 1 predicted")
//    println(Preds.collect().sum.toDouble)
//    println("num of test records")
//    println(Preds.count.toDouble)
//    model.save(sc, args(3))
    //import org.apache.spark.mllib.tree.model.RandomForestModel
    //val sameModel = RandomForestModel.load(sc, "/Users/yzh/Desktop/cour/parallel/brain/model")
//    val testErr = labelAndPreds.filter(r => r._1 != r._2).count.toDouble / TestingData.count()
//    println("Test Error = " + testErr)
//    println("Learned classification forest model:\n" + model.toDebugString)
//
//    // Save and load model
//    model.save(sc, "target/tmp/myRandomForestClassificationModel")
//    val sameModel = RandomForestModel.load(sc, "target/tmp/myRandomForestClassificationModel")
//    sc.stop()
//    println("starting")
//    val conf = new SparkConf().setMaster("local").setAppName("My App")  //for local run&debug
//    //val conf = new SparkConf().setAppName("My App") //for AWS run
//    val sc = new SparkContext(conf)
//
//    // Load and parse the data file.
//    val data = MLUtils.loadLibSVMFile(sc, "data/mllib/sample_libsvm_data.txt")
//    // Split the data into training and test sets (30% held out for testing)
//    val splits = data.randomSplit(Array(0.7, 0.3))
//    val (trainingData, testData) = (splits(0), splits(1))
//
//    // Train a RandomForest model.
//    // Empty categoricalFeaturesInfo indicates all features are continuous.
//    val numClasses = 2
//    val categoricalFeaturesInfo = Map[Int, Int]()
//    val numTrees = 3 // Use more in practice.
//    val featureSubsetStrategy = "auto" // Let the algorithm choose.
//    val impurity = "gini"
//    val maxDepth = 4
//    val maxBins = 32
//
//    val model = RandomForest.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
//      numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)
//
//    // Evaluate model on test instances and compute test error
//    val labelAndPreds = testData.map { point =>
//      val prediction = model.predict(point.features)
//      (point.label, prediction)
//    }
//    val testErr = labelAndPreds.filter(r => r._1 != r._2).count.toDouble / testData.count()
//    println("Test Error = " + testErr)
//    println("Learned classification forest model:\n" + model.toDebugString)

    //just some things for debugging...
    //println(c)
    //cleanedData.saveAsTextFile(args(1));

    /*print(pageLinksPair.flatMap(pair=>pair._2).count)
    println("-------------")
    println("-------------")
    print(dangleRdd.count)*/
    //counts.foreach(println)
    //println(nowPages)
    //counts.saveAsTextFile("/Users/yzh/Desktop/njtest/output")
    /*val a=scala.io.StdIn.readLine()
    webParser.parse(a).foreach(println)*/
  }
}
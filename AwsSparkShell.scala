//957649 test records
//import org.apache.spark.{SparkConf, SparkContext}
//import org.apache.spark.mllib.linalg.Vectors
//import org.apache.spark.mllib.regression.LabeledPoint
//import org.apache.spark.mllib.tree.RandomForest
//
//
//val train = sc.textFile("s3://michaelyangcs/sample1.csv")// Should be some file on your system
//val test = sc.textFile("s3://michaelyangcs/sample5")
////val splits = data.randomSplit(Array(0.7, 0.3))
////val (trainingData, testData) = (splits(0), splits(1))
////    val TrainingData=trainingData.map{record=>
////      val features=record.slice(0,record.size-2).map(_.toDouble)
////      val label=record(record.size-1).toInt
////      LabeledPoint(label,Vectors.dense(features))
////    }
//def cleanData(line:String): Array[LabeledPoint] = {
//  try {
//    val parts = line.split(',').map(_.toDouble)
//    Array(LabeledPoint(parts.last.toInt, Vectors.dense(parts.init)))
//  }
//  catch{
//    case _: Throwable => {
//      Array()
//    }
//  }
//}
//def filterData(in:Array[LabeledPoint]):Boolean={
//  if (in.length!=0)
//    true
//  else {
//    false
//  }
//}
//
//val TrainingData = train.map (cleanData).filter(filterData).map(line=>line(0))
//
//
//
//val TestingData = test.map { line =>
//  val parts = line.split(',').init.map(_.toDouble)
//  LabeledPoint(1, Vectors.dense(parts))
//}
//
//
////      val spark: SparkSession = SparkSession.builder.master("local").getOrCreate
////      val trainingDF=spark.createDataFrame(TrainingData)
////val sc = spark.sparkContext // Just used to create test RDDs
//val numClasses = 2// Empty categoricalFeaturesInfo indicates all features are continuous.
//val categoricalFeaturesInfo = Map[Int, Int]()
//val numTrees =10 // Use more in practice.
//val featureSubsetStrategy = "auto" // Let the algorithm choose.
//val impurity = "gini"
//val maxDepth = 4
//val maxBins = 32
//val model = RandomForest.trainClassifier(TrainingData, numClasses, categoricalFeaturesInfo, numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)
//
//val Preds = TestingData.map { point =>
//  val prediction = model.predict(point.features)
//  prediction.toInt
//}
//Preds.coalesce(1).saveAsTextFile("s3://michaelyangcs/outSmall")


//console predict
//import org.apache.spark.mllib.linalg.Vectors
//import org.apache.spark.mllib.regression.LabeledPoint
//import org.apache.spark.mllib.tree.model.RandomForestModel
//val test = sc.textFile("s3://michaelyangcs/L6_5_new.csv")
//def cleanData2(line:String): Array[LabeledPoint] = {
//  try {
//    val parts = line.split(',').init.map(_.toDouble)
//    Array(LabeledPoint(1, Vectors.dense(parts)))
//  }
//  catch{
//    case _: Throwable => {
//      Array()
//    }
//  }
//}
//
//val TestingData = test.map(cleanData2)
//val model = RandomForestModel.load(sc, "s3://michaelyangcs/model")
//def predict(point:Array[LabeledPoint]):Int={
//  if (point.length!=0){
//    val prediction = model.predict(point(0).features)
//    prediction.toInt}
//  else
//    0
//}
//val Preds = TestingData.map(predict)
//Preds.coalesce(1).saveAsTextFile("s3://michaelyangcs/out2")
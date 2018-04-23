
//data not clean in training
//data not clean in testing set
//sampling for initial analysis:
//head -50000 /Users/yzh/Desktop/cour/parallel/brain/L6_1_965381.csv >> sample

//compress
//bzip2 -k /Users/yzh/Desktop/cour/parallel/brain/sample2.csv

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.mllib.tree.model.RandomForestModel
object TestScala {

  def main(args: Array[String]):Unit= {
    val conf = new SparkConf().setAppName("My App")
    val sc = new SparkContext(conf)
    val train = sc.textFile(args(0))// Should be some file on your system
    val test = sc.textFile(args(1))
    def cleanData(line:String): LabeledPoint = { //filter input training data
      try {
        val parts = line.split(',').map(_.toDouble)
        LabeledPoint(parts.last.toInt, Vectors.dense(parts.init))
      }
      catch{
        case _: Throwable => {
          LabeledPoint(-1, Vectors.dense(1.0))
        }
      }
    }
    def filterData(point : LabeledPoint):Boolean={ //filter input training data
      if (point.label != -1)
        true
      else {
        false
      }
    }

    val TrainingData = train.map (cleanData).filter(filterData)

    def cleanData2(line:String): LabeledPoint = { //predict wrong-format test records to 0
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
    val numClasses = 2// Empty categoricalFeaturesInfo indicates all features are continuous.
    val categoricalFeaturesInfo = Map[Int, Int]()
    val numTrees =args(2).toInt // Use more in practice.
    val featureSubsetStrategy = "auto"
    val impurity = "gini"
    val maxDepth = args(3).toInt
    val maxBins = 32
    val model = RandomForest.trainClassifier(TrainingData, numClasses, categoricalFeaturesInfo, numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)
    //println("finished training")
    val broadModel:Broadcast[RandomForestModel]=sc.broadcast(model)
    def predict(point:LabeledPoint):Int={ //predict wrong-format test records to 0
      if (point.label==1) { //indicating right format
        try {
          val prediction = broadModel.value.predict(point.features)
          prediction.toInt
        }
        catch{
          case _: Throwable => {
            0 //wrong format
          }
        }
      }//
      else
        0
    }
    val Preds = TestingData.map(predict).collect
    //println("finished prediction")
    for (each<-Preds){
      println(each)
    }//write to stdout of master node...
  }
}
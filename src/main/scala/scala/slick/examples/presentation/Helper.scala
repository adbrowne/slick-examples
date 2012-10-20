package scala.slick.examples.presentation

import scala.slick.driver.H2Driver.simple._
// Use the implicit threadLocalSession
import Database.threadLocalSession

import scala.slick.direct._

// Use the implicit threadLocalSession
import Database.threadLocalSession
import scala.slick.direct.AnnotationMapper._

object Helper {
	def createDb {

	    val TrainTrips = new Table[(String, Double, Int)]("TrainTrips") {
	    def name = column[String]("NAME")
    def price = column[Double]("PRICE")
    def countryID = column[Int]("FK_COUNTRY_ID")
    def * = name ~ price ~ countryID
  }
  
    (TrainTrips.ddl).create
    
    TrainTrips.insertAll(
      ("Melbourne-Bendigo", 13.90, 1),
      ("The Ghan", 1421, 1),
      ("Indian Pacific", 408, 1),
      ("Glacier Express", 142.8, 2),
      ("Bernina Express", 118, 2),
      ("Trans Siberain", 805, 3))
      
	}
}
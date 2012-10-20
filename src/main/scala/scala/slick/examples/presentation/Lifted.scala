package scala.slick.example.presentation

// Use H2Driver to connect to an H2 database
// Use H2Driver to connect to an H2 database
import scala.slick.driver.H2Driver.simple._

// Use the implicit threadLocalSession
import Database.threadLocalSession

import scala.slick.direct._

// Use the implicit threadLocalSession
import Database.threadLocalSession
import scala.slick.direct.AnnotationMapper._

object Lifted extends App {

  val TrainTrips = new Table[(String, Double, Int)]("TrainTrips") {
    def name = column[String]("NAME")
    def price = column[Double]("PRICE")
    def countryID = column[Int]("FK_COUNTRY_ID")
    def * = name ~ price ~ countryID
  }
  
  Database.forURL("jdbc:h2:mem:test1;TRACE_LEVEL_FILE=4", driver = "org.h2.Driver") withSession {

    (TrainTrips.ddl).create
    
    TrainTrips.insertAll(
      ("Melbourne-Bendigo", 13.90, 1),
      ("The Ghan", 1421, 1),
      ("Indian Pacific", 408, 1),
      ("Glacier Express", 142.8, 2),
      ("Bernina Express", 118, 2),
      ("Trans Siberain", 805, 3))
      
    val australianTrips = for {
    	t <- TrainTrips if t.countryID === 1 
    } yield (t.name, t.price)
  
    australianTrips.foreach { case(name, price) => println(name + ": " + price) }
  }

}
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

object Presentation extends App {

  case class TrainTrip(
      name: String,
      price: Double,
      countryID: Int
  )
  
  case class Country(
      name: String,
      countryID: Int
  )
  
  val trips = List(
		  TrainTrip("Melbourne-Bendigo", 13.90, 1),
		  TrainTrip("The Ghan", 1421, 1),
		  TrainTrip("Indian Pacific", 408, 1),
		  TrainTrip("Glacier Express", 142.8, 2),
		  TrainTrip("Bernina Express", 118, 2),
		  TrainTrip("Trans Siberain", 805, 3)
  )
  
  val countries = List(
  		  Country("Australia", 1),
  		  Country("Switzerland", 2),
  		  Country("Russia", 3)
  )
  
  val australianTrips = for {
    t <- trips if t.countryID == 1 
  } yield (t.name, t.price)
  
  australianTrips.foreach { case(name, price) => println(name + ": " + price) }
  
  val manualJoin = for {
    t <- trips
    c <- countries if c.countryID == t.countryID
  } yield (t.name, c.name)
  
  manualJoin.foreach {
    case(trip, country) => println("  " + trip + " country: " + country)
  }
}
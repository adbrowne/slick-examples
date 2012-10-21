package scala.slick.examples.rough

import scala.slick.examples.rough._

object DirectRough extends App {



  case class TrainTrip(
    name: String,
    price: Double,
    countryID: Int)

 
    val trips = Queryable[TrainTrip]

 val australianTrips = for {
      t <- trips if t.countryID == 1
    } yield (t.name, t.price)

//    val australianTrips : Queryable[TrainTrip] = trips
    
    australianTrips.toSeq.foreach {
      case trip => println(trip.name)
    }
 
}

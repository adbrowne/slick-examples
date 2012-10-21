package scala.slick.examples.rough

import scala.slick.examples.rough._
import scala.reflect.runtime.{ universe => ru }
object DirectRough extends App {

  case class TrainTrip(
    name: String,
    price: Double,
    countryID: Int)
 
    val trips = Queryable[TrainTrip]

    // val australianTrips = for {
    //  t <- trips if t.countryID == 1
    // } yield (t.name, t.price)

    // desugars to
    val australianTrips = 
      trips.filter(c => c.countryID == 1)
      .map(t => (t.name, t.price))
    
    australianTrips.toSeq.foreach {
       case trip => println(trip._1)
    }
}
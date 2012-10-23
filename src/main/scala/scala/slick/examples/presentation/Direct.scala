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

object Direct extends App {

    @table("TrainTrips") 
    case class TrainTrip(
        @column("NAME") name: String,
        @column("PRICE") price: Double,
        @column("FK_COUNTRY_ID") countryID: Int
    )

    Database.forURL("jdbc:h2:mem:test1;TRACE_LEVEL_FILE=4", driver = "org.h2.Driver") withSession {

        scala.slick.examples.presentation.Helper.createDb

        val trips = Queryable[TrainTrip]
        val backend = new SlickBackend(scala.slick.driver.H2Driver, AnnotationMapper)

        val australianTrips = for {
            t <- trips if t.countryID.toString() == "1" 
        } yield (t.name, t.price)

        backend
            .result(australianTrips, threadLocalSession)
            .foreach { 
                case(name, price) => println(name + ": " + price) 
            }

        val implicitTrips = ImplicitQueryable(Queryable[TrainTrip], backend, threadLocalSession)
        implicitTrips.toSeq.foreach(t => println(t.name))

        implicitTrips.toSeq.sortBy(t => t.price).foreach(t => println(t.name + ": " + t.price)) 

        println( implicitTrips.length)
        
        val flatMapResult = implicitTrips.flatMap(e1 => implicitTrips.map(e2=>e1))
        println("Flat Map Results")
        flatMapResult.toSeq.foreach(t => println(t.name))
        
        val flatMapped = for {
          a <- implicitTrips
          b <- implicitTrips
          if a.countryID == 1
        } yield (a.name, b.name)
        
        flatMapped.toSeq.foreach {case (a_name, b_name) => println(a_name + " " + b_name)}
    }
}

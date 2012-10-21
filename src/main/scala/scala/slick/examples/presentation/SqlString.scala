package scala.slick.example.presentation

import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

object SqlString extends App {

    case class TrainTrip(
        name: String,
        price: Double,
        countryID: Int
    )


    Database.forURL("jdbc:h2:mem:test1;TRACE_LEVEL_FILE=4", driver = "org.h2.Driver") withSession {

        implicit val getTrainTripResult = GetResult(r => TrainTrip(r.<<, r.<<, r.<<))

        Q.updateNA("create table TrainTrips("+
            "name varchar not null, "+
            "price double not null, "+
            "FK_COUNTRY_ID int not null)").execute
      
        val q = Q.queryNA("select * from TrainTrips where FK_COUNTRY_ID = 1")
        
        q.foreach(println)
    }
}

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

object Joins extends App {

	val Countries = new Table[(Int, String)]("Countries") {
		def id = column[Int]("COUNTRY_ID", O.PrimaryKey) // This is the primary key column
		def name = column[String]("NAME")
		def * = id ~ name
    }
    
	def halfQuery() = {
        for {
            t <- TrainTrips if t.countryID === 1 
        } yield (t.name, t.price)
    }
    
    val TrainTrips = new Table[(String, Double, Int)]("TrainTrips") {
        def name = column[String]("NAME")
        def price = column[Double]("PRICE")
        def countryID = column[Int]("FK_COUNTRY_ID")
        def * = name ~ price ~ countryID
        def country = foreignKey("COUNTRY_ID", countryID, Countries)(_.id)
    }
    
    Database.forURL("jdbc:h2:mem:test1;TRACE_LEVEL_FILE=4", driver = "org.h2.Driver") withSession {

        (TrainTrips.ddl ++ Countries.ddl).create

        Countries.insertAll(
                (1, "Australia"),
                (2, "Switzerland"),
                (3, "Russia"))
                
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

        println("Filtered Trips")
        australianTrips.foreach { case(name, price) => println(name + ": " + price) }
        
        println("Manual Join")
        val manualJoin = for {
	      t <- TrainTrips
	      c <- Countries if c.id === t.countryID
	    } yield (t.name, c.name)
	    for(t <- manualJoin) println("  " + t._1 + " country: " + t._2)
	    
	    println("Join by foreign key:")
	    val foreignKeyJoin = for {
	      t <- TrainTrips
	      c <- t.country
	    } yield (t.name, c.name)
	    for(t <- foreignKeyJoin) println("  " + t._1 + " country: " + t._2)
	    
	    println(foreignKeyJoin.selectStatement)
	    
	    println("running exists")
	    val qCustom = for { 
	    	(c, index) <- TrainTrips.zipWithIndex
	    } yield (index, c.name)
      
	    qCustom.foreach {
	    	case (index,name) => println(index + " " + name)
	    }
	  
	    val composedQuery = for {
	      t <- halfQuery() if t._2 > 100.00
	    }
	    
	    // Compute the number of coffees by each supplier
    println("Trips per country:")
    val tripsPerCountry = (for {
      t <- TrainTrips
      c <- t.country
    } yield (c, c)).groupBy(_._2.id).map {
      case (_, q) => (q.map(_._2.name).min.get, q.length)
    }
    // .get is needed because SLICK cannot enforce statically that
    // the supplier is always available (being a non-nullable foreign key),
    // thus wrapping it in an Option
    tripsPerCountry foreach { case (name, count) =>
      println("  " + name + ": " + count)
    }
	}
}

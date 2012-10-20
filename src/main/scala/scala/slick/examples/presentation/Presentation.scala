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
  
  val trips = List(
		  TrainTrip("Melbourne-Bendigo", 13.90, 1),
		  TrainTrip("The Ghan", 1421, 1),
		  TrainTrip("Indian Pacific", 408, 1),
		  TrainTrip("Glacier Express", 142.8, 2),
		  TrainTrip("Bernina Express", 118, 2),
		  TrainTrip("Trans Siberain", 805, 3)
  )
  
  val australianTrips = for {
    t <- trips if t.countryID == 1 
  } yield (t.name, t.price)
  
  australianTrips.foreach { case(name, price) => println(name + ": " + price) }
  
  // Definition of the SUPPLIERS table
  object Suppliers extends Table[(Int, String, String, String, String, String)]("SUPPLIERS") {
    def id = column[Int]("SUP_ID", O.PrimaryKey) // This is the primary key column
    def name = column[String]("SUP_NAME")
    def street = column[String]("STREET")
    def city = column[String]("CITY")
    def state = column[String]("STATE")
    def zip = column[String]("ZIP")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = id ~ name ~ street ~ city ~ state ~ zip
  }

  // Definition of the COFFEES table
  object Coffees extends Table[(String, Int, Double, Int, Int)]("COFFEES") {
    def name = column[String]("COF_NAME", O.PrimaryKey)
    def supID = column[Int]("SUP_ID")
    def price = column[Double]("PRICE")
    def sales = column[Int]("SALES")
    def total = column[Int]("TOTAL")
    def * = name ~ supID ~ price ~ sales ~ total
    // A reified foreign key relation that can be navigated to create a join
    def supplier = foreignKey("SUP_FK", supID, Suppliers)(_.id)
  }

  @table("COFFEES") case class Coffee(
    @column("COF_NAME") name: String,
    @column("SUP_ID") supID: Int,
    @column("PRICE") price: Double)

  // Connect to the database and execute the following block within a session
  Database.forURL("jdbc:h2:mem:test1;TRACE_LEVEL_FILE=4", driver = "org.h2.Driver") withSession {

    (Suppliers.ddl ++ Coffees.ddl).create

    // Insert some suppliers
    Suppliers.insert(101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199")
    Suppliers.insert(49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460")
    Suppliers.insert(150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966")

    // Insert some coffees (using JDBC's batch insert feature, if supported by the DB)
    Coffees.insertAll(
      ("Colombian", 101, 7.99, 0, 0),
      ("French_Roast", 49, 8.99, 0, 0),
      ("Espresso", 150, 9.99, 0, 0),
      ("Colombian_Decaf", 101, 8.99, 0, 0),
      ("French_Roast_Decaf", 49, 9.99, 0, 0))

    // Iterate through all coffees and output them
    //    println("Coffees:")
    //    Query(Coffees) foreach { case (name, supID, price, sales, total) =>
    //      println("  " + name + "\t" + supID + "\t" + price + "\t" + sales + "\t" + total)
    //    }

    val coffees = Queryable[Coffee]

    val l2 = coffees.withFilter(c => c.supID == 101).map(c => c.name)
    
    val l3 = l2.filter(c => c == "Colombian") //{case ("Colombian", _, _ ) => true } 
    
    val l4 = l2.filter { case "Colombian" => true }
    
    val l5 = l2.length
    
    val l = for {
      c <- coffees if c.supID == 101
      //                       ^ comparing Int to Int!
    } yield (c.name, c.price, c)

    println("Output Query results")
    val q2 = coffees.map(c => c.name)
    // execute query using a chosen db backend
    val backend = new SlickBackend(scala.slick.driver.H2Driver, AnnotationMapper)
    //backend.result(l2, threadLocalSession)
    //  .foreach { case (name, price, c) => println(name + ": " + price * 2 + " " + c.supID) }

    // backend.result(q2, threadLocalSession).foreach { case name => println(name) }
    println(backend.result(l5, threadLocalSession))
  }

  /*
// Definition of the SUPPLIERS table
  object Suppliers extends Table[(Int, String, String, String, String, String)]("SUPPLIERS") {
    def id = column[Int]("SUP_ID", O.PrimaryKey) // This is the primary key column
    def name = column[String]("SUP_NAME")
    def street = column[String]("STREET")
    def city = column[String]("CITY")
    def state = column[String]("STATE")
    def zip = column[String]("ZIP")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = id ~ name ~ street ~ city ~ state ~ zip
  }

  // Definition of the COFFEES table
  object Coffees extends Table[(String, Int, Double, Int, Int)]("COFFEES") {
    def name = column[String]("COF_NAME", O.PrimaryKey)
    def supID = column[Int]("SUP_ID")
    def price = column[Double]("PRICE")
    def sales = column[Int]("SALES")
    def total = column[Int]("TOTAL")
    def * = name ~ supID ~ price ~ sales ~ total
    // A reified foreign key relation that can be navigated to create a join
    def supplier = foreignKey("SUP_FK", supID, Suppliers)(_.id)
  }

  // Connect to the database and execute the following block within a session
  Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver") withSession {
    // The session is never named explicitly. It is bound to the current
    // thread as the threadLocalSession that we imported

    // Create the tables, including primary and foreign keys
    (Suppliers.ddl ++ Coffees.ddl).create

    // Insert some suppliers
    Suppliers.insert(101, "Acme, Inc.",      "99 Market Street", "Groundsville", "CA", "95199")
    Suppliers.insert( 49, "Superior Coffee", "1 Party Place",    "Mendocino",    "CA", "95460")
    Suppliers.insert(150, "The High Ground", "100 Coffee Lane",  "Meadows",      "CA", "93966")

    // Insert some coffees (using JDBC's batch insert feature, if supported by the DB)
    Coffees.insertAll(
      ("Colombian",         101, 7.99, 0, 0),
      ("French_Roast",       49, 8.99, 0, 0),
      ("Espresso",          150, 9.99, 0, 0),
      ("Colombian_Decaf",   101, 8.99, 0, 0),
      ("French_Roast_Decaf", 49, 9.99, 0, 0)
    )

    // Iterate through all coffees and output them
    println("Coffees:")
    Query(Coffees) foreach { case (name, supID, price, sales, total) =>
      println("  " + name + "\t" + supID + "\t" + price + "\t" + sales + "\t" + total)
    }

    // Why not let the database do the string conversion and concatenation?
    println("Coffees (concatenated by DB):")
    val q1 = for(c <- Coffees) // Coffees lifted automatically to a Query
      yield ConstColumn("  ") ++ c.name ++ "\t" ++ c.supID.asColumnOf[String] ++
        "\t" ++ c.price.asColumnOf[String] ++ "\t" ++ c.sales.asColumnOf[String] ++
        "\t" ++ c.total.asColumnOf[String]
    // The first string constant needs to be lifted manually to a ConstColumn
    // so that the proper ++ operator is found
    q1 foreach println

    // Perform a join to retrieve coffee names and supplier names for
    // all coffees costing less than $9.00
    println("Manual join:")
    val q2 = for {
      c <- Coffees if c.price < 9.0
      s <- Suppliers if s.id === c.supID
    } yield (c.name, s.name)
    for(t <- q2) println("  " + t._1 + " supplied by " + t._2)

    // Do the same thing using the navigable foreign key
    println("Join by foreign key:")
    val q3 = for {
      c <- Coffees if c.price < 9.0
      s <- c.supplier
    } yield (c.name, s.name)
    // This time we read the result set into a List
    val l3: List[(String, String)] = q3.list
    for((s1, s2) <- l3) println("  " + s1 + " supplied by " + s2)

    // Check the SELECT statement for that query
    println(q3.selectStatement)

    // Compute the number of coffees by each supplier
    println("Coffees per supplier:")
    val q4 = (for {
      c <- Coffees
      s <- c.supplier
    } yield (c, s)).groupBy(_._2.id).map {
      case (_, q) => (q.map(_._2.name).min.get, q.length)
    }
    // .get is needed because SLICK cannot enforce statically that
    // the supplier is always available (being a non-nullable foreign key),
    // thus wrapping it in an Option
    q4 foreach { case (name, count) =>
      println("  " + name + ": " + count)
    }
    */
}
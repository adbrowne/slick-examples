package scala.slick.example.presentation
import scala.language.experimental.macros // turns on the macros
import scala.reflect.runtime.{ universe => ru }
import scala.reflect.ClassTag
import scala.reflect.macros.Context

object DirectRough extends App {

  class Session
  class Database {
    def withSession[T](f: Session => T): T = {
      val s = new Session
      f(s)
    }
  }

  object Database {
    val session = new Session

    implicit def threadLocalSession: Session = {
      session
    }
    def forURL(connectionString: String, driver: String): Database = {
      new Database
    }
  }
  object QueryableMacros {
    def filter[T: c.AbsTypeTag]
      (c: scala.reflect.macros.Context)
      (projection: c.Expr[T => Boolean]): c.Expr[Queryable[T]] = {
      import c.universe._
      c.universe.reify(new Queryable[T])
    }
    
    def map[T:c.AbsTypeTag, S:c.AbsTypeTag]
      (c: scala.reflect.macros.Context)
      (projection: c.Expr[T => S]): c.Expr[Queryable[S]] = {
      c.universe.reify(new Queryable[S])
    }
  
  }

  class Queryable[T] {
    def filter(projection: T => Boolean): Queryable[T] = macro QueryableMacros.filter[T]
    def withFilter(projection: T => Boolean): Queryable[T] = macro QueryableMacros.filter[T]
    def map[S]( projection: T => S ) : Queryable[S] = macro QueryableMacros.map[T,S]
  }

  class SlickBackend

  object Queryable {
    def apply[T: ru.TypeTag: ClassTag]() = new Queryable[T]()
  }

  class ImplicitQueryable[T](q: Queryable[T], backend: SlickBackend, session: Session) extends Queryable {

  }

  object ImplicitQueryable {
    def apply[T](q: Queryable[T], backend: SlickBackend)(implicit session: Session) = new ImplicitQueryable[T](q, backend, session)

  }

  case class TrainTrip(
    name: String,
    price: Double,
    countryID: Int)

  Database.forURL("jdbc:h2:mem:test1;TRACE_LEVEL_FILE=4", driver = "org.h2.Driver") withSession {
    import Database.threadLocalSession

    scala.slick.examples.presentation.Helper.createDb

    val backend = new SlickBackend()

    val trips = ImplicitQueryable(Queryable[TrainTrip], backend)

    val australianTrips = for {
      t <- trips // if t.countryID == 1
    } yield (t.name, t.price)

    australianTrips.foreach {
      case (name, price) => println(name + ": " + price)
    }
  }
}

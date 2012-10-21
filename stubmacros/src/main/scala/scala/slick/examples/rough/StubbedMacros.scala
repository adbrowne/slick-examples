package scala.slick.examples.rough

import scala.language.experimental.macros // turns on the macros
import scala.reflect.runtime.{ universe => ru }
import scala.reflect.ClassTag
import scala.reflect.macros.Context

  object QueryableMacros {
    def filter[T: c.AbsTypeTag]
      (c: scala.reflect.macros.Context)
      (projection: c.Expr[T => Boolean]): c.Expr[Queryable[T]] = {
      import c.universe._
      c.universe.reify({
        println("Running filter")
       new Queryable[T] 
      })
    }
    
    def map[T:c.AbsTypeTag, S:c.AbsTypeTag]
      (c: scala.reflect.macros.Context)
      (projection: c.Expr[T => S]): c.Expr[Queryable[S]] = {
      c.universe.reify({
       println("Running map")
       new Queryable[S]
      })
    }
  
  }

class Queryable[T] {
	def toSeq : Seq[T] = List()
    def filter(projection: T => Boolean): Queryable[T] = macro QueryableMacros.filter[T]
    def withFilter(projection: T => Boolean): Queryable[T] = macro QueryableMacros.filter[T]
    def map[S]( projection: T => S ) : Queryable[S] = macro QueryableMacros.map[T,S]
  }

  object Queryable {
    def apply[T: ru.TypeTag: ClassTag]() = new Queryable[T]()
    //def apply[T]() = new Queryable[T]()
  }
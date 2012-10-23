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
      val tree = projection.tree // take compile time tree
      
      // create tree that will be available at runtime
      val reifiedTree = c.reifyTree(c.runtimeUniverse, c.universe.EmptyTree, c.typeCheck(tree)).asInstanceOf[Tree]
      val treeExpresssion = c.Expr[ru.Expr[T]](reifiedTree)
      
      c.universe.reify({
        // splice is how we bring variables in
    	   val splicedExpression = treeExpresssion.splice
        
       println("Running filter")
       println(splicedExpression)
        new Queryable[T] 
      })
    }
    
    def map[T:c.AbsTypeTag, S:c.AbsTypeTag]
      (c: scala.reflect.macros.Context)
      (projection: c.Expr[T => S]): c.Expr[Queryable[S]] = {
      import c.universe._
      val tree = projection.tree // take compile time tree
      
      // create tree that will be available at runtime
      val reifiedTree = c.reifyTree(c.runtimeUniverse, c.universe.EmptyTree, c.typeCheck(tree))
      val treeExpresssion = c.Expr[ru.Expr[T]](reifiedTree)
      
      c.universe.reify({
        // splice is how we bring variables in
    	   val splicedExpression = treeExpresssion.splice
        
       println("Running map")
       println(splicedExpression)
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
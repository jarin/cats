package cats
package tests

import algebra.laws.{GroupLaws, OrderLaws}

import cats.data.{Const, NonEmptyList}
import cats.functor.Contravariant
import cats.laws.discipline._
import cats.laws.discipline.arbitrary.{constArbitrary, oneAndArbitrary}

class ConstTests extends CatsSuite {
  checkAll("Const[String, Int]", ApplicativeTests[Const[String, ?]].applicative[Int, Int, Int])
  checkAll("Applicative[Const[String, ?]]", SerializableTests.serializable(Applicative[Const[String, ?]]))

  checkAll("Const[String, Int] with Option", TraverseTests[Const[String, ?]].traverse[Int, Int, Int, Int, Option, Option])
  checkAll("Traverse[Const[String, ?]]", SerializableTests.serializable(Traverse[Const[String, ?]]))

  // Get Apply[Const[C : Semigroup, ?]], not Applicative[Const[C : Monoid, ?]]
  {
    implicit def nonEmptyListSemigroup[A]: Semigroup[NonEmptyList[A]] = SemigroupK[NonEmptyList].algebra
    checkAll("Apply[Const[NonEmptyList[String], Int]]", ApplyTests[Const[NonEmptyList[String], ?]].apply[Int, Int, Int])
    checkAll("Apply[Const[NonEmptyList[String], ?]]", SerializableTests.serializable(Apply[Const[NonEmptyList[String], ?]]))
  }

  // Algebra checks for Serializability of instances as part of the laws
  checkAll("Monoid[Const[Int, String]]", GroupLaws[Const[Int, String]].monoid)

  checkAll("Const[NonEmptyList[Int], String]", GroupLaws[Const[NonEmptyList[Int], String]].semigroup)

  // Note while Eq is a superclass of PartialOrder and PartialOrder a superclass
  // of Order, you can get different instances with different (more general) constraints.
  // For instance, you can get an Order for Const if the first type parameter has an Order,
  // but you can also get just an Eq for Const if the first type parameter has just an Eq
  checkAll("Const[Map[Int, Int], String]", OrderLaws[Const[Map[Int, Int], String]].eqv)
  checkAll("PartialOrder[Const[Set[Int], String]]", OrderLaws[Const[Set[Int], String]].partialOrder)
  checkAll("Order[Const[Int, String]]", OrderLaws[Const[Int, String]].order)

  checkAll("Const[String, Int]", ContravariantTests[Const[String, ?]].contravariant[Int, Int, Int])
  checkAll("Contravariant[Const[String, ?]]", SerializableTests.serializable(Contravariant[Const[String, ?]]))

  test("show") {

    Const(1).show should === ("Const(1)")

    forAll { const: Const[Int, String] =>
      const.show.startsWith("Const(") should === (true)
      const.show.contains(const.getConst.show)
      const.show should === (implicitly[Show[Const[Int, String]]].show(const))
      const.show should === (const.retag[Boolean].show)
    }
  }



}

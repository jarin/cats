---
layout: default
title:  "Apply"
section: "typeclasses"
source: "https://github.com/non/cats/blob/master/core/src/main/scala/cats/Apply.scala"
scaladoc: "#cats.Apply"
---
# Apply

`Apply` extends the [`Functor`](functor.html) type class (which features the familiar `map`
function) with a new function `ap`. The `ap` function is similar to `map`
in that we are transforming a value in a context (a context being the `F` in `F[A]`;
a context can be `Option`, `List` or `Future` for example).
However, the difference between `ap` and `map` is that for `ap` the function that 
takes care of the transformation is of type `F[A => B]`, whereas for `map` it is `A => B`:

```tut:silent
import cats._

val intToString: Int => String = _.toString
val double: Int => Int = _ * 2
val addTwo: Int => Int = _ + 2

implicit val optionApply: Apply[Option] = new Apply[Option] {
  def ap[A, B](fa: Option[A])(f: Option[A => B]): Option[B] =
    fa.flatMap (a => f.map (ff => ff(a)))

  def map[A,B](fa: Option[A])(f: A => B): Option[B] = fa map f
}

implicit val listApply: Apply[List] = new Apply[List] {
  def ap[A, B](fa: List[A])(f: List[A => B]): List[B] =
    fa.flatMap (a => f.map (ff => ff(a)))

  def map[A,B](fa: List[A])(f: A => B): List[B] = fa map f
}
```

### map

Since `Apply` extends `Functor`, we can use the `map` method from `Functor`:

```tut
Apply[Option].map(Some(1))(intToString)
Apply[Option].map(Some(1))(double)
Apply[Option].map(None)(double)
```

### compose

And like functors, `Apply` instances also compose:

```tut
val listOpt = Apply[List] compose Apply[Option]
val plusOne = (x:Int) => x + 1
listOpt.ap(List(Some(1), None, Some(3)))(List(Some(plusOne)))
```

### ap
The `ap` method is a method that `Functor` does not have:

```tut
Apply[Option].ap(Some(1))(Some(intToString))
Apply[Option].ap(Some(1))(Some(double))
Apply[Option].ap(None)(Some(double))
Apply[Option].ap(Some(1))(None)
Apply[Option].ap(None)(None)
```

### ap2, ap3, etc

`Apply` also offers variants of `ap`. The functions `apN` (for `N` between `2` and `22`) 
accept `N` arguments where `ap` accepts `1`:

For example:

```tut
val addArity2 = (a: Int, b: Int) => a + b
Apply[Option].ap2(Some(1), Some(2))(Some(addArity2))

val addArity3 = (a: Int, b: Int, c: Int) => a + b + c
Apply[Option].ap3(Some(1), Some(2), Some(3))(Some(addArity3))
```

Note that if any of the arguments of this example is `None`, the
final result is `None` as well.  The effects of the context we are operating on
are carried through the entire computation:

```tut
Apply[Option].ap2(Some(1), None)(Some(addArity2))
Apply[Option].ap4(Some(1), Some(2), Some(3), Some(4))(None)
```

### map2, map3, etc

Similarly, `mapN` functions are available:

```tut
Apply[Option].map2(Some(1), Some(2))(addArity2)

Apply[Option].map3(Some(1), Some(2), Some(3))(addArity3)
```

### tuple2, tuple3, etc

And `tupleN`:

```tut
Apply[Option].tuple2(Some(1), Some(2))

Apply[Option].tuple3(Some(1), Some(2), Some(3))
```

## apply builder syntax

The `|@|` operator offers an alternative syntax for the higher-arity `Apply`
functions (`apN`, `mapN` and `tupleN`).
In order to use it, first import `cats.syntax.all._` or `cats.syntax.apply._`.
Here we see that the following two functions, `f1` and `f2`, are equivalent:

```tut
import cats.syntax.apply._

def f1(a: Option[Int], b: Option[Int], c: Option[Int]) =
  (a |@| b |@| c) map { _ * _ * _ }
def f2(a: Option[Int], b: Option[Int], c: Option[Int]) =
  Apply[Option].map3(a, b, c)(_ * _ * _)

f1(Some(1), Some(2), Some(3))

f2(Some(1), Some(2), Some(3))
```

All instances created by `|@|` have `map`, `ap`, and `tupled` methods of the appropriate arity:

```tut
val option2 = Option(1) |@| Option(2)
val option3 = option2 |@| Option.empty[Int]

option2 map addArity2
option3 map addArity3

option2 ap Some(addArity2)
option3 ap Some(addArity3)

option2.tupled
option3.tupled
```

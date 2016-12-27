/*
 * Copyright (C) 2016  Well-Factored Software Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.wellfactored.playbindings

import play.api.mvc.{PathBindable, QueryStringBindable}
import shapeless.Generic

trait ValueClassPathBindable extends GenericValueWrapper {
  implicit def valueClassPathBindable[W, V](implicit gen: Generic.Aux[W, V],
                                            binder: PathBindable[V]): PathBindable[W] =
    new PathBindable[W] {
      override def unbind(key: String, wrapper: W): String =
        binder.unbind(key, gen.to(wrapper))

      override def bind(key: String, value: String): Either[String, W] =
        binder.bind(key, value).right.map(gen.from)
    }
}

trait ValueClassQueryStringBindable extends GenericValueWrapper {
  implicit def valueClassQueryStringBindable[W, V](implicit gen: Generic.Aux[W, V],
                                                   binder: QueryStringBindable[V]): QueryStringBindable[W] =
    new QueryStringBindable[W] {
      override def unbind(key: String, wrapper: W): String =
        binder.unbind(key, gen.to(wrapper))

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, W]] = {
        binder.bind(key, params).map(_.right.map(gen.from))
      }
    }
}


trait ValueClassUrlBinders extends ValueClassPathBindable with ValueClassQueryStringBindable

/**
  * `import ValueClassUrlBinders._` to get both the `ValueClassPathBindable` and `ValueClassQueryStringBindable`
  * generators in implicit scope. In particular, add this to your `build.sbt` to import them into
  * the routes file:
  *
  * `routesImport += "com.wellfactored.playbindings.ValueClassUrlBinders._"`
  */
object ValueClassUrlBinders extends ValueClassUrlBinders
/*
 * Copyright 2018 Greg Methvin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.methvin.orphanfinder

import scala.collection.mutable
import scala.tools.nsc.Global
import scala.tools.nsc.Phase
import scala.tools.nsc.plugins.Plugin
import scala.tools.nsc.plugins.PluginComponent

class OrphanFinderPlugin(val global: Global) extends Plugin {

  val name: String = "orphan-finder"
  val description: String = "Checks for orphan expressions"

  val components: List[PluginComponent] = List(OrphanFinderComponent)

  private final val ClassOptionPrefix = "class:"
  private val classNames: mutable.ArrayBuffer[String] = mutable.ArrayBuffer()

  override def init(options: List[String], error: String => Unit): Boolean = {
    for (option <- options) {
      if (option startsWith ClassOptionPrefix) {
        classNames += option stripPrefix ClassOptionPrefix
      } else {
        error(s"Option not understood: $option")
      }
    }
    true
  }

  override val optionsHelp: Option[String] = Some(
    s"  -P:$name:class:scala.concurrent.Future  look for orphan expressions of scala.concurrent.Future"
  )

  private object OrphanFinderComponent extends PluginComponent {
    val global = OrphanFinderPlugin.this.global
    import global._

    override val runsAfter = List("typer")
    override val runsBefore = List("patmat")

    val phaseName = "orphan-finder"

    override def newPhase(prev: Phase): StdPhase = new StdPhase(prev) {
      override def apply(unit: CompilationUnit): Unit = {
        new OrphanFinderTraverser(unit) traverse unit.body
      }
    }

    class OrphanFinderTraverser(unit: CompilationUnit) extends Traverser {
      override def traverse(tree: Tree): Unit = {
        tree match {
          case Block(statements, _) =>
            for {
              stat <- statements
              stpe <- Option(stat.tpe)
              tpe <- stpe.baseTypeSeq.toList
              fullName = tpe.typeSymbol.fullName if classNames contains fullName
            } {
              reporter.warning(stat.pos, s"Orphan $fullName found!")
            }
          case _ =>
        }
        super.traverse(tree)
      }
    }
  }
}

package tftcomps

import tftcomps.domain._
import tftcomps.domain.data
import tftcomps.domain.data.roles.{all => _, _}
import tftcomps.domain.data.champions.{all => _, _}

import scala.util.Random

//object Main extends App {
object OldMain {
  val MaxTeamSize = 7
  val MaxCost = 5

  val championCompositions = (0 until 1)
    .flatMap { _ =>
      search(Random.shuffle(data.champions.all.toSeq).filter(_.cost <= MaxCost), MaxTeamSize).headOption
    }
    .distinct
    .sortBy(-_.score)

  def compositionDescription(composition: Composition): String = {
    val rolesString =
      composition.roles.toSeq
        .sortBy { case (role, count) => (-count, role.name) }
        .map { case (role, count) => s"$count ${role.name}" }
        .mkString(", ")
    val champsString = composition.champions.map(_.name).toSeq.sorted.mkString(", ")
    s"[${composition.score}] (${composition.roles.size}) $rolesString ($champsString)"
  }

  val roleCompositions = championCompositions
  //.groupBy(_.roles.toSeq.sortBy(-_._2).map(_._1).take(3))
    .groupBy(_.synergies.toSeq.sortBy(-_._2))
    .toSeq
    .sortBy(-_._2.size)
  //.sortBy(-_._2.head.score)

  println(
    s"Found ${roleCompositions.size} role compositions scored between ${championCompositions.map(_.score).max} and ${championCompositions.map(_.score).min} points.")
  println()

  println("Highlight:")
  roleCompositions
    .filter(_._2.size >= 3)
    .sortBy(-_._2.head.score)
    .zipWithIndex
    .map {
      case ((mainRoles, comps), index) =>
        //val rolesString = mainRoles.map { case (role, count) => f"$count ${role.name}" }.mkString(", ")
        val rolesString = mainRoles
        //.map(_.name)
//          .map { case (role, count) => s"${comps.head.champions.count(_.roles.contains(role))} ${role.name}" }
          .map { case (role, count) => s"$count ${role.name}" }
          .mkString(", ")
        f"$index%3d [${comps.head.score}%3d] $rolesString (${comps.size}x)"
    }
    .foreach(println)
  println()

  roleCompositions.zipWithIndex
    .map {
      case ((mainRoles, comps), index) =>
        //val rolesString = mainRoles.map { case (role, count) => f"$count ${role.name}" }.mkString(", ")
        val rolesString = mainRoles
        //.map(_.name)
          .map { case (role, count) => s"$count ${role.name}" }
          .mkString(", ")
        f"$index%3d [${comps.head.score}%3d] $rolesString (${comps.size}x)"
    }
    .foreach(println)

  println()

  roleCompositions
    .map {
      case (mainRoles, comps) =>
        //val title = mainRoles.map { case (role, count) => s"$count $role" }.mkString(", ")
        val rolesString = mainRoles
        //.map(_.name)
          .map { case (role, count) => s"$count ${role.name}" }
          .mkString(", ")
        val content = comps.map("  " + compositionDescription(_)).mkString("\n")
        s"$rolesString\n$content"
    }
    .foreach(println)

  println()

  championCompositions.zipWithIndex
    .map { case (composition, index) => f"$index%3d ${compositionDescription(composition)}" }
    .foreach(println)

}

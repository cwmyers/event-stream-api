package app

import shapeless.tag
import shapeless.tag.@@

package object model {
  type SystemName = String @@ SystemNameT
  def SystemName(name: String): String @@ SystemNameT = tag[SystemNameT](name)

  type EventId = String @@ EventIdT
  def EventId(id: String): String @@ EventIdT = tag[EventIdT](id)

  type EntityId = String @@ EntityIdT
  def EntityId(id: String): String @@ EntityIdT = tag[EntityIdT](id)

  type SnapshotId = String @@ SnapshotIdT
  def SnapshotId(id:String): String @@ SnapshotIdT = tag[SnapshotIdT](id)

  type URI = String @@ URIT
  def URI(uri: String): String @@ URIT = tag[URIT](uri)



}

package org.worldofscala.earth

final case class NewMeshEntity(id: Option[Mesh.Id], label: String, blob: Array[Byte])
final case class MeshEntity(id: Mesh.Id, label: String, blob: Array[Byte])

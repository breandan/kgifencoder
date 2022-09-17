package ai.hypergraph.kaliningraph.image.gif

class OutputStream {
  var bytes = mutableListOf<Byte>()
  fun write(i: Int) { bytes.add(i.toByte()) }
  fun write(lzwData: List<Byte>) {
    bytes.addAll(lzwData.toList())
  }
}
package io.jenkins.plugins.imagetagparam.model

data class ImageTag(val image: String, val tag: String) {
  /**
   * Returns a string representation of the object.
   */
  override fun toString(): String {
    return "${image}:${tag}"
  }
}

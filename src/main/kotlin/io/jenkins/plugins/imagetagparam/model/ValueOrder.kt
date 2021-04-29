package io.jenkins.plugins.imagetagparam.model

enum class ValueOrder {
  NONE,
  ASC,
  DSC;

  override fun toString(): String {
    return when (this) {
      NONE -> "None"
      ASC -> "Ascending"
      DSC ->"Descending"
    }
  }
}

package io.jenkins.plugins.imagetagparam.model

enum class AuthType {
  BASIC,
  BEARER;

  override fun toString(): String {
    return when (this) {
      BASIC -> "Basic"
      BEARER -> "Bearer"
    }
  }
}

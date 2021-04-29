package io.jenkins.plugins.imagetagparam.model

data class AuthService(val authType: AuthType, val authRealm: String?, val authService: String?) {
  constructor(authType: AuthType) : this(authType, null, null)

  override fun toString(): String {
    return "AuthService: type=${authType}, realm=${authRealm}, service=${authService}"
  }
}

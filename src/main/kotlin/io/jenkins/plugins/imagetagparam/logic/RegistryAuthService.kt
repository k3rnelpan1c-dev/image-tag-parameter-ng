package io.jenkins.plugins.imagetagparam.logic

import com.cloudbees.plugins.credentials.common.StandardCredentials
import io.jenkins.plugins.imagetagparam.model.AuthService
import io.jenkins.plugins.imagetagparam.model.AuthType
import io.jenkins.plugins.imagetagparam.model.errors.RegistryUnavailableError
import io.jenkins.plugins.imagetagparam.model.errors.UnsupportedAuthTypeError
import io.jenkins.plugins.imagetagparam.util.OkHttpUtils
import okhttp3.Request
import java.util.logging.Logger

object RegistryAuthService {
  private const val AUTH_TYPE_PATTERN: String = "^(\\S+)"
  private const val REALM_SERVICE_PATTERN: String = "Bearer realm=\"(\\S+)\",service=\"([\\S ]+)\""

  private val logger: Logger = Logger.getLogger(RegistryAuthService::class.java.name)

  internal fun getAuthService(registryEndpoint: String) : Result<AuthService> = kotlin.runCatching {
    val client = OkHttpUtils.getClientWithProxyAndCache("${registryEndpoint}/v2/")
    val builder = Request.Builder().cacheControl(OkHttpUtils.getCacheControl(0)).url("${registryEndpoint}/v2/")

    client.newCall(builder.build()).execute().use { response ->
      val statusCode = response.code()

      if (statusCode < 500) {
        val headerValue = response.header("Www-Authenticate").orEmpty()
        handleAuthServiceHeader(headerValue).getOrThrow()
      } else {
        throw RegistryUnavailableError(registryEndpoint)
      }
    }
  }

  private fun handleAuthServiceHeader(headerValue: String) : Result<AuthService> = kotlin.runCatching {
    val authType = Regex(AUTH_TYPE_PATTERN).find(headerValue)

    when (authType?.value.orEmpty()) {
      AuthType.BASIC.toString() -> {
        logger.fine("AuthService: type=Basic")
        AuthService(AuthType.BASIC)
      }
      AuthType.BEARER.toString() -> {
        val realmService = Regex(REALM_SERVICE_PATTERN).find(headerValue)
        val (realm, service) = realmService!!.destructured
        logger.fine("AuthService: type=Bearer, realm=${realm}, service=${service}")
        AuthService(AuthType.BEARER, realm, service)
      }
      else -> {
        throw UnsupportedAuthTypeError(authType?.value.orEmpty())
      }
    }
  }

  internal fun getAuthToken(
    authService: AuthService,
    imageName: String,
    credentials: StandardCredentials
  ) : Result<String> = kotlin.runCatching {
    when (authService.authType) {
      AuthType.BASIC -> {}
      AuthType.BEARER -> {}
    }
    throw NotImplementedError()
  }

  private fun getBearerAuthToken(
    authService: AuthService,
    imageName: String,
    credentials: StandardCredentials
  ) : Result<String> = kotlin.runCatching {
    throw NotImplementedError()
  }
}

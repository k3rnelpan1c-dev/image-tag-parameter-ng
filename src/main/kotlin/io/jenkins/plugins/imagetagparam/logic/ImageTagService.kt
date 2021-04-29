package io.jenkins.plugins.imagetagparam.logic


import com.cloudbees.plugins.credentials.common.StandardCredentials
import io.jenkins.plugins.imagetagparam.logic.RegistryAuthService.getAuthService
import io.jenkins.plugins.imagetagparam.logic.RegistryAuthService.getAuthToken
import io.jenkins.plugins.imagetagparam.model.AuthService
import io.jenkins.plugins.imagetagparam.model.AuthType
import io.jenkins.plugins.imagetagparam.model.ImageTag
import io.jenkins.plugins.imagetagparam.model.ValueOrder
import io.jenkins.plugins.imagetagparam.model.errors.RegistryUnavailableError
import io.jenkins.plugins.imagetagparam.model.errors.UnsupportedAuthTypeError
import io.jenkins.plugins.imagetagparam.util.OkHttpUtils
import io.jenkins.plugins.imagetagparam.util.OkHttpUtils.getCacheControl
import java.util.logging.Logger
import okhttp3.Request


object ImageTagService {
  private val logger: Logger = Logger.getLogger(ImageTagService::class.java.name)

  fun getTagList(
    registryEndpoint: String,
    imageName: String,
    credentials: StandardCredentials,
    filter: String,
    valueOrder: ValueOrder
  ) : Result<List<ImageTag>> {
    return kotlin.runCatching {
      val authService = getAuthService(registryEndpoint).getOrThrow()
      val authToken = getAuthToken(authService, imageName, credentials).getOrThrow()
      getImageTagsFromRegistry(registryEndpoint, imageName, authService.authType, authToken).getOrThrow()
    }
  }



  internal fun getImageTagsFromRegistry(
    registryEndpoint: String,
    imageName: String,
    authType: AuthType,
    token: String
  ) : Result<List<ImageTag>> = kotlin.runCatching {
    throw NotImplementedError()
  }
}

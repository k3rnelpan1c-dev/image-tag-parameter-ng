package io.jenkins.plugins.imagetagparam.util

import hudson.FilePath
import io.jenkins.plugins.imagetagparam.ImageTagParameterGlobalConfig
import jenkins.model.Jenkins
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import java.io.File
import java.net.MalformedURLException
import java.net.Proxy
import java.net.URL
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object OkHttpUtils {
  private val log = Logger.getLogger(OkHttpUtils::class.java.name)
  private val jenkins: Jenkins? = Jenkins.getInstanceOrNull()
  private const val PARAMETERS = "parameters"
  private const val PARAMETER_ID = "imageTagParam"
  private const val MEBIBYTE = 1024L * 1024L

  /**
   * Builds a OkHTTP client that respects any proxy set in the Jenkins 'Plugin Manager' and offers a response cache.
   * A proxy will only be applied if the host of the `httpEndpoint` is NOT part of the noProxy values.
   * The response cache is only present if the creation on disk is possible, otherwise there is none
   *
   * @param httpEndpoint the host of the endpoint will get looked up against the noProxy values
   * @return OkHttpClient setup with an appropriate httpProxy value and response cache
   */
  fun getClientWithProxyAndCache(httpEndpoint: String): OkHttpClient {
    try {
      if (jenkins != null) {
        val config: ImageTagParameterGlobalConfig = ImageTagParameterGlobalConfig.get()
        val parameterUserContent: FilePath = jenkins.rootPath
          .child(PARAMETERS)
          .child(PARAMETER_ID)
        if (!parameterUserContent.exists() && !parameterUserContent.isDirectory) {
          parameterUserContent.mkdirs()
        }
        val cacheDir = File(parameterUserContent.toURI().path, "okhttp_cache")
        log.fine("Messages.PLP_OkHttpUtils_fine_CacheCreationSuccess(config.getCacheSize())")
        return OkHttpClient.Builder()
          .cache(Cache(cacheDir, config.getCacheSize() * MEBIBYTE))
          .proxy(getProxy(httpEndpoint))
          .build()
      } else {
        log.fine("Messages.PLP_OkHttpUtils_fine_NoJenkinsInstance()")
      }
    } catch (ex: Exception) {
      log.warning("Messages.PLP_OkHttpUtils_warn_CacheIOException()")
      log.fine("""
        Cache creation failed with: ${ex.javaClass.name}
        EX Message: ${ex.message}
      """.trimIndent()
      )
    }
    return OkHttpClient.Builder()
      .proxy(getProxy(httpEndpoint))
      .build()
  }

  private fun getProxy(httpEndpoint: String): Proxy {
    return if (jenkins?.proxy == null) {
      Proxy.NO_PROXY
    } else {
      try {
        jenkins.proxy.createProxy(URL(httpEndpoint).host)
      } catch (e: MalformedURLException) {
        jenkins.proxy.createProxy(httpEndpoint)
      }
    }
  }

  fun getCacheControl(minutesCached: Int): CacheControl {
    return CacheControl.Builder()
      .maxAge(minutesCached, TimeUnit.MINUTES)
      .build()
  }
}

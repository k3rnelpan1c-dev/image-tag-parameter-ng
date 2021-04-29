package io.jenkins.plugins.imagetagparam

import hudson.Extension
import jenkins.YesNoMaybe
import jenkins.model.GlobalConfiguration
import org.jenkinsci.Symbol
import hudson.ExtensionList
import org.kohsuke.stapler.DataBoundSetter
import hudson.util.FormValidation
import org.kohsuke.stapler.QueryParameter

import org.kohsuke.stapler.verb.POST

@Extension(dynamicLoadable = YesNoMaybe.YES)
@Symbol("imageTagParam", "imageTagParamConfig")
class ImageTagParameterGlobalConfig: GlobalConfiguration() {
  init {
    load()
  }
  companion object {
    /**
     * The default for [.cacheSize].
     */
    private const val DEFAULT_CACHE_SIZE = 50L

    /**
     * The default for [.cacheTime].
     */
    private const val DEFAULT_CACHE_TIME = 0

    /**
     * Get the current ImageTagParameter global configuration.
     *
     * @return the ImageTagParameter configuration, or `null` if Jenkins has been shut down
     */
    fun get(): ImageTagParameterGlobalConfig {
      return ExtensionList.lookupSingleton(ImageTagParameterGlobalConfig::class.java)
    }
  }

  private var cacheSize: Long? = null
  private var cacheTime: Int? = null

  fun getCacheSize(): Long {
    return if (cacheSize != null && cacheSize!! > 0L) cacheSize!! else DEFAULT_CACHE_SIZE
  }

  @DataBoundSetter
  fun setCacheSize(cacheSize: Long?) {
    this.cacheSize = cacheSize
    save()
  }

  @POST
  fun doCheckCacheSize(@QueryParameter cacheSize: Long?): FormValidation? {
    return if (cacheSize != null && cacheSize > 0) {
      FormValidation.ok()
    } else FormValidation.error("Messages.RLP_GlobalConfig_ValidationErr_CacheSize()")
  }

  fun getCacheTime(): Int? {
    return if (cacheTime != null && cacheTime!! > 0) cacheTime else DEFAULT_CACHE_TIME
  }

  @DataBoundSetter
  fun setCacheTime(cacheTime: Int?) {
    this.cacheTime = cacheTime
    save()
  }

  @POST
  fun doCheckCacheTime(@QueryParameter cacheTime: Int?): FormValidation? {
    return if (cacheTime != null && cacheTime >= 0) {
      FormValidation.ok()
    } else FormValidation.error("Messages.RLP_GlobalConfig_ValidationErr_CacheTime()")
  }
}

package io.jenkins.plugins.imagetagparam

import hudson.Extension
import hudson.model.ParameterValue
import hudson.model.SimpleParameterDefinition
import net.sf.json.JSONObject
import org.kohsuke.stapler.StaplerRequest
import hudson.model.ParameterDefinition
import org.jenkinsci.Symbol
import org.kohsuke.stapler.DataBoundConstructor
import javax.annotation.Nonnull


class ImageTagParameterDefinition @DataBoundConstructor constructor(
  name: String,
  description: String?,
  val image: String,
  val filter: String,
  val defaultTag: String,
  val registry: String,
  val credentialId: String
) : SimpleParameterDefinition(name, description) {

  override fun copyWithDefaultValue(defaultValue: ParameterValue): ParameterDefinition {
    return if (defaultValue is ImageTagParameterValue) {
      val value: ImageTagParameterValue = defaultValue
      ImageTagParameterDefinition(
        name, description, image, filter, value.imageTag(), registry, credentialId)
    } else {
      this
    }
  }

  override fun createValue(value: String): ParameterValue {
    return ImageTagParameterValue(name, description, image, value)
  }

  override fun createValue(req: StaplerRequest, jo: JSONObject): ParameterValue {
    return req.bindJSON(ImageTagParameterValue::class.java, jo)
  }

  @Symbol("ImageTag", "ImageTagParam")
  @Extension
  class DescriptorImpl : ParameterDescriptor() {
    @Nonnull
    override fun getDisplayName(): String {
      return "Image Tag Parameter"
    }
  }
}

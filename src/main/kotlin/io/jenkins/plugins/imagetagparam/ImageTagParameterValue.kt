package io.jenkins.plugins.imagetagparam

import hudson.model.ParameterValue

class ImageTagParameterValue(
  name: String,
  description: String?,
  var image: String,
  var tag: String
) : ParameterValue(name, description) {
  fun imageTag(): String = "${image}:${tag}"
}

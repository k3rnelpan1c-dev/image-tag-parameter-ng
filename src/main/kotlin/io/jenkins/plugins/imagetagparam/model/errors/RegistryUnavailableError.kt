package io.jenkins.plugins.imagetagparam.model.errors

class RegistryUnavailableError(registryEndpoint: String): Error("Registry is unavailable on '$registryEndpoint'")

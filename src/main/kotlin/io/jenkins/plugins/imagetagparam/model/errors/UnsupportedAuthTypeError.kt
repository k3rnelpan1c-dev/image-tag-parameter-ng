package io.jenkins.plugins.imagetagparam.model.errors

class UnsupportedAuthTypeError(authTypeName: String) : Error("The auth type '$authTypeName' is not supported")

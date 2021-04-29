package io.jenkins.plugins.imagetagparam.logic

import io.jenkins.plugins.imagetagparam.model.AuthType
import io.kotlintest.fail
import io.kotlintest.shouldBe
import io.kotlintest.tables.forAll
import io.kotlintest.tables.headers
import io.kotlintest.tables.row
import io.kotlintest.tables.table
import kotlin.test.Test


class RegistryAuthServiceTests {
  @Test
  fun getAuthServiceSuccessTest() {
    table(
      headers("endpointUrl", "authType", "authRealm", "authService"),
      row("https://registry-1.docker.io", AuthType.BEARER, "https://auth.docker.io/token", "registry.docker.io"),
      row("https://ghcr.io", AuthType.BEARER, "https://ghcr.io/token", "ghcr.io\",scope=\"repository:user/image:pull"),
      row("https://quay.io", AuthType.BEARER, "https://quay.io/v2/auth", "quay.io"),
      row("https://gcr.io", AuthType.BEARER, "https://gcr.io/v2/token", "gcr.io"),
    ).forAll { endpointUrl, authType, authRealm, authService ->
      val serviceResult = RegistryAuthService.getAuthService(endpointUrl)
      serviceResult.fold(
        {ok ->
          ok.authType shouldBe authType
          ok.authRealm shouldBe authRealm
          ok.authService shouldBe authService
        },
        {err -> fail(err.stackTraceToString())}
      )
    }
  }

  @Test
  internal fun getAuthServiceFailureTest() {
    table(
      headers("endpointUrl", "errorMsg"),
      row("", "Expected URL scheme 'http' or 'https' but no colon was found"),
      row("http://localhost", "Failed to connect to localhost/0:0:0:0:0:0:0:1:80"),
      row("https://google.com", "The auth type '' is not supported")
    ).forAll { endpointUrl, errorMsg ->
      val serviceResult = RegistryAuthService.getAuthService(endpointUrl)
      serviceResult.isFailure shouldBe true
      serviceResult.exceptionOrNull()?.message shouldBe errorMsg
    }
  }
}

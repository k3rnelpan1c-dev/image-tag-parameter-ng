package io.jenkins.plugins.imagetagparam.util

import com.cloudbees.plugins.credentials.CredentialsMatcher
import com.cloudbees.plugins.credentials.CredentialsMatchers
import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.common.StandardCredentials
import com.cloudbees.plugins.credentials.common.StandardListBoxModel
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
import hudson.model.Item
import hudson.security.ACL
import hudson.util.FormValidation
import hudson.util.ListBoxModel
import jenkins.model.Jenkins
import org.apache.commons.lang.StringUtils
import org.jenkinsci.plugins.plaincredentials.StringCredentials
import java.util.*
import java.util.logging.Logger

object CredentialsUtils {
  private val log = Logger.getLogger(CredentialsUtils::class.java.name)

  fun doFillCredentialsIdItems(
    context: Item?,
    credentialsId: String
  ): ListBoxModel {
    if (context == null && !Jenkins.get().hasPermission(Jenkins.ADMINISTER)
      || (context != null && !context.hasPermission(Item.EXTENDED_READ)
        && !context.hasPermission(CredentialsProvider.USE_ITEM))
    ) {
      log.info("Messages.RLP_CredentialsUtils_info_NoPermission()")
      return StandardListBoxModel().includeCurrentValue(credentialsId)
    }
    return StandardListBoxModel()
      .includeEmptyValue()
      .includeMatchingAs(
        ACL.SYSTEM,
        context,
        StandardCredentials::class.java, emptyList(),
        CredentialsMatchers.anyOf(
          CredentialsMatchers.instanceOf(StringCredentials::class.java),
          CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials::class.java)
        )
      )
      .includeCurrentValue(credentialsId)
  }

  fun doCheckCredentialsId(
    context: Item?,
    credentialsId: String
  ): FormValidation {
    if (context == null && !Jenkins.get().hasPermission(Jenkins.ADMINISTER)
      || (context != null && !context.hasPermission(Item.EXTENDED_READ)
        && !context.hasPermission(CredentialsProvider.USE_ITEM))
    ) {
      return FormValidation.ok()
    }
    if (StringUtils.isBlank(credentialsId)) {
      return FormValidation.ok()
    }
    if (credentialsId.startsWith("\${") && credentialsId.endsWith("}")) {
      return FormValidation.warning("Messages.RLP_CredentialsUtils_ValidationWrn_ExpressionBased()")
    }
    return if (!findCredentials(credentialsId).isPresent) {
      FormValidation.error("Messages.RLP_CredentialsUtils_ValidationErr_CannotFind()")
    } else FormValidation.ok()
  }

  fun findCredentials(credentialsId: String): Optional<StandardCredentials> {
    if (StringUtils.isBlank(credentialsId)) {
      return Optional.empty<StandardCredentials>()
    }
    val lookupCredentials: List<StandardCredentials> = CredentialsProvider.lookupCredentials(
      StandardCredentials::class.java,
      null as Item?,
      ACL.SYSTEM, emptyList()
    )
    val allOf: CredentialsMatcher = CredentialsMatchers.allOf(
      CredentialsMatchers.withId(credentialsId),
      CredentialsMatchers.anyOf(
        CredentialsMatchers.instanceOf(StringCredentials::class.java),
        CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials::class.java)
      )
    )
    return Optional.ofNullable(CredentialsMatchers.firstOrNull(lookupCredentials, allOf))
  }
}

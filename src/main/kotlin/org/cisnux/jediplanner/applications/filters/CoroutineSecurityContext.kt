package org.cisnux.jediplanner.applications.filters

import kotlinx.coroutines.ThreadContextElement
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import kotlin.coroutines.CoroutineContext

data class ContextPayload(
    @JvmField val username: String,
    @JvmField val password: String? = null,
    val roles: List<String> = emptyList(),
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return roles.map { role ->
            GrantedAuthority { role }
        }
    }

    override fun getPassword(): String? {
        return password
    }

    override fun getUsername(): String {
        return username
    }
}

class CoroutineSecurityContext(val ctx: SecurityContext = SecurityContextHolder.getContext()) : ThreadContextElement<SecurityContext?> {
    companion object Key : CoroutineContext.Key<CoroutineSecurityContext>

    override val key = Key
    override fun restoreThreadContext(
        context: CoroutineContext, oldState: SecurityContext?
    ) = if (oldState == null) SecurityContextHolder.clearContext()
    else SecurityContextHolder.setContext(oldState)

    override fun updateThreadContext(context: CoroutineContext): SecurityContext? {
        val old = SecurityContextHolder.getContext()
        SecurityContextHolder.setContext(ctx)
        return old.takeIf { it.authentication?.isAuthenticated == true }
    }
}
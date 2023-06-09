package com.AlMLand.security

import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.*

@Component
class AuditorAwareImpl : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> = Optional.of("customname")
}
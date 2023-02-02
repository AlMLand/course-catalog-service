package com.AlMLand.aop

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableLoadTimeWeaving
import org.springframework.context.annotation.EnableLoadTimeWeaving.AspectJWeaving.ENABLED

@Configuration
@EnableLoadTimeWeaving(aspectjWeaving = ENABLED)
class ColumnTransformerAspectConfiguration {
    // @Bean
    // fun columnTransformerAspect() = Aspects.aspectOf(ColumnTransformerAspect::class.java)
}
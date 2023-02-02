package com.AlMLand.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.annotation.Value

@Aspect
// @Component
class ColumnTransformerAspect(
    @Value("\${secret-key.sensible-data}") private val secretKey: String
) {
    @Around("execution(* org.hibernate.cfg.Ejb3Column.processExpression(..))")
    // @Around("execution(* com.AlMLand.service.CourseService.findAllCourses(..))")
    // @Around("@annotation(org.hibernate.annotations.ColumnTransformer)")
    fun test(joinPoint: ProceedingJoinPoint): Any {
        println("AAAAAAAAAAAAA: ${joinPoint.signature.name}")
        println("BBBBBBBBBBBBB: $joinPoint")
        println("CCCCCCCCCCCCC: $secretKey")
        return joinPoint.proceed()
    }
}
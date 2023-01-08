package com.AlMLand.converter

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.InvalidKeyException
import java.util.*
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.locks.ReentrantLock
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.spec.SecretKeySpec
import javax.persistence.AttributeConverter

@SuppressWarnings("TooGenericExceptionThrown")
@Component
class DbAttributeEncryptor(
    @Value("\${attribute-encryptor.secret}") private val secret: String,
    @Value("\${attribute-encryptor.algorithm}") private val algorithm: String
) : AttributeConverter<String, String> {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        private val lock = ReentrantLock()
    }

    override fun convertToDatabaseColumn(attribute: String?): String =
        if (isUnlocked()) {
            try {
                SecretKeySpec(secret.encodeToByteArray(), algorithm).let {
                    Cipher.getInstance(algorithm).let { cipher ->
                        cipher.init(Cipher.ENCRYPT_MODE, it)
                        Base64.getEncoder().encodeToString(
                            cipher.doFinal(
                                attribute?.encodeToByteArray()
                                    ?: throw RuntimeException("The attribute to encrypt is null")
                            )
                        )
                    }
                }
            } catch (re: RuntimeException) {
                logger.error("Error by encoding: {}", re.message)
                when (re) {
                    is IllegalBlockSizeException -> throw RuntimeException("illegal block size exception...")
                    is BadPaddingException -> throw RuntimeException("bad padding exception...")
                    is InvalidKeyException -> throw RuntimeException("invalid key exception...")
                    else -> throw RuntimeException("surprise exception...")
                }
            } finally {
                lock.unlock()
            }
        } else {
            logger.error("Reentrant try lock value is too small")
            throw RuntimeException("Attribute: $attribute is not encrypted, time out. The count of waiting threads: ${lock.queueLength}")
        }

    override fun convertToEntityAttribute(dbData: String?): String =
        if (isUnlocked()) {
            try {
                SecretKeySpec(secret.encodeToByteArray(), algorithm).let {
                    Cipher.getInstance(algorithm).let { cipher ->
                        cipher.init(Cipher.DECRYPT_MODE, it)
                        String(cipher.doFinal(Base64.getDecoder().decode(dbData)))
                    }
                }
            } catch (re: RuntimeException) {
                logger.error("Error by decoding: {}", re.message)
                when (re) {
                    is IllegalBlockSizeException -> throw RuntimeException("illegal block size exception...")
                    is BadPaddingException -> throw RuntimeException("bad padding exception...")
                    is InvalidKeyException -> throw RuntimeException("invalid key exception...")
                    else -> throw RuntimeException("surprise exception...")
                }
            } finally {
                lock.unlock()
            }
        } else {
            logger.error("Reentrant try lock value is too small")
            throw RuntimeException("Attribute: $dbData is not decrypted, time out. The count of waiting threads: ${lock.queueLength}")
        }

    private fun isUnlocked() = lock.tryLock(2000, MILLISECONDS)
}
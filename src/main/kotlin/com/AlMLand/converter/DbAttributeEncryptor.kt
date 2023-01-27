package com.AlMLand.converter

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.InvalidKeyException
import java.util.*
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.locks.ReentrantLock
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.IllegalBlockSizeException
import javax.crypto.spec.SecretKeySpec
import javax.persistence.AttributeConverter

@Component
class DbAttributeEncryptor  (
    @Value("\${attribute-encryptor.secret}") private val secret: String,
    @Value("\${attribute-encryptor.algorithm}") private val algorithm: String
) : AttributeConverter<String, String> {
    companion object {
        private val lock = ReentrantLock()
    }

    override fun convertToDatabaseColumn(attribute: String?): String? =
        attribute?.let {
            if (isUnlocked()) {
                try {
                    encode(cipherInstance(initSecretKeySpec(), ENCRYPT_MODE), it)
                } catch (t: Throwable) {
                    runtimeExceptionHandling(t)
                } finally {
                    lock.unlock()
                }
            } else {
                throw RuntimeException("Attribute: $it is not encrypted. Reentrant try lock value is too small. The count of waiting threads: ${lock.queueLength}")
            }
        }

    private fun encode(cipher: Cipher, toConvert: String): String? = Base64.getEncoder().encodeToString(
        cipher.doFinal(toConvert.encodeToByteArray())
    )

    override fun convertToEntityAttribute(dbData: String?): String? =
        dbData?.let {
            if (isUnlocked()) {
                try {
                    decode(cipherInstance(initSecretKeySpec(), DECRYPT_MODE), it)
                } catch (t: Throwable) {
                    runtimeExceptionHandling(t)
                } finally {
                    lock.unlock()
                }
            } else {
                throw RuntimeException("Attribute: $it is not decrypted. Reentrant try lock value is too small. The count of waiting threads: ${lock.queueLength}")
            }
        }

    private fun runtimeExceptionHandling(re: Throwable): Nothing = when (re) {
        is IllegalBlockSizeException -> throw RuntimeException("illegal block size exception...")
        is BadPaddingException -> throw RuntimeException("bad padding exception...")
        is InvalidKeyException -> throw RuntimeException("invalid key exception...")
        else -> throw RuntimeException("surprise exception...")
    }

    private fun decode(cipher: Cipher, toConvert: String) =
        String(cipher.doFinal(Base64.getDecoder().decode(toConvert)))

    private fun cipherInstance(secretKeySpec: SecretKeySpec, mode: Int): Cipher = Cipher.getInstance(algorithm).apply {
        init(mode, secretKeySpec)
    }

    private fun initSecretKeySpec() = SecretKeySpec(secret.encodeToByteArray(), algorithm)

    private fun isUnlocked() = lock.tryLock(2000, MILLISECONDS)
}
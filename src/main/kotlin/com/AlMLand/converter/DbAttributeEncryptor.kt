package com.AlMLand.converter

import com.AlMLand.converter.exception.AttributeNotDecryptedException
import com.AlMLand.converter.exception.AttributeNotEncryptedException
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
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.persistence.AttributeConverter
import kotlin.text.Charsets.UTF_8

@Component
class DbAttributeEncryptor(
    @Value("\${crypto.secret}") private val secret: String,
    @Value("\${crypto.algorithm}") private val algorithm: String,
    @Value("\${crypto.transformation}") private val transformation: String
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
                throw AttributeNotEncryptedException("Attribute: $it is not encrypted. Reentrant try lock value is too small. The count of waiting threads: ${lock.queueLength}")
            }
        }

    private fun encode(cipher: Cipher, toConvert: String): String = Base64.getEncoder()
        .encodeToString(
            cipher.doFinal(toConvert.toByteArray())
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
                throw AttributeNotDecryptedException("Attribute: $it is not decrypted. Reentrant try lock value is too small. The count of waiting threads: ${lock.queueLength}")
            }
        }

    private fun runtimeExceptionHandling(re: Throwable): Nothing = when (re) {
        is IllegalBlockSizeException -> throw RuntimeException("illegal block size exception...", re)
        is BadPaddingException -> throw RuntimeException("bad padding exception...", re)
        is InvalidKeyException -> throw RuntimeException("invalid key exception...", re)
        else -> throw RuntimeException("surprise exception...", re)
    }

    private fun decode(cipher: Cipher, toConvert: String) =
        String(
            cipher.doFinal(
                Base64.getDecoder().decode(toConvert)
            )
        )

    private fun cipherInstance(secretKeySpec: SecretKeySpec, mode: Int): Cipher =
        Cipher.getInstance(transformation).apply {
            init(mode, secretKeySpec, IvParameterSpec(ByteArray(16)))
        }

    private fun initSecretKeySpec() = SecretKeySpec(secret.toByteArray(UTF_8), algorithm)

    private fun isUnlocked() = lock.tryLock(2000, MILLISECONDS)
}

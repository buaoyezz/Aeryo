package net.zzbuaoye.aeryo.util

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.ECGenParameterSpec

object BiometricAuthHelper {
    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val KEY_ALIAS = "aeryo_privacy_auth_key_v1"
    private const val SIGNATURE_ALGORITHM = "SHA256withECDSA"
    private const val EC_CURVE = "secp256r1"
    private const val CHALLENGE_SIZE_BYTES = 32

    private val secureRandom = SecureRandom()

    fun canAuthenticate(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        val result = biometricManager.canAuthenticate(allowedAuthenticators())
        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun authenticate(
        activity: FragmentActivity,
        title: String = "验证身份以访问隐私模式",
        subtitle: String = "请识别指纹或输入设备凭据以校验安全身份",
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        val authenticators = allowedAuthenticators()
        val authenticationStatus = BiometricManager.from(activity).canAuthenticate(authenticators)
        if (authenticationStatus != BiometricManager.BIOMETRIC_SUCCESS) {
            onError(authenticationErrorMessage(authenticationStatus))
            return
        }

        val challenge = ByteArray(CHALLENGE_SIZE_BYTES).also(secureRandom::nextBytes)
        val signingSignature = runCatching(::createSigningSignature).getOrElse {
            onError("无法初始化安全身份验证，请重试")
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val authenticatedSignature = result.cryptoObject?.signature
                    if (authenticatedSignature == null) {
                        onError("身份验证未返回安全凭据")
                        return
                    }

                    val verified = runCatching {
                        authenticatedSignature.update(challenge)
                        val signedChallenge = authenticatedSignature.sign()
                        verifySignedChallenge(challenge, signedChallenge)
                    }.getOrDefault(false)

                    if (verified) {
                        onSuccess()
                    } else {
                        onError("安全身份验证失败，请重试")
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("指纹验证失败，请重试")
                }
            }
        )

        val promptBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(authenticators)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            promptBuilder.setNegativeButtonText("取消")
        }

        val promptInfo = promptBuilder.build()

        runCatching {
            biometricPrompt.authenticate(
                promptInfo,
                BiometricPrompt.CryptoObject(signingSignature)
            )
        }.onFailure {
            onError("无法启动安全身份验证，请重试")
        }
    }

    private fun allowedAuthenticators(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
        } else {
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        }

    private fun createSigningSignature(): Signature {
        val keyPair = try {
            getOrCreateKeyPair()
        } catch (_: KeyPermanentlyInvalidatedException) {
            deleteKey()
            getOrCreateKeyPair()
        }

        return try {
            Signature.getInstance(SIGNATURE_ALGORITHM).apply {
                initSign(keyPair.private)
            }
        } catch (_: KeyPermanentlyInvalidatedException) {
            deleteKey()
            Signature.getInstance(SIGNATURE_ALGORITHM).apply {
                initSign(getOrCreateKeyPair().private)
            }
        }
    }

    private fun getOrCreateKeyPair(): KeyPair {
        val keyStore = loadKeyStore()
        val privateKey = keyStore.getKey(KEY_ALIAS, null)
        val publicKey = keyStore.getCertificate(KEY_ALIAS)?.publicKey
        if (privateKey != null && publicKey != null) {
            return KeyPair(publicKey, privateKey as java.security.PrivateKey)
        }

        val parameterSpecBuilder = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_SIGN
        )
            .setAlgorithmParameterSpec(ECGenParameterSpec(EC_CURVE))
            .setDigests(KeyProperties.DIGEST_SHA256)
            .setUserAuthenticationRequired(true)
            .setInvalidatedByBiometricEnrollment(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            parameterSpecBuilder.setUserAuthenticationParameters(
                0,
                KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL
            )
        }

        return KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, KEYSTORE_PROVIDER).run {
            initialize(parameterSpecBuilder.build())
            generateKeyPair()
        }
    }

    private fun verifySignedChallenge(challenge: ByteArray, signatureBytes: ByteArray): Boolean {
        val publicKey = loadKeyStore().getCertificate(KEY_ALIAS)?.publicKey ?: return false
        return Signature.getInstance(SIGNATURE_ALGORITHM).run {
            initVerify(publicKey)
            update(challenge)
            verify(signatureBytes)
        }
    }

    private fun loadKeyStore(): KeyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
        load(null)
    }

    private fun deleteKey() {
        loadKeyStore().deleteEntry(KEY_ALIAS)
    }

    private fun authenticationErrorMessage(status: Int): String = when (status) {
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "请先在系统设置中录入指纹或设备凭据"
        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "此设备不支持安全身份验证"
        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> "身份验证硬件暂时不可用，请稍后重试"
        BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> "请安装系统安全更新后再使用身份验证"
        else -> "当前无法使用安全身份验证"
    }
}

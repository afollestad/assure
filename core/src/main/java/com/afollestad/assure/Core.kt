/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afollestad.assure

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.CryptoObject
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.afollestad.assure.crypto.Credentials
import com.afollestad.assure.crypto.Crypto
import com.afollestad.assure.crypto.CryptoMode
import com.afollestad.assure.crypto.OnDecryptor
import com.afollestad.assure.crypto.OnEncryptor
import com.afollestad.assure.crypto.RealDecryptor
import com.afollestad.assure.crypto.RealEncryptor
import com.afollestad.assure.result.BiometricError.UNKNOWN
import com.afollestad.assure.result.toBiometricError

internal fun BiometricPrompt.cancelAuthOnPause(
  lifecycleOwner: LifecycleOwner
): BiometricPrompt = apply {
  val observer = object : LifecycleObserver {
    @Suppress("unused")
    @OnLifecycleEvent(ON_PAUSE)
    fun onPause() {
      cancelAuthentication()
      lifecycleOwner.lifecycle.removeObserver(this)
    }
  }
  lifecycleOwner.lifecycle.addObserver(observer)
}

internal fun createAuthenticateCallback(
  callback: () -> Unit
): BiometricPrompt.AuthenticationCallback {
  return object : BiometricPrompt.AuthenticationCallback() {
    override fun onAuthenticationError(
      errorCode: Int,
      errString: CharSequence
    ) {
      throw BiometricErrorException(errorCode.toBiometricError(), errString.toString())
    }

    override fun onAuthenticationFailed() {
      throw BiometricErrorException(UNKNOWN, "Unknown failure")
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
      callback()
    }
  }
}

internal fun createEncryptCallback(
  credentials: Credentials?,
  callback: OnEncryptor
): BiometricPrompt.AuthenticationCallback {
  return object : BiometricPrompt.AuthenticationCallback() {
    override fun onAuthenticationError(
      errorCode: Int,
      errString: CharSequence
    ) {
      throw BiometricErrorException(errorCode.toBiometricError(), errString.toString())
    }

    override fun onAuthenticationFailed() {
      throw BiometricErrorException(UNKNOWN, "Unknown failure")
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
      result.cryptoObject?.let {
        credentials?.iv = it.cipher?.iv ?: error("Unable to get IV")
      }
      callback(RealEncryptor(cryptoObject = result.cryptoObject))
    }
  }
}

internal fun createDecryptCallback(
  callback: OnDecryptor
): BiometricPrompt.AuthenticationCallback {
  return object : BiometricPrompt.AuthenticationCallback() {
    override fun onAuthenticationError(
      errorCode: Int,
      errString: CharSequence
    ) {
      throw BiometricErrorException(errorCode.toBiometricError(), errString.toString())
    }

    override fun onAuthenticationFailed() {
      throw BiometricErrorException(UNKNOWN, "Unknown failure")
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
      callback(RealDecryptor(cryptoObject = result.cryptoObject))
    }
  }
}

internal fun BiometricPrompt.authenticate(
  context: Context,
  prompt: Prompt,
  mode: CryptoMode,
  credentials: Credentials? = null
) {
  val promptInfo: PromptInfo = prompt.toPromptInfo(context)
  val cryptoObject: CryptoObject? = credentials
      ?.let { Crypto(it) }
      ?.getObject(
          mode = mode,
          keyGenModifier = prompt::applyToKeyGenSpec
      )
  cryptoObject
      ?.let { authenticate(promptInfo, it) }
      ?: authenticate(promptInfo)
}

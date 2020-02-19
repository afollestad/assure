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
@file:Suppress("UNCHECKED_CAST", "unused")

package com.afollestad.assure

import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.afollestad.assure.crypto.Credentials
import com.afollestad.assure.crypto.CryptoMode.DECRYPT
import com.afollestad.assure.crypto.CryptoMode.ENCRYPT
import com.afollestad.assure.crypto.Decryptor
import com.afollestad.assure.crypto.Encryptor
import com.afollestad.assure.crypto.OnDecryptor
import com.afollestad.assure.crypto.OnEncryptor

/**
 * Shows the biometric authentication prompt. Calls the [onAuthentication] callback when
 * authentication is successful, otherwise a [BiometricErrorException] is thrown with details.
 *
 * @param prompt Provides settings for the biometric prompt.
 * @param onAuthentication The callback to invoke with the result.
 *
 * @author Aidan Follestad (@afollestad)
 */
fun Fragment.authenticate(
  prompt: Prompt,
  onAuthentication: () -> Unit
) {
  val activity = requireActivity()
  val executor = ContextCompat.getMainExecutor(activity)
  val callback = createAuthenticateCallback(onAuthentication)

  BiometricPrompt(this, executor, callback)
      .cancelAuthOnPause(this)
      .authenticate(
          context = activity,
          prompt = prompt,
          mode = ENCRYPT
      )
}

/**
 * Shows the biometric authentication prompt, authenticating a key to encrypt data. Sends an
 * [Encryptor] through the [onAuthentication] callback when authentication is successful.
 * Otherwise a [BiometricErrorException] is thrown with details.
 *
 * @param credentials Encryption settings, the key is important here. The IV is populated after auth.
 * @param prompt Provides settings for the biometric prompt.
 * @param onAuthentication The callback to invoke with the result.
 *
 * @author Aidan Follestad (@afollestad)
 */
fun Fragment.authenticateForEncrypt(
  credentials: Credentials,
  prompt: Prompt,
  onAuthentication: OnEncryptor
) {
  val activity = requireActivity()
  val executor = ContextCompat.getMainExecutor(activity)
  val callback = createEncryptCallback(
      credentials = credentials,
      callback = onAuthentication
  )

  BiometricPrompt(this, executor, callback)
      .cancelAuthOnPause(this)
      .authenticate(
          context = activity,
          prompt = prompt,
          mode = ENCRYPT,
          credentials = credentials
      )
}

/**
 * Shows the biometric authentication prompt, authenticating a key to decrypt data. Sends an
 * [Decryptor] through the [onAuthentication] callback when authentication is successful.
 * Otherwise a [BiometricErrorException] is thrown with details.
 *
 * @param credentials Decryption settings, both a key and populated IV are needed.
 * @param prompt Provides settings for the biometric prompt.
 * @param onAuthentication The callback to invoke with the result.
 *
 * @author Aidan Follestad (@afollestad)
 */
fun Fragment.authenticateForDecrypt(
  credentials: Credentials,
  prompt: Prompt,
  onAuthentication: OnDecryptor
) {
  val activity = requireActivity()
  val executor = ContextCompat.getMainExecutor(activity)
  val callback = createDecryptCallback(onAuthentication)

  BiometricPrompt(this, executor, callback)
      .authenticate(
          context = activity,
          prompt = prompt,
          mode = DECRYPT,
          credentials = credentials
      )
}

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
@file:Suppress("unused")

package com.afollestad.assure.coroutines

import androidx.fragment.app.FragmentActivity
import com.afollestad.assure.BiometricErrorException
import com.afollestad.assure.Prompt
import com.afollestad.assure.authenticate
import com.afollestad.assure.authenticateForDecrypt
import com.afollestad.assure.authenticateForEncrypt
import com.afollestad.assure.crypto.Credentials
import com.afollestad.assure.crypto.Decryptor
import com.afollestad.assure.crypto.Encryptor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Shows the biometric authentication prompt. Throws a [BiometricErrorException] if authentication
 * is not successful.
 *
 * @param prompt Provides settings for the biometric prompt.
 *
 * @author Aidan Follestad (@afollestad)
 */
suspend fun FragmentActivity.authenticate(
  prompt: Prompt
) {
  return suspendCoroutine { continuation ->
    try {
      authenticate(prompt) {
        continuation.resume(Unit)
      }
    } catch (t: Throwable) {
      continuation.resumeWithException(t)
    }
  }
}

/**
 * Shows the biometric authentication prompt, authenticating a key to encrypt data. Returns an
 * [Encryptor] when authentication is successful. Otherwise a [BiometricErrorException] is thrown
 * with details.
 *
 * @param credentials Encryption settings, the key is important here. The IV is populated after auth.
 * @param prompt Provides settings for the biometric prompt.
 *
 * @author Aidan Follestad (@afollestad)
 */
suspend fun FragmentActivity.authenticateForEncrypt(
  credentials: Credentials,
  prompt: Prompt
): Encryptor {
  return suspendCoroutine { continuation ->
    try {
      authenticateForEncrypt(
          credentials = credentials,
          prompt = prompt
      ) {
        continuation.resume(this)
      }
    } catch (t: Throwable) {
      continuation.resumeWithException(t)
    }
  }
}

/**
 * Shows the biometric authentication prompt, authenticating a key to decrypt data. Returns an
 * [Decryptor] when authentication is successful. Otherwise a [BiometricErrorException] is thrown
 * with details.
 *
 * @param credentials Decryption settings, both a key and populated IV are needed.
 * @param prompt Provides settings for the biometric prompt.
 *
 * @author Aidan Follestad (@afollestad)
 */
suspend fun FragmentActivity.authenticateForDecrypt(
  credentials: Credentials,
  prompt: Prompt
): Decryptor {
  return suspendCoroutine { continuation ->
    try {
      authenticateForDecrypt(
          credentials = credentials,
          prompt = prompt
      ) {
        continuation.resume(this)
      }
    } catch (t: Throwable) {
      continuation.resumeWithException(t)
    }
  }
}

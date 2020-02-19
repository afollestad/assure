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

package com.afollestad.assure.rxjava

import androidx.fragment.app.FragmentActivity
import com.afollestad.assure.BiometricErrorException
import com.afollestad.assure.Prompt
import com.afollestad.assure.authenticate
import com.afollestad.assure.authenticateForDecrypt
import com.afollestad.assure.authenticateForEncrypt
import com.afollestad.assure.crypto.Credentials
import com.afollestad.assure.crypto.Decryptor
import com.afollestad.assure.crypto.Encryptor
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Shows the biometric authentication prompt. The returned [Completable] will complete when
 * authentication is successful, otherwise the [Single]'s `onError` is invoked with a
 * [BiometricErrorException].
 *
 * @param prompt Provides settings for the biometric prompt.
 *
 * @author Aidan Follestad (@afollestad)
 */
fun FragmentActivity.authenticate(
  prompt: Prompt
): Completable {
  return Completable.create { emitter ->
    try {
      authenticate(prompt) {
        emitter.onComplete()
      }
    } catch (t: Throwable) {
      emitter.onError(t)
    }
  }
}

/**
 * Shows the biometric authentication prompt, authenticating a key to encrypt data. Emits a
 * [Encryptor] through the returned [Single] when authentication is successful, otherwise the
 * [Single]'s `onError` is invoked with a [BiometricErrorException].
 *
 * @param credentials Encryption settings, the key is important here. The IV is populated after auth.
 * @param prompt Provides settings for the biometric prompt.
 *
 * @author Aidan Follestad (@afollestad)
 */
fun FragmentActivity.authenticateForEncrypt(
  credentials: Credentials,
  prompt: Prompt
): Single<Encryptor> {
  return Single.create { emitter ->
    try {
      authenticateForEncrypt(
          credentials = credentials,
          prompt = prompt
      ) {
        emitter.onSuccess(this)
      }
    } catch (t: Throwable) {
      emitter.onError(t)
    }
  }
}

/**
 * Shows the biometric authentication prompt, authenticating a key to decrypt data. Emits a
 * [Decryptor] through the returned [Single] when authentication is successful, otherwise the
 * [Single]'s `onError` is invoked with a [BiometricErrorException].
 *
 * @param credentials Decryption settings, both a key and populated IV are needed.
 * @param prompt Provides settings for the biometric prompt.
 *
 * @author Aidan Follestad (@afollestad)
 */
fun FragmentActivity.authenticateForDecrypt(
  credentials: Credentials,
  prompt: Prompt
): Single<Decryptor> {
  return Single.create { emitter ->
    try {
      authenticateForDecrypt(
          credentials = credentials,
          prompt = prompt
      ) {
        emitter.onSuccess(this)
      }
    } catch (t: Throwable) {
      emitter.onError(t)
    }
  }
}

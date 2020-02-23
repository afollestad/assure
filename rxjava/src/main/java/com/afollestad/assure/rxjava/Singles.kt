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

import com.afollestad.assure.BiometricErrorException
import io.reactivex.Single
import io.reactivex.functions.Consumer

/**
 * Similar to [Single.doOnError] but only invokes [onError] for [BiometricErrorException].
 *
 * @author Aidan Follestad (@afollestad)
 */
fun <T> Single<T>.doOnBiometricError(onError: Consumer<BiometricErrorException>) =
  doOnError { throwable ->
    if (throwable is BiometricErrorException) {
      onError.accept(throwable)
    }
  }

/**
 * Similar to [Single.doOnError] but only invokes [onError] for [BiometricErrorException].
 *
 * @author Aidan Follestad (@afollestad)
 */
fun <T> Single<T>.doOnBiometricError(onError: (error: BiometricErrorException) -> Unit) =
  doOnBiometricError(Consumer { onError(it) })

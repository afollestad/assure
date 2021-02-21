## Assure

Assure is a Kotlin library that makes biometric authentication quick and easy.

[![Android CI](https://github.com/afollestad/assure/workflows/Android%20CI/badge.svg)](https://github.com/afollestad/assure/actions?query=workflow%3A%22Android+CI%22)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0.html)

## Table of Contents

2. [Core](#usage)
    1. [Gradle Dependency](#gradle-dependency)
    2. [Prompt](#prompt)
    2. [Authentication](#authentication)
    3. [More on Credentials](#more-on-credentials)
    4. [More on Encryptor and Decryptor](#more-on-encryptor-and-decryptor)
3. [Coroutines](#coroutines)
    1. [Gradle Dependency](#gradle-dependency-1)
    2. [Usage](#usage-1)
4. [RxJava](#rxjava)
    1. [Gradle Dependency](#gradle-dependency-2)
    2. [Usage](#usage-2)

---

## Core

[ ![Core](https://img.shields.io/maven-central/v/com.afollestad.assure/core?style=flat&label=Core) ](https://repo1.maven.org/maven2/com/afollestad/assure/core)

### Gradle Dependency

```gradle
dependencies {

  implementation 'com.afollestad.assure:core:0.2.0'
}
```

---

### Prompt

Before you can begin authentication, you need to know about the `Prompt` class. Here is the
definition:

```kotlin
data class Prompt(
  @StringRes val title: Int,
  @StringRes val subtitle: Int? = null,
  @StringRes val description: Int? = null,
  @StringRes val negativeButtonText: Int? = null,
  val confirmRequired: Boolean = false,
  val deviceCredentialsAllowed: Boolean = false,
  val validityDurationSeconds: Int? = null
)
```

* `title`: Set the title to display.
* `subtitle`: Set the subtitle to display.
* `description`: Set the description to display.
* `negativeButtonText`: Set the text for the negative button. This would typically be used as
a "Cancel" button, but may be also used to show an alternative method for authentication,
such as screen that asks for a backup password.
* `confirmRequired`: Only applies to Q and above. A hint to the system to require user
confirmation after a biometric has been authenticated. For example, implicit modalities like
Face and Iris authentication are passive, meaning they don't require an explicit user action
to complete. When set to 'false', the user action (e.g. pressing a button) will not be
required. BiometricPrompt will require confirmation by default.
* `deviceCredentialsAllowed`: The user will first be prompted to authenticate with biometrics,
but also given the option to authenticate with their device PIN, pattern, or password.
Developers should first check [android.app.KeyguardManager.isDeviceSecure()] before enabling.
* `validityDurationSeconds`: Sets the duration of time (seconds) for which this key is
authorized to be used after the user is successfully authenticated. This has effect if the
key requires user authentication for its use. By default, if user authentication is required,
it must take place for every use of the key.

---

### Authentication

*This library exposes extension methods on `FragmentActivity` (which any `AppCompatActivity` is),
and `Fragment` (from AndroidX). Examples are below.*

**Plain authentication:**

```kotlin
val prompt: Prompt = // ...

authenticate(prompt) { error ->
  // If `error` IS null, auth was successful.
}
```

**Authentication to encrypt data:** After successful authentication, the `Credentials` given will
now have a populated IV field.

```kotlin
val credentials = Credentials("default")
val prompt: Prompt = // ...
val plainTextData: ByteArray = // ...

authenticateForEncrypt(
    credentials = credentials,
    prompt = prompt
) { error ->
  // If `error` is NOT null, there was an error.Handle with `e.error` and `e.errorMessage`. Else...
  val encryptedData: ByteArray = encrypt(plainTextData)
  ...
}
```

**Authentication to decrypt data:** A populated IV is needed in the credentials.

```kotlin
val credentials: Credentials = // ...
val prompt: Prompt = // ...
val encryptedData: ByteArray = // ...

authenticateForDecrypt(
    credentials = credentials,
    prompt = prompt
) { error ->
  // If `error` is NOT null, there was an error.Handle with `error.error` and `error.errorMessage`. Else...
  val decryptedData: ByteArray = decrypt(encryptedData)
  ...
}
```

---

### More on Credentials

`Credentials` is a `Parcelable`, so it can be parcelized. There are also two extension functions on
it that can be used to convert Credentials to and from ByteArrays.

```kotlin
val credentials: Credentials = // ...

val serialized: ByteArray = credentials.serialize()
val deserialized: Credentials = Credentials.deserialize(serialized)
```

This could be used to securely write `Credentials` to a file where it cannot be found by someone
with malicious intent (shared preferences are not recommended for that).

---

### More on Encryptor and Decryptor

`Encryptor` and `Decryptor` have a few additional useful methods. These are the classes that are
`this` in the callbacks of `authenticateForEncrypt(...) { }` and `authenticateForDecrypt(...) { }`.

```kotlin
interface Encryptor {

  /** Encrypts a [ByteArray] and returns the result. */
  fun encrypt(data: ByteArray): ByteArray

  /** Encrypts a [String] with a given [charset] and returns the result. */
  fun encrypt(
    data: String,
    charset: Charset = Charset.defaultCharset()
  ): ByteArray

  /** Encrypts a [String] and returns a Base64 representation of the result. */
  fun encryptToString(
    data: String,
    charset: Charset = Charset.defaultCharset()
  ): String
}
```

```kotlin
interface Decryptor {

  /** Encrypts a [ByteArray] and returns the result. */
  fun decrypt(data: ByteArray): ByteArray

  /** Decrypts an encrypted [ByteArray] and returns the result. */
  fun decryptToString(
    data: ByteArray,
    charset: Charset = Charset.defaultCharset()
  ): String

  /** Decrypts a Base64 [String] to an original [String]. */
  fun decryptToString(
    data: String,
    charset: Charset = Charset.defaultCharset()
  ): String
}
```

---

## Coroutines

[ ![Coroutines](https://img.shields.io/maven-central/v/com.afollestad.assure/coroutines?style=flat&label=Core) ](https://repo1.maven.org/maven2/com/afollestad/assure/coroutines)

### Gradle Dependency

```gradle
dependencies {

  implementation 'com.afollestad.assure:coroutines:0.2.0'
}
```

---

### Usage

Coroutines allow you to perform biometric authentication without the callback. These extension
functions must happen within a `suspend` function or `CoroutineScope`.

**Plain authentication:**

```kotlin
import com.afollestad.assure.coroutines.authenticate

try {
  authenticate(prompt)
  ...
} catch(e: BiometricErrorException) {
  // Handle error with `e.error` and `e.errorMessage`
}
```

**Authentication to encrypt data:** After successful authentication, the `Credentials` given will
now have a populated IV field.

```kotlin
import com.afollestad.assure.coroutines.authenticateForEncrypt

val credentials = Credentials("default")
val prompt: Prompt = // ...
val plainTextData: ByteArray = // ...

try {
  val encryptor: Encryptor = authenticateForEncrypt(credentials, prompt)
  val encryptedData: ByteArray = encryptor.encrypt(plainTextData)
  ...
} catch(e: BiometricErrorException) {
  // Handle error with `e.error` and `e.errorMessage`
}
```

**Authentication to decrypt data:** A populated IV is needed in the credentials.

```kotlin
import com.afollestad.assure.coroutines.authenticateForDecrypt

val credentials: Credentials = // ...
val prompt: Prompt = // ...
val encryptedData: ByteArray = // ...

try {
  val decryptor: Decryptor = authenticateForDecrypt(credentials, prompt)
  val decryptedData: ByteArray = decryptor.decrypt(encryptedData)
  ...
} catch(e: BiometricErrorException) {
  // Handle error with `e.error` and `e.errorMessage`
}
```

---

## RxJava

[ ![RxJava](https://img.shields.io/maven-central/v/com.afollestad.assure/rxjava?style=flat&label=RxJava) ](https://repo1.maven.org/maven2/com/afollestad/assure/rxjava)

### Gradle Dependency

```gradle
dependencies {

  implementation 'com.afollestad.assure:rxjava:0.2.0'
}
```

---

### Usage

Coroutines allow you to perform biometric authentication without the callback. These extension
functions \must happen within a `suspend` function or `CoroutineScope`.

**Plain authentication:**

```kotlin
import com.afollestad.assure.rxjava.authenticate

val disposable = authenticate(prompt)
    .doOnBiometricError { error ->
      // Handle error with `error.error` and `error.errorMessage`
    }
    .subscribe {
      // Auth was successful, do something
    }

// make sure you manage the subscription
disposable.dispose()
```

**Authentication to encrypt data:** After successful authentication, the `Credentials` given will
now have a populated IV field.

```kotlin
import com.afollestad.assure.rxjava.authenticateForEncrypt

val credentials = Credentials("default")
val prompt: Prompt = // ...
val plainTextData: ByteArray = // ...

val disposable = authenticateForEncrypt(credentials, prompt)
    .doOnBiometricError { error ->
      // Handle error with `error.error` and `error.errorMessage`
    }
    .map { encryptor ->
      encryptor.encrypt(plainTextData)
    }
    .subscribe { encryptedData ->
      // Use encryptedData
    }

// make sure you manage the subscription
disposable.dispose()
```

**Authentication to decrypt data:** A populated IV is needed in the credentials.

```kotlin
import com.afollestad.assure.rxjava.authenticateForDecrypt

val credentials: Credentials = // ...
val prompt: Prompt = // ...
val encryptedData: ByteArray = // ...

val disposable = authenticateForDecrypt(credentials, prompt)
    .doOnBiometricError { error ->
      // Handle error with `error.error` and `error.errorMessage`
    }
    .map { decryptor ->
      decryptor.decrypt(encryptedData)
    }
    .subscribe { decryptedData ->
      // Use decryptedData
    }

// make sure you manage the subscription
disposable.dispose()
```

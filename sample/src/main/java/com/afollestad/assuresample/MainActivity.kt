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
package com.afollestad.assuresample

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.afollestad.assure.Prompt
import com.afollestad.assure.coroutines.authenticateForDecrypt
import com.afollestad.assure.coroutines.authenticateForEncrypt
import com.afollestad.assure.crypto.Credentials
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks

@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : AppCompatActivity() {
  private val inputView by lazy { findViewById<TextView>(R.id.inputView) }
  private val encryptButton by lazy { findViewById<Button>(R.id.encryptButton) }
  private val decryptButton by lazy { findViewById<Button>(R.id.decryptButton) }

  private val prompt = Prompt(
      title = R.string.prompt_title,
      subtitle = R.string.prompt_subtitle,
      description = R.string.prompt_description,
      negativeButtonText = R.string.prompt_negative_text,
      confirmRequired = false,
      validityDurationSeconds = 10
  )
  private val credentials = Credentials("default")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    encryptButton.clicks()
        .debounce(BUTTON_CLICK_DEBOUNCE_MS)
        .onEach {
          val result = authenticateForEncrypt(credentials, prompt)
          inputView.text = result.encryptToString(inputView.text.toString())
        }
        .launchIn(lifecycleScope)

    decryptButton.clicks()
        .debounce(BUTTON_CLICK_DEBOUNCE_MS)
        .onEach {
          val result = authenticateForDecrypt(credentials, prompt)
          inputView.text = result.decryptToString(inputView.text.toString())
        }
        .launchIn(lifecycleScope)
  }
}

private const val BUTTON_CLICK_DEBOUNCE_MS: Long = 200L

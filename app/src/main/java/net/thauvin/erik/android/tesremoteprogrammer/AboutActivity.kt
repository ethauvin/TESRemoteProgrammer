/*
 * AboutActivity.kt
 *
 * Copyright 2016-2019 Erik C. Thauvin (erik@thauvin.net)
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
package net.thauvin.erik.android.tesremoteprogrammer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import org.jetbrains.anko.design.snackbar
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


/**
 * The <code>net.thauvin.erik.android.tesremoteprogrammer.AboutActivity</code> class.
 *
 * @author <a href="mailto:erik@thauvin.net">Erik C. Thauvin</a>
 * @created 2019-09-27
 * @since 1.0
 */
class AboutActivity : AppCompatActivity() {
    private val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(BuildConfig.TIMESTAMP),
            ZoneId.of("America/Los_Angeles"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
        )

        val aboutPage = AboutPage(this).apply {
            isRTL(false)
            setImage(R.drawable.background_splash)
            setDescription(getString(R.string.about_description))
            addItem(getVersion())
            addEmail("erik@thauvin.net")
            addGitHub("ethauvin/TESRemoteProgrammer")
            addWebsite("https://m.thauvin.net/android/TESRemoteProgrammer/licenses.shtml")
            addItem(getPrivacyPolicy())
            addTwitter("ethauvin")
            addItem(getCopyright())

            if (BuildConfig.DEBUG) {
                addItem(getCrashTest())
            }
        }.create()

        setContentView(aboutPage)
    }

    private fun getCrashTest(): Element {
        return Element().apply {
            title = "Crash Test"
            iconDrawable = android.R.drawable.ic_dialog_alert
            iconTint = R.color.about_github_color
            onClickListener = View.OnClickListener {
                throw RuntimeException("This is a crash!")
            }
        }
    }

    private fun getCopyright(): Element {
        return Element().apply {
            title = "© ${Calendar.getInstance().get(Calendar.YEAR)} Erik C. Thauvin"
            intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://m.thauvin.net/android/")
            )
        }
    }

    private fun getPrivacyPolicy(): Element {
        return Element().apply {
            title = getString(R.string.about_privacy)
            iconDrawable = R.drawable.icons8_policy_document
            iconTint = R.color.about_github_color
            value = "privacy"
            intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://m.thauvin.net/android/TESRemoteProgrammer/privacy.shtml")
            )
        }
    }

    private fun getVersion(): Element {
        val dateTimeFormatter = DateTimeFormatter.ofPattern(getString(R.string.about_built_on))
        return Element().apply {
            title = String.format(getString(R.string.about_version), BuildConfig.VERSION_NAME)
            onClickListener = View.OnClickListener {
                it.snackbar(dateTime.format(dateTimeFormatter))
            }
        }
    }
}

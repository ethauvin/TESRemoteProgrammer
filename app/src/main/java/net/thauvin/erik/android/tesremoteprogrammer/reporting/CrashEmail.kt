/*
 * CrashEmail.kt
 *
 * Copyright 2016-2018 Erik C. Thauvin (erik@thauvin.net)
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
package net.thauvin.erik.android.tesremoteprogrammer.reporting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import net.thauvin.erik.android.tesremoteprogrammer.R
import org.acra.ACRAConstants
import org.acra.ReportField
import org.acra.collections.ImmutableSet
import org.acra.collector.CrashReportData
import org.acra.config.ACRAConfiguration
import org.acra.sender.ReportSender
import org.acra.sender.ReportSenderException

class CrashEmail(private val config: ACRAConfiguration) : ReportSender {

    @Throws(ReportSenderException::class)
    override fun send(context: Context, errorContent: CrashReportData) {

        val subject = context.getString(R.string.crash_report_subject,
                context.getString(R.string.app_name))
        val body = buildBody(errorContent)

        val emailIntent = Intent(android.content.Intent.ACTION_SENDTO)
        emailIntent.data = Uri.fromParts("mailto", config.mailTo(), null)
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body)
        context.startActivity(emailIntent)
    }

    private fun buildBody(errorContent: CrashReportData): String {
        var fields: Set<ReportField> = config.reportFields
        if (fields.isEmpty()) {
            fields = ImmutableSet(*ACRAConstants.DEFAULT_MAIL_REPORT_FIELDS)
        }
        val builder = StringBuilder()
        fields.forEach { field ->
            builder.append(field.toString()).append('=')
            val value = errorContent[field]
            if (value != null) {
                builder.append(TextUtils.join("\n\t", value.flatten()))
            }
            builder.append('\n')
        }
        return builder.toString()
    }
}
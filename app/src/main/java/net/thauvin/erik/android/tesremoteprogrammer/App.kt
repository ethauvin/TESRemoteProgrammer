/*
 * App.kt
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

import android.app.Application
import android.content.Context
import net.thauvin.erik.android.tesremoteprogrammer.reporting.CrashEmailFactory
import net.thauvin.erik.android.tesremoteprogrammer.reporting.CrashReportActivity
import org.acra.ACRA
import org.acra.ReportingInteractionMode
import org.acra.annotation.ReportsCrashes

@Suppress("unused")
@ReportsCrashes(
    mailTo = "erik@thauvin.net",
    mode = ReportingInteractionMode.DIALOG,
    reportSenderFactoryClasses = [CrashEmailFactory::class],
    reportDialogClass = CrashReportActivity::class
)
open class App : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        ACRA.init(this)
    }
}

/*
 * CrashReportActivity.kt
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
package net.thauvin.erik.android.tesremoteprogrammer.reporting

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.EditText

import net.thauvin.erik.android.tesremoteprogrammer.R

import org.acra.dialog.BaseCrashReportDialog
import org.jetbrains.anko.find

class CrashReportActivity : BaseCrashReportDialog(), DialogInterface.OnDismissListener,
                            DialogInterface.OnClickListener {
    private var comment: EditText? = null

    companion object {
        private const val STATE_USER_COMMENT = "comment"
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)

        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.crash_dialog_title, getString(R.string.app_name)))
            .setView(R.layout.crash_report_dialog)
            .setPositiveButton(R.string.ok, this)
            .setNegativeButton(R.string.cancel, this)
            .create()

        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnDismissListener(this)
        dialog.show()

        comment = dialog.find(android.R.id.input)
        if (savedInstanceState != null) {
            comment!!.setText(savedInstanceState.getString(STATE_USER_COMMENT))
        }
    }

    override fun onDismiss(dialog: DialogInterface) = finish()

    override fun onClick(dialog: DialogInterface, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            sendCrash(comment!!.text.toString(), "")
        } else {
            cancelReports()
        }
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(STATE_USER_COMMENT, comment!!.text.toString())
        super.onSaveInstanceState(outState)
    }
}

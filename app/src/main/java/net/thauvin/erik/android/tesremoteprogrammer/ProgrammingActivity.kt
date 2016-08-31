/*
 * ProgrammingActivity.kt
 *
 * Copyright 2016 Erik C. Thauvin (erik@thauvin.net)
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

import android.Manifest
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity.*
import android.view.ViewManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.lb.auto_fit_textview.AutoResizeTextView
import net.thauvin.erik.android.tesremoteprogrammer.filters.AlphaFilter
import net.thauvin.erik.android.tesremoteprogrammer.filters.MinMaxFilter
import net.thauvin.erik.android.tesremoteprogrammer.filters.NumberFilter
import net.thauvin.erik.android.tesremoteprogrammer.models.Option
import net.thauvin.erik.android.tesremoteprogrammer.models.Params
import net.thauvin.erik.android.tesremoteprogrammer.util.Dtmf
import net.thauvin.erik.android.tesremoteprogrammer.widget.ScrollAwareFABBehavior
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.support.v4.nestedScrollView
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.util.*

@RuntimePermissions
class ProgrammingActivity : AppCompatActivity(), AnkoLogger {

    inline fun ViewManager.autofitTextView(theme: Int = 0, init: AutoResizeTextView.() -> Unit) = ankoView({ AutoResizeTextView(it) }, theme, init)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val params: Params = intent.extras.getParcelable("net.thauvin.erik.android.tesremoteprogrammer.models.Params")
        val option: Option = intent.extras.getParcelable("net.thauvin.erik.android.tesremoteprogrammer.models.Option")
        val fields = arrayListOf<EditText>()

        coordinatorLayout {

            autofitTextView {
                padding = dip(20)
                text = option.title
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
                singleLine = true
                maxLines = 1
                typeface = Typeface.DEFAULT_BOLD
                isFocusableInTouchMode = true
                ellipsize = TextUtils.TruncateAt.END
            }.lparams(width = matchParent, height = matchParent)

            nestedScrollView {
                topPadding = dip(50)
                bottomPadding = dip(30)
                horizontalPadding = dip(20)

                verticalLayout {
                    lparams(width = matchParent, height = wrapContent)

                    val it = option.fields.iterator()
                    while (it.hasNext()) {
                        val field = it.next()

                        textInputLayout {
                            horizontalPadding = dip(40)
                            lparams(width = matchParent)

                            val inputFilters: ArrayList<InputFilter> = ArrayList()

                            val editText = editText() {
                                hint = field.hint

                                if (field.alpha.isNotBlank()) {
                                    if (field.alpha.equals(Dtmf.DKS, true)) {
                                        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                                        inputFilters.add(AlphaFilter(Dtmf.DKS_EXTRAS))
                                    } else if (field.alpha.equals(Dtmf.LINEAR, true)) {
                                        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                                        inputFilters.add(AlphaFilter(Dtmf.LINEAR_EXTRAS))
                                    }
                                } else {
                                    inputType = InputType.TYPE_CLASS_PHONE
                                    inputFilters.add(NumberFilter(field.digits, if (field.alt) params.alt else ""))
                                    if (field.max != -1 && field.min != -1) {
                                        inputFilters.add(
                                                MinMaxFilter(field.min, field.max, field.size, field.zeros))
                                    }
                                }

                                if (field.size != -1) {
                                    inputFilters.add(InputFilter.LengthFilter(field.size))
                                }

                                if (inputFilters.isNotEmpty()) {
                                    filters = inputFilters.toTypedArray()
                                }

                                if (!it.hasNext()) {
                                    imeOptions = EditorInfo.IME_ACTION_DONE
                                    setOnEditorActionListener { v, id, event ->
                                        if (id == EditorInfo.IME_ACTION_DONE) {
                                            clearFocus()
                                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                            imm.hideSoftInputFromWindow(windowToken, 0)
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                }
                            }
                            fields.add(editText)
                        }
                    }
                }
            }.lparams(width = matchParent, height = matchParent)

            if (!option.nosteps) {
                floatingActionButton {
                    imageResource = R.drawable.ic_menu_dialpad_lt

                    if (!option.nodial) {
                        backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                    }

                    onClick {
                        if (validateFields(fields, option)) {
                            val dtmf = Dtmf.build(params.master, params.ack, option, fields)
                            if (Dtmf.validate(dtmf, "${MainActivity.PAUSE}${params.ack}${params.alt}")) {
                                val begin = if (params.begin.isNotBlank()) {
                                    "${params.begin}${MainActivity.PAUSE}"
                                } else {
                                    ""
                                }

                                val end = if (params.end.isNotBlank()) {
                                    "${MainActivity.PAUSE}${params.end}"
                                } else {
                                    ""
                                }

                                startActivity<StepsActivity>(
                                        StepsActivity.EXTRA_STEPS to "$begin$dtmf$end".split(','))
                            } else {
                                Snackbar.make(this@coordinatorLayout, getString(R.string.error_invalid_dtmf, dtmf),
                                        Snackbar.LENGTH_LONG).show()
                            }
                        } else {
                            Snackbar.make(this@coordinatorLayout, R.string.error_invalid_field,
                                    Snackbar.LENGTH_LONG).show()
                        }
                    }
                }.lparams(width = wrapContent, height = wrapContent) {
                    if (option.nodial) {
                        gravity = BOTTOM or END
                        rightMargin = dip(16)
                    } else {
                        gravity = BOTTOM or START
                        leftMargin = dip(16)
                    }

                    bottomMargin = dip(16)
                    elevation = dip(6).toFloat()
                    behavior = ScrollAwareFABBehavior()
                }
            }

            if (!option.nodial) {
                floatingActionButton {
                    imageResource = R.drawable.fab_ic_call
                    onClick {
                        if (validateFields(fields, option)) {
                            val dtmf = Dtmf.build(params.master, params.ack, option, fields)
                            if (Dtmf.validate(dtmf, "${MainActivity.PAUSE}${params.ack}${params.alt}")) {
                                ProgrammingActivityPermissionsDispatcher.callWithCheck(
                                        this@ProgrammingActivity, params.phone, dtmf)
                            } else {
                                Snackbar.make(this@coordinatorLayout, getString(R.string.error_invalid_dtmf, dtmf),
                                        Snackbar.LENGTH_LONG).show()
                            }
                        } else {
                            Snackbar.make(this@coordinatorLayout, R.string.error_invalid_field,
                                    Snackbar.LENGTH_LONG).show()
                        }
                    }
                }.lparams(width = wrapContent, height = wrapContent) {
                    gravity = BOTTOM or END
                    bottomMargin = dip(16)
                    rightMargin = dip(16)
                    elevation = dip(6).toFloat()
                    behavior = ScrollAwareFABBehavior()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ProgrammingActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults)
    }

    @NeedsPermission(Manifest.permission.CALL_PHONE)
    fun call(phone: String, dtmf: String) {
        if (BuildConfig.DEBUG) {
            info(">>> $phone${MainActivity.PAUSE}${MainActivity.PAUSE}$dtmf")
        }
        makeCall("$phone${MainActivity.PAUSE}${MainActivity.PAUSE}$dtmf")
    }

    fun validateFields(fields: ArrayList<EditText>, option: Option): Boolean {
        var isValid = true

        fields.forEachIndexed { i, v ->
            if (v.text.isNullOrBlank()) {
                v.error = getString(R.string.error_required)
                isValid = false
            } else {
                val size = if (option.fields[i].minSize > 0) {
                    option.fields[i].minSize
                } else {
                    option.fields[i].size
                }
                if (option.fields[i].alpha.isBlank() &&
                        !validateSize(v.length(), option.fields[i].minSize, option.fields[i].size)) {
                    v.error = getString(R.string.error_invalid_size, size,
                            resources.getQuantityString(R.plurals.error_digit, size))
                    isValid = false
                }

                if (option.fields[i].min > 0 && option.fields[i].max > 0) {
                    try {
                        if (v.text.toString().toInt() !in IntRange(option.fields[i].min, option.fields[i].max)) {
                            v.error = getString(R.string.error_invalid)
                            isValid = false
                        }
                    } catch (nfe: NumberFormatException) {
                        v.error = getString(R.string.error_invalid)
                        isValid = false
                    }
                }
            }
        }

        return isValid
    }

    fun validateSize(size: Int, min: Int, max: Int): Boolean {
        if (min > 0) {
            return size in IntRange(min, max)
        } else {
            return size == max
        }
    }
}

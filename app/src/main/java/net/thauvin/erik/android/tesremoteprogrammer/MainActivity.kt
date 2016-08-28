/*
 * MainActivity.kt
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
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import net.thauvin.erik.android.tesremoteprogrammer.models.Config
import net.thauvin.erik.android.tesremoteprogrammer.models.Configurations
import net.thauvin.erik.android.tesremoteprogrammer.util.Dtmf
import org.jetbrains.anko.*
import org.jetbrains.anko.design.textInputLayout
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*

@RuntimePermissions
class MainActivity : AppCompatActivity(), AnkoLogger {
    lateinit var config: Config
    val configurations_data = "configurations.dat"
    val current_config_data = "config.dat"
    val defaultConfigs = listOf(
            R.raw.dks_1802,
            R.raw.dks_1802_epd,
            R.raw.dks_1812,
            R.raw.dks_1819,
            R.raw.dks_1803_1808_1810)
    val read_request_code = 42

    companion object {
        val PAUSE = ','
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun importConfig(intent: Intent) {
        val errors = StringBuilder()

        val tmp: Config? = try {
            Gson().fromJson(InputStreamReader(contentResolver.openInputStream(intent.data)),
                    Config::class.java)
        } catch (jse: JsonSyntaxException) {
            val cause = jse.cause
            if (cause != null) {
                errors.append(cause.message)
            } else {
                errors.append(jse.message)
            }
            null
        }

        if (tmp != null && validateConfig(tmp, errors)) {
            config = tmp
            saveConfig()
            recreate()
        }

        if (errors.length > 0) {
            alert {
                title(R.string.alert_config_error)
                message(Html.fromHtml("$errors"))
                cancelButton { }
            }.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == read_request_code && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                MainActivityPermissionsDispatcher.importConfigWithCheck(this, data)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fields = arrayListOf<EditText>()

        loadConfig()

        verticalLayout {
            padding = dip(20)

            textView {
                text = config.params.name.toUpperCase()
                bottomPadding = dip(5)
                typeface = Typeface.DEFAULT_BOLD
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24f)
                isFocusableInTouchMode = true
                singleLine = true
                ellipsize = TextUtils.TruncateAt.END
            }

            textInputLayout {
                horizontalPadding = dip(40)
                val phone = editText() {
                    lparams(width = matchParent)
                    inputType = InputType.TYPE_CLASS_PHONE
                    hint = getString(R.string.hint_phone_number)

                    if (config.params.phone.isNotBlank()) {
                        setText(config.params.phone)
                    }

                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_call_black_24dp, 0)
                    setOnFocusChangeListener { view, hasFocus ->
                        if (!hasFocus) {
                            config.params.phone = (view as EditText).text.toString()
                            saveConfig()
                        }
                    }
                }
                fields.add(phone)
            }

            textInputLayout {
                horizontalPadding = dip(40)
                val masterCode = editText() {
                    lparams(width = matchParent)
                    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                    hint = getString(R.string.hint_master_code)
                    filters = arrayOf(InputFilter.LengthFilter(config.params.size))
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_black_24dp, 0)
                    imeOptions = EditorInfo.IME_ACTION_DONE

                    if (config.params.master.isNotBlank()) {
                        setText(config.params.master)
                    }

                    setOnFocusChangeListener { view, hasFocus ->
                        if (!hasFocus) {
                            config.params.master = (view as EditText).text.toString()
                            saveConfig()
                        }
                    }

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
                fields.add(masterCode)
            }

            textView {
                topPadding = dip(10)
                text = getString(R.string.programming_heading)
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
                typeface = Typeface.DEFAULT_BOLD
            }.lparams(width = matchParent)

            val opts = config.opts.sorted()
            val titles = arrayListOf<String>()
            opts.all {
                titles.add(it.title)
            }

            listView {
                adapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_list_item_1, titles)
                isTextFilterEnabled = true
                isScrollbarFadingEnabled = false
                onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
                    if (validateFields(fields, config.params.size)) {
                        startActivity<ProgrammingActivity>(
                                "net.thauvin.erik.android.tesremoteprogrammer.models.Params" to config.params,
                                "net.thauvin.erik.android.tesremoteprogrammer.models.Option" to opts[position])
                    }
                }
            }.lparams(width = matchParent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_about -> {
                val alert = alert {
                    title(R.string.app_name)
                    message(Html.fromHtml(
                            getString(R.string.about_message, BuildConfig.VERSION_NAME)))
                    icon(R.mipmap.ic_launcher)
                    okButton {}
                }.show()
                (alert.dialog?.findViewById(android.R.id.message) as TextView)
                        .movementMethod = LinkMovementMethod.getInstance()
            }
            R.id.action_config -> {
                val configs = loadConfigurations().configs.toSortedMap()
                val keys = configs.keys
                val checked = keys.indexOf(config.params.name)
                val alert = AlertDialog.Builder(this)

                alert.setTitle(R.string.action_config)

                alert.setSingleChoiceItems(keys.toTypedArray(), checked,
                        { dialogInterface, i ->
                            if (i != checked) {
                                config = configs.getOrElse(keys.elementAt(i), defaultValue = { config })
                                saveConfig()
                                this.recreate()
                            }
                            dialogInterface.dismiss()
                        })

                alert.setNegativeButton(android.R.string.cancel,
                        { dialogInterface, i ->
                            dialogInterface.dismiss()
                        })

                alert.setNeutralButton(R.string.dialog_import,
                        { dialogInterface, i ->
                            dialogInterface.dismiss()
                            val intent = Intent(Intent.ACTION_GET_CONTENT)
                            intent.addCategory(Intent.CATEGORY_OPENABLE)
                            intent.type = "application/json"
                            startActivityForResult(intent, read_request_code)
                        })


                alert.show()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults)
    }

    fun loadConfigurations(): Configurations {
        try {
            ObjectInputStream(openFileInput(configurations_data)).use {
                return it.readObject() as Configurations
            }
        } catch (ex: FileNotFoundException) {
            return Configurations()
        }
    }

    fun loadConfig() {
        try {
            ObjectInputStream(openFileInput(current_config_data)).use {
                config = it.readObject() as Config
            }
        } catch (ex: FileNotFoundException) {
            val confs = Configurations()

            defaultConfigs.forEach {
                config = Gson().fromJson(InputStreamReader(resources.openRawResource(it)),
                        Config::class.java)

                if (BuildConfig.DEBUG) {
                    val errors = StringBuilder()
                    if (!validateConfig(config, errors)) {
                        info(">>> ${config.params.name}: " + Html.fromHtml(errors.toString()))
                    }
                }

                confs.configs.put(config.params.name, config)
            }

            saveConfigurations(confs)
            saveConfig(false)
        }
    }

    fun saveConfigurations(confs: Configurations) {
        ObjectOutputStream(openFileOutput(configurations_data, Context.MODE_PRIVATE)).use {
            it.writeObject(confs)
        }
    }

    fun saveConfig(backup: Boolean = true) {
        if (backup) {
            val confs = loadConfigurations()
            confs.configs.put(config.params.name, config)
            saveConfigurations(confs)
        }

        ObjectOutputStream(openFileOutput(current_config_data, Context.MODE_PRIVATE)).use {
            it.writeObject(config)
        }
    }

    fun validateConfig(config: Config, errors: StringBuilder): Boolean {
        val len = errors.length

        with(config) {
            if (params.name.isBlank()) {
                errors.append(getString(R.string.validate_missing_param, "name"))
            }

            if (params.size < 1) {
                errors.append(getString(R.string.validate_invalid_param, "size"))
            }

            if (params.star.isBlank()) {
                errors.append(getString(R.string.validate_missing_param, "star"))
            }

            if (opts.size == 0) {
                errors.append(getString(R.string.validate_missing_opts))
            }

            opts.forEachIndexed { i, option ->
                if (option.fields.size == 0) {
                    errors.append(getString(R.string.validate_missing_fields, i + 1))
                }

                if (option.nosteps && option.nodial) {
                    errors.append(getString(R.string.validate_invalid_option, i + 1, "nodial/nosteps"))
                }

                if (option.dtmf.isBlank()) {
                    errors.append(getString(R.string.validate_invalid_dtmf, i + 1, "''"))
                }

                option.fields.forEachIndexed { j, field ->
                    if (field.size <= 0) {
                        errors.append(getString(R.string.validate_invalid_attr, i + 1, j + 1, "size"))
                    }

                    if (!field.alpha) {
                        if (field.min >= 0 || field.max >= 0) {
                            if (field.max < 1) {
                                errors.append(getString(R.string.validate_invalid_attr, i + 1, j + 1, "max"))
                            } else if (field.min < 0) {
                                errors.append(getString(R.string.validate_invalid_attr, i + 1, j + 1, "min"))
                            } else if (field.min > field.max) {
                                errors.append(getString(R.string.validate_invalid_attr, i + 1, j + 1, "max/min"))
                            }
                        }
                    }

                    if (!option.dtmf.contains(Dtmf.DTMF_FIELD.format(j + 1))) {
                        errors.append(getString(R.string.validate_unused_field, i + 1, j + 1))
                    }
                }

                val blank = "\\0"
                val dtmf = Dtmf.mock(option, blank)
                if (!Dtmf.validate(dtmf, "${MainActivity.Companion.PAUSE}${params.star}${params.hash}$blank")) {
                    errors.append(getString(R.string.validate_invalid_dtmf, i + 1, dtmf.replace(blank, "&#10003;")))
                }
            }
        }

        return errors.length == len
    }

    fun validateFields(fields: ArrayList<EditText>, size: Int): Boolean {
        var isValid = true

        fields.forEach {
            if (it.text.isNullOrBlank()) {
                it.error = getString(R.string.error_required)
                isValid = false
            }
        }
        if (size > 0 && (fields[1].text.length != size)) {
            isValid = false
            fields[1].error = getString(R.string.error_invalid_size, size,
                    resources.getQuantityString(R.plurals.error_digit, size))
        }

        return isValid
    }
}

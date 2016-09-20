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
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.*
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
import net.thauvin.erik.android.tesremoteprogrammer.util.isDKS
import net.thauvin.erik.android.tesremoteprogrammer.util.isDigits
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
    val configurationsData = "configurations.dat"
    val currentConfigData = "config.dat"
    val defaultConfigurations = listOf(
            R.raw.dks_1802,
            R.raw.dks_1802_epd,
            R.raw.dks_1812,
            R.raw.dks_1819,
            R.raw.linear_ae_100,
            R.raw.linear_ae_500,
            R.raw.dks_1803_1808_1810)
    val readRequestCode = 42

    companion object {
        val PAUSE = ','
    }

    fun fromHtml(s: String) : Spanned {
        if (Build.VERSION.SDK_INT >= 24) {
            return Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY)
        } else {
            return Html.fromHtml(s)
        }
    }

    fun initConfigurations() {
        try {
            ObjectInputStream(openFileInput(currentConfigData)).use {
                config = it.readObject() as Config
            }
        } catch (ex: FileNotFoundException) {
            val confs = Configurations()

            defaultConfigurations.forEach {
                config = Gson().fromJson(InputStreamReader(resources.openRawResource(it)),
                        Config::class.java)

                if (BuildConfig.DEBUG) {
                    val errors = StringBuilder()
                    if (validateConfig(config, errors)) {
                        info(">>> ${config.params.name}: successfully loaded")
                    } else {
                        info(">>> ${config.params.name}: " + fromHtml(errors.toString()))
                    }
                }

                confs.configs.put(config.params.name, config)
            }

            saveConfigurations(confs)
            saveConfig(false)
        }
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
                message(fromHtml("$errors"))
                cancelButton { }
            }.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == readRequestCode && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                MainActivityPermissionsDispatcher.importConfigWithCheck(this, data)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // editText, size
        val fields = arrayListOf<Pair<EditText, Int>>()

        initConfigurations()

        with(config.params) {
            verticalLayout {
                padding = dip(20)

                // config name
                textView {
                    text = name.toUpperCase()
                    bottomPadding = dip(5)
                    typeface = Typeface.DEFAULT_BOLD
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24f)
                    isFocusableInTouchMode = true
                    singleLine = true
                    ellipsize = TextUtils.TruncateAt.END
                }

                // phone
                textInputLayout {
                    horizontalPadding = dip(40)
                    val editText = editText() {
                        lparams(width = matchParent)
                        inputType = InputType.TYPE_CLASS_PHONE
                        hint = getString(R.string.hint_phone_number)

                        if (phone.isNotBlank()) {
                            setText(phone)
                        }

                        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_call_black_24dp, 0)
                        setOnFocusChangeListener { view, hasFocus ->
                            if (!hasFocus) {
                                phone = (view as EditText).text.toString()
                                saveConfig()
                            }
                        }
                    }
                    fields.add(Pair(editText, 0))
                }

                // master code
                textInputLayout {
                    horizontalPadding = dip(40)
                    val editText = editText() {
                        lparams(width = matchParent)
                        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                        hint = getString(R.string.hint_master_code)
                        filters = arrayOf(InputFilter.LengthFilter(size))
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_black_24dp, 0)
                        imeOptions = EditorInfo.IME_ACTION_DONE

                        if (master.isNotBlank()) {
                            setText(master)
                        }

                        setOnFocusChangeListener { view, hasFocus ->
                            if (!hasFocus) {
                                master = (view as EditText).text.toString()
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
                    fields.add(Pair(editText, size))
                }

                // programming title
                textView {
                    topPadding = dip(10)
                    text = getString(R.string.programming_heading)
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
                    typeface = Typeface.DEFAULT_BOLD
                }.lparams(width = matchParent)

                // options list
                listView {
                    val opts = config.opts.sorted()
                    val titles = arrayListOf<String>()
                    opts.all {
                        titles.add(it.title)
                    }

                    adapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_list_item_1, titles)
                    isTextFilterEnabled = true
                    isScrollbarFadingEnabled = false
                    onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
                        if (validateFields(fields)) {
                            saveConfig()
                            startActivity<ProgrammingActivity>(
                                    "net.thauvin.erik.android.tesremoteprogrammer.models.Params" to config.params,
                                    "net.thauvin.erik.android.tesremoteprogrammer.models.Option" to opts[position])
                        }
                    }
                }.lparams(width = matchParent)
            }
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
                    message(fromHtml(getString(R.string.about_message, BuildConfig.VERSION_NAME)))
                    icon(R.mipmap.ic_launcher)
                    okButton {}
                }.show()

                (alert.dialog?.findViewById(android.R.id.message) as TextView)
                        .movementMethod = LinkMovementMethod.getInstance()
            }
            R.id.action_confs -> {
                val confs = loadConfigurations().configs.toSortedMap()
                val keys = confs.keys
                val checked = keys.indexOf(config.params.name)
                val alert = AlertDialog.Builder(this)

                alert.setTitle(R.string.action_config)

                alert.setSingleChoiceItems(keys.toTypedArray(), checked,
                        { dialogInterface, i ->
                            if (i != checked) {
                                config = confs.getOrElse(keys.elementAt(i), defaultValue = { config })
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
                            startActivityForResult(intent, readRequestCode)
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
            ObjectInputStream(openFileInput(configurationsData)).use {
                return it.readObject() as Configurations
            }
        } catch (ex: FileNotFoundException) {
            return Configurations()
        }
    }

    fun saveConfigurations(confs: Configurations) {
        ObjectOutputStream(openFileOutput(configurationsData, Context.MODE_PRIVATE)).use {
            it.writeObject(confs)
        }
    }

    fun saveConfig(backup: Boolean = true) {
        if (backup) {
            val confs = loadConfigurations()
            confs.configs.put(config.params.name, config)
            saveConfigurations(confs)
        }

        ObjectOutputStream(openFileOutput(currentConfigData, Context.MODE_PRIVATE)).use {
            it.writeObject(config)
        }
    }

    fun validateConfig(config: Config, errors: StringBuilder): Boolean {
        val len = errors.length

        with(config) {
            // params
            with(params) {
                // name
                if (name.isBlank()) {
                    errors.append(getString(R.string.validate_missing_param, "name"))
                }

                // type
                if (type.isBlank()) {
                    errors.append(getString(R.string.validate_missing_param, "type"))
                } else if (!Dtmf.isValidType(type)) {
                    errors.append(getString(R.string.validate_invalid_param, "type"))

                }

                // size
                if (size < 1) {
                    errors.append(getString(R.string.validate_invalid_param, "size"))
                }

                // ack
                if (ack.isBlank()) {
                    errors.append(getString(R.string.validate_missing_param, "ack"))
                }
            }

            // options
            if (opts.size == 0) {
                errors.append(getString(R.string.validate_missing_opts))
            } else {
                opts.forEachIndexed { i, option ->
                    // gson will create a null object on trailing comma
                    // see: https://github.com/google/gson/issues/494
                    if (option == null) {
                        errors.append(getString(R.string.validate_syntax_error, "opts[]"))
                    } else {
                        with(option) {
                            // title
                            if (title.isBlank()) {
                                errors.append(getString(
                                        R.string.validate_missing_opts_prop,
                                        i + 1,
                                        "title"))
                            }

                            // nosteps/nodial
                            if (nosteps && nodial) {
                                errors.append(getString(
                                        R.string.validate_invalid_option,
                                        i + 1,
                                        "nodial/nosteps"))
                            }

                            // dtmf
                            if (dtmf.isBlank()) {
                                errors.append(getString(
                                        R.string.validate_missing_opts_prop,
                                        i + 1,
                                        "dtmf"))
                            } else if (fields.size == 0) { // fields missing
                                errors.append(getString(
                                        R.string.validate_missing_opts_prop,
                                        i + 1,
                                        "fields"))
                            } else {
                                val blank = "\\0"
                                val mock = Dtmf.mock(option, blank)

                                if (!mock.contains(MainActivity.PAUSE)) { // no pause
                                    errors.append(getString(
                                            R.string.validate_invalid_opts_prop,
                                            i + 1,
                                            "dtmf",
                                            getString(R.string.validate_dtmf_nopause)))
                                }

                                if (!Dtmf.validate(mock,
                                        "${MainActivity.PAUSE}${params.ack}${params.alt}$blank")) {
                                    errors.append(getString(
                                            R.string.validate_invalid_opts_prop,
                                            i + 1,
                                            "dtmf",
                                            mock.replace(blank, "&#10003;")))
                                }
                            }

                            // fields
                            fields.forEachIndexed { j, field ->
                                if (field == null) {
                                    errors.append(getString(
                                            R.string.validate_syntax_error,
                                            "opts[${i+1}], field[$j]"))
                                } else {
                                    with(field) {
                                        // size
                                        if (size <= 0) {
                                            errors.append(getString(
                                                    R.string.validate_invalid_field_prop,
                                                    i + 1,
                                                    j + 1,
                                                    resources.getQuantityString(R.plurals.error_prop, 1),
                                                    "size=$size"))
                                        }

                                        // digits
                                        if (digits.isNotBlank() && !digits.isDigits()) {
                                            errors.append(getString(
                                                    R.string.validate_invalid_field_prop,
                                                    i + 1,
                                                    j + 1,
                                                    resources.getQuantityString(R.plurals.error_prop, 1),
                                                    "digits='$digits'"))
                                        }

                                        // minSize
                                        if (minSize >= 0 && minSize > size) {
                                            errors.append(getString(
                                                    R.string.validate_invalid_field_prop,
                                                    i + 1,
                                                    j + 1,
                                                    resources.getQuantityString(R.plurals.error_prop, 2),
                                                    "minSize=$minSize > size=$size"))
                                        }

                                        // numeric fields only
                                        if (!alpha) {
                                            if (minSize == 0) {
                                                errors.append(getString(
                                                        R.string.validate_invalid_field_prop,
                                                        i + 1,
                                                        j + 1,
                                                        resources.getQuantityString(R.plurals.error_prop, 1),
                                                        "minSize=$minSize"))
                                            }

                                            // min/max
                                            if (min >= 0 || max >= 0) {
                                                if (max < 1) {
                                                    errors.append(getString(
                                                            R.string.validate_invalid_field_prop,
                                                            i + 1,
                                                            j + 1,
                                                            resources.getQuantityString(R.plurals.error_prop, 1),
                                                            "max=$max"))
                                                }

                                                if (min < 0) {
                                                    errors.append(getString(
                                                            R.string.validate_invalid_field_prop,
                                                            i + 1,
                                                            j + 1,
                                                            resources.getQuantityString(R.plurals.error_prop, 1),
                                                            "min=$min"))
                                                }

                                                if (min > max) {
                                                    errors.append(getString(
                                                            R.string.validate_invalid_field_prop,
                                                            i + 1,
                                                            j + 1,
                                                            resources.getQuantityString(R.plurals.error_prop, 2),
                                                            "min=$min > max=$max"))
                                                }
                                            }

                                            // no leading zeros
                                            if (!params.type.isDKS() && !zeros) {
                                                // minSize/min
                                                if (min >= 0 && minSize > 0) {
                                                    if (min.toString().length != minSize) {
                                                        errors.append(getString(
                                                                R.string.validate_invalid_field_prop,
                                                                i + 1,
                                                                j + 1,
                                                                resources.getQuantityString(R.plurals.error_prop, 2),
                                                                "minSize=$minSize/min=$min"))
                                                    }
                                                }

                                                // size/max
                                                if (size > 0 && max > 0) {
                                                    if (max.toString().length != size) {
                                                        errors.append(getString(
                                                                R.string.validate_invalid_field_prop,
                                                                i + 1,
                                                                j + 1,
                                                                resources.getQuantityString(R.plurals.error_prop, 2),
                                                                "size=$size/max=$max"))
                                                    }
                                                }
                                            }
                                        }

                                        // unused fields
                                        if (!dtmf.contains(Dtmf.DTMF_FIELD.format(j + 1))) {
                                            errors.append(getString(
                                                    R.string.validate_unused_field,
                                                    i + 1,
                                                    j + 1))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return errors.length == len
    }

    fun validateFields(fields: ArrayList<Pair<EditText, Int>>): Boolean {
        var isValid = true

        fields.forEach {
            with(it) {
                if (first.text.isNullOrBlank()) {
                    first.error = getString(R.string.error_required)
                    isValid = false
                } else if (second > 0 && first.text.length != second) {
                    first.error = getString(R.string.error_invalid_size, second,
                            resources.getQuantityString(R.plurals.error_digit, second), "")
                    isValid = false
                }
            }
        }

        return isValid
    }
}

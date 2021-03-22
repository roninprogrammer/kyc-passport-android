package io.vicknesh.passportkyc.ui.validators

import android.content.Context
import androidx.appcompat.widget.AppCompatEditText
import android.widget.EditText

import com.mobsandgeeks.saripaar.QuickRule
import io.vicknesh.passportkyc.R

import java.util.regex.Matcher
import java.util.regex.Pattern


class DocumentNumberRule : QuickRule<AppCompatEditText>() {

    override fun isValid(editText: AppCompatEditText): Boolean {
        val text = editText.text!!.toString().trim { it <= ' ' }
        val patternDate = Pattern.compile(REGEX)
        val matcherDate = patternDate.matcher(text)
        return matcherDate.find()
    }

    override fun getMessage(context: Context): String {
        return context.getString(R.string.error_validation_document_number)
    }

    companion object {

        private val REGEX = "[A-Z0-9<]{9}$"
    }
}
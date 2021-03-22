package io.vicknesh.passportkyc.ui.validators

import android.content.Context
import androidx.appcompat.widget.AppCompatEditText
import com.mobsandgeeks.saripaar.QuickRule
import io.vicknesh.passportkyc.R
import java.util.regex.Pattern


class DateRule : QuickRule<AppCompatEditText>() {

    override fun isValid(editText: AppCompatEditText): Boolean {
        val text = editText.text!!.toString().trim { it <= ' ' }
        val patternDate = Pattern.compile(REGEX)
        val matcherDate = patternDate.matcher(text)
        return matcherDate.find()
    }

    override fun getMessage(context: Context): String {
        return context.getString(R.string.error_validation_date)
    }

    companion object {

        private val REGEX = "[0-9]{6}$"
    }
}
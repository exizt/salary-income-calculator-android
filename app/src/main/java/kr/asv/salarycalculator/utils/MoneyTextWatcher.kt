package kr.asv.salarycalculator.utils

import android.widget.EditText
import android.text.TextWatcher
import android.text.Editable
import java.lang.Exception
import java.lang.NumberFormatException
import java.text.DecimalFormat

/**
 * MoneyTextEditor 확장하는 클래스
 * Created by EXIZT on 2016-05-03.
 */
class MoneyTextWatcher(private val editText: EditText) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        //입력하기 전에
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        //입력되는 텍스트에 변화가 있을 때
    }

    override fun afterTextChanged(s: Editable) {

        //입력이 끝났을 때
        editText.removeTextChangedListener(this)
        try {
            var givenString = s.toString()
            val longVal: Long
            if (givenString.contains(",")) {
                givenString = givenString.replace(",".toRegex(), "")
            }
            longVal = givenString.toLong()
            val formatter = DecimalFormat("#,###,###")
            val formattedString = formatter.format(longVal)
            editText.setText(formattedString)
            editText.setSelection(editText.text.length)
            // to place the cursor at the end of text
        } catch (nfe: NumberFormatException) {
            editText.setText("0")
            nfe.printStackTrace()
        } catch (e: Exception) {
            editText.setText("0")
            e.printStackTrace()
        }
        editText.addTextChangedListener(this)
    }

    companion object {
        /**
         * long 타입으로 반환하는 메서드
         *
         * @param editText EditText
         * @return long
         */
        fun getValue(editText: EditText): Long {
            return try {
                var text = editText.text.toString()
                if (text.contains(",")) {
                    text = text.replace(",".toRegex(), "")
                }
                //double result =  Double.parseDouble();
                text.toLong()
            } catch (e: Exception) {
                0
            }
        }
    }
}
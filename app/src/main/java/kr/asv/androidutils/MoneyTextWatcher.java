package kr.asv.androidutils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;

/**
 * MoneyTextEditor 확장하는 클래스
 * Created by EXIZT on 2016-05-03.
 */
public class MoneyTextWatcher implements TextWatcher {
    private final EditText editText;

    @SuppressWarnings("unused")
    public MoneyTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //입력하기 전에
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //입력되는 텍스트에 변화가 있을 때
    }

    @Override
    public void afterTextChanged(Editable s) {

        //입력이 끝났을 때
        editText.removeTextChangedListener(this);

        try {
            String given_string = s.toString();
            long long_val;
            if (given_string.contains(",")) {
                given_string = given_string.replaceAll(",", "");
            }
            long_val = Long.parseLong(given_string);
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            String formattedString = formatter.format(long_val);
            editText.setText(formattedString);
            editText.setSelection(editText.getText().length());
            // to place the cursor at the end of text
        } catch (NumberFormatException nfe) {
            editText.setText("1");
            nfe.printStackTrace();
        } catch (Exception e) {
            editText.setText("1");
            e.printStackTrace();
        }
        editText.addTextChangedListener(this);
    }

    /**
     * long 타입으로 반환하는 메서드
     *
     * @param editText EditText
     * @return long
     */
    @SuppressWarnings("UnusedReturnValue")
    public static long getValue(EditText editText) {
        try {
            String text = editText.getText().toString();

            if (text.contains(",")) {
                text = text.replaceAll(",", "");
            }
            //double result =  Double.parseDouble();
            return Long.parseLong(text);
        } catch (Exception e) {
            return 0;
        }
    }
}

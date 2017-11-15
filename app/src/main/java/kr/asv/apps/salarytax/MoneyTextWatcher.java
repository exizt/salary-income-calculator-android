package kr.asv.apps.salarytax;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;

/**
 * MoneyTextEditor 확장하는 클래스
 * Created by EXIZT on 2016-05-03.
 */
public class MoneyTextWatcher implements TextWatcher {
    public EditText editText;
    public MoneyTextWatcher(EditText editText){
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
            String givenstring = s.toString();
            Long longval;
            if (givenstring.contains(",")) {
                givenstring = givenstring.replaceAll(",", "");
            }
            longval = Long.parseLong(givenstring);
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            String formattedString = formatter.format(longval);
            editText.setText(formattedString);
            editText.setSelection(editText.getText().length());
            // to place the cursor at the end of text
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        editText.addTextChangedListener(this);
    }

    /**
     * long 타입으로 반환하는 메서드
      * @param editText EditText
     * @return long
     */
    public static long getValue(EditText editText)
    {
        try{
            String text = editText.getText().toString();

            if(text.contains(",")){
                text = text.replaceAll(",","");
            }
            //double result =  Double.parseDouble();
            return Long.parseLong(text);
        } catch(Exception e){
            return 0;
        }
    }
}

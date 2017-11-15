package kr.asv.apps.salarytax.fragments;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import kr.asv.apps.salarytax.MoneyTextWatcher;
import kr.asv.shhtaxmanager.MainActivity;
import kr.asv.shhtaxmanager.R;
import kr.asv.apps.salarytax.Services;

/**
  */
public class TaxCalculatorFragment extends BaseFragment {
    public TaxCalculatorFragment() {
        // Required empty public constructor
    }

    /**
     */
    @SuppressWarnings("unused")
    public static TaxCalculatorFragment newInstance() {
        TaxCalculatorFragment fragment = new TaxCalculatorFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tax_calculator, container, false);
        setFragmentView(view);
        ((EditText) findViewById(R.id.id_input_salary)).setFilters(new InputFilter[]{new InputFilterMinMax(0,1000000000)});
        ((EditText) findViewById(R.id.id_input_taxfree)).setFilters(new InputFilter[]{new InputFilterMinMax(0,100000000)});
        ((EditText) findViewById(R.id.id_input_national)).setFilters(new InputFilter[]{new InputFilterMinMax(0,100000000)});
        ((EditText) findViewById(R.id.id_input_healthcare)).setFilters(new InputFilter[]{new InputFilterMinMax(0,100000000)});
        ((EditText) findViewById(R.id.id_input_longtermcare)).setFilters(new InputFilter[]{new InputFilterMinMax(0,100000000)});
        ((EditText) findViewById(R.id.id_input_employmentcare)).setFilters(new InputFilter[]{new InputFilterMinMax(0,100000000)});
        setActionBarTitle("세율 계산");

        ((EditText) findViewById(R.id.id_input_salary)).addTextChangedListener(new MoneyTextWatcher(((EditText) findViewById(R.id.id_input_salary))));
        ((EditText) findViewById(R.id.id_input_taxfree)).addTextChangedListener(new MoneyTextWatcher(((EditText) findViewById(R.id.id_input_taxfree))));
        ((EditText) findViewById(R.id.id_input_national)).addTextChangedListener(new MoneyTextWatcher(((EditText) findViewById(R.id.id_input_national))));
        ((EditText) findViewById(R.id.id_input_healthcare)).addTextChangedListener(new MoneyTextWatcher(((EditText) findViewById(R.id.id_input_healthcare))));
        ((EditText) findViewById(R.id.id_input_longtermcare)).addTextChangedListener(new MoneyTextWatcher(((EditText) findViewById(R.id.id_input_longtermcare))));
        ((EditText) findViewById(R.id.id_input_employmentcare)).addTextChangedListener(new MoneyTextWatcher(((EditText) findViewById(R.id.id_input_employmentcare))));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEventListener();
    }

    public void initEventListener()
    {
        // 계산하기 버튼 클릭시
        findViewById(R.id.id_btn_calculate).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButtonCalculate();// 계산하기 버튼 클릭시
            }
        });
    }

    /**
     * 세금 계산하기 버튼 클릭시 발생
     *
     */
    public void onClickButtonCalculate() {
        long salary = MoneyTextWatcher.getValue((EditText) findViewById(R.id.id_input_salary));
        long taxfree = MoneyTextWatcher.getValue((EditText) findViewById(R.id.id_input_taxfree));
        long national = MoneyTextWatcher.getValue((EditText) findViewById(R.id.id_input_national));
        long healthCare = MoneyTextWatcher.getValue((EditText) findViewById(R.id.id_input_healthcare));
        long longTermCare = MoneyTextWatcher.getValue((EditText) findViewById(R.id.id_input_longtermcare));
        long employmentCare = MoneyTextWatcher.getValue((EditText) findViewById(R.id.id_input_employmentcare));

        /*
        double salary = parseDouble((EditText) findViewById(R.id.id_input_salary));
        double taxfree = parseDouble((EditText) findViewById(R.id.id_input_taxfree));
        double national = parseDouble((EditText) findViewById(R.id.id_input_national));
        double healthCare = parseDouble((EditText) findViewById(R.id.id_input_healthcare));
        double longTermCare = parseDouble((EditText) findViewById(R.id.id_input_longtermcare));
        double employmentCare = parseDouble((EditText) findViewById(R.id.id_input_employmentcare));
        */


        long baseMoney = salary - taxfree;

        Services.getInstance().getTaxCalculatorRates().nationalRate = getRates(baseMoney,national);
        Services.getInstance().getTaxCalculatorRates().healthCareRate = getRates(baseMoney,healthCare);
        Services.getInstance().getTaxCalculatorRates().longTermCareRate = getRates(healthCare,longTermCare);
        Services.getInstance().getTaxCalculatorRates().employmentCareRate = getRates(baseMoney,employmentCare);

        //결과 화면 호출
        //Intent intent=new Intent(getActivity(),ReportActivity.class);
        //startActivity(intent);
        TaxCalculatorReportFragment fragment = TaxCalculatorReportFragment.newInstance();
        ((MainActivity)getActivity()).replaceFragments(fragment);
    }

    /**
     * 이자율을 뽑아내는 계산식
     * @param origin int
     * @param interest int
     * @return double
     */
    @SuppressWarnings("unused")
    private double getRates(int origin, int interest)
    {
        return getRates((double)origin,(double)interest);
    }
    private double getRates(long origin,long interest)
    {
        return getRates((double)origin,(double)interest);
    }
    private double getRates(double origin,double interest)
    {
        double result = (interest / origin) * 100.0;
        return (double)Math.round(result * 10000) / 10000; //반올림
        //return (double)Math.floor(result * 10) / 10; //버림
        //return (double)Math.ceil(result * 10) / 10; //올림
        //return  ((double)interest / (double)origin) * 100.0;
    }
    @SuppressWarnings("unused")
    private int parseInt(EditText editText)
    {
        return Integer.parseInt(editText.getText().toString());
    }

    @SuppressWarnings("unused")
    private double parseDouble(EditText editText)
    {
        return Double.parseDouble(editText.getText().toString());
    }

    public static class InputFilterMinMax implements InputFilter {
        @SuppressWarnings("FieldCanBeLocal")
        private long min = 0;
        @SuppressWarnings("FieldCanBeLocal")
        private long max;

        InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }
        @SuppressWarnings("unused")
        public InputFilterMinMax(long min, long max){
            this.min = min;
            this.max = max;
        }
        @SuppressWarnings("unused")
        public InputFilterMinMax(String min, String max) {
            this.min = Long.parseLong(min);
            this.max = Long.parseLong(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                String str = dest.toString() + source.toString();
                if (str.contains(",")) {
                    str = str.replaceAll(",", "");
                }
                long input = Long.parseLong(str);

                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException ignored) { }
            return "";
        }

        private boolean isInRange(long a, long b, long c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

    public static class InputFilterDoubleMinMax implements InputFilter {

        private double min, max;

        @SuppressWarnings("unused")
        public InputFilterDoubleMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @SuppressWarnings("unused")
        public InputFilterDoubleMinMax(double min, double max){
            this.min = min;
            this.max = max;
        }
        @SuppressWarnings("unused")
        public InputFilterDoubleMinMax(String min, String max) {
            this.min = Double.parseDouble(min);
            this.max = Double.parseDouble(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                Double input = Double.parseDouble(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException ignored) { }
            return "";
        }

        private boolean isInRange(Double a, Double b, Double c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}

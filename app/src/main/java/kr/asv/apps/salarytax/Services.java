package kr.asv.apps.salarytax;

import android.content.Context;
import android.util.Log;

import kr.asv.apps.salarytax.databases.WordInfoDBHandler;
import kr.asv.apps.salarytax.databases.TableWordDictionary;
import kr.asv.calculators.salary.SalaryCalculator;

/**
 * 어플 전체적으로 활용되는 기능들을 모아두는 클래스
 * Created by EXIZT on 2016-04-27.
 */
public class Services {
    //default
    private static Services instance = new Services();
    private Context applicationContext = null;
    @SuppressWarnings("FieldCanBeLocal")
    private boolean isDebug = true;

    //objects
    private SalaryCalculator calculator = new SalaryCalculator();
    private TaxCalculatorRates taxCalculatorRates = new TaxCalculatorRates();
    @SuppressWarnings("FieldCanBeLocal")
    private WordInfoDBHandler wordInfoDbHandler = null;
    TableWordDictionary tableWordDictionary;

    /**
     * 생성자 메서드
     */
    private Services() {
    }

    public static Services getInstance() {
        return instance;
    }

    /**
     * 싱글톤 비슷하게 구현. 처음 한번만 init 메서드를 호출하게
     * @param context Context
     * @return Services
     */
    public static Services getInstanceWithInit(Context context)
    {
        if(instance.applicationContext == null){
            instance.init(context);
        }
        return instance;
    }

    /**
     * 최초 한번만 실행하게끔
     * @param context Context
     */
    public void init(Context context)
    {
        //최초 한번만 실행한다.
        if(this.applicationContext != null) return;
        this.applicationContext = context;

        debug("[Init] >> ");

        //디비 연결
        this.wordInfoDbHandler = new WordInfoDBHandler(context);
        debug("[Init] > set WordInfoDBHandler");

        this.tableWordDictionary = new TableWordDictionary(wordInfoDbHandler.getDb());
        debug("[Init] > set TableWordDictionary");
    }

    /**
     * 디버깅
     * @param msg message
     */
    private void debug(String msg)
    {
        if(isDebug) {
            Log.e("[EXIZT-DEBUG]", "[Services]" + msg);
        }
    }

    public void onResume(Context context)
    {

    }
    public SalaryCalculator getCalculator()
    {
        return calculator;
    }
    public TableWordDictionary getTableWordDictionary(){
        return this.tableWordDictionary;
    }
    public TaxCalculatorRates getTaxCalculatorRates(){
        return this.taxCalculatorRates;
    }

    public class TaxCalculatorRates{
        public double nationalRate;
        public double healthCareRate;
        public double longTermCareRate;
        public double employmentCareRate;
    }
}

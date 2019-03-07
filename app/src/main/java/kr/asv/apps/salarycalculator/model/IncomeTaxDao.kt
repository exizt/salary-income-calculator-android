package kr.asv.apps.salarycalculator.model

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.util.*

class IncomeTaxDao(private val db: SQLiteDatabase) {

    fun getValue(money: Long, family: Int, yearmonth: String) : Long{
        debug("getCurrentRates 호출")
        var resultTax : Long = 0
        val searchMoneyUnit = money / 1000

        debug("기준 금액 검색 단위",searchMoneyUnit)
        debug("family",family)
        debug("yearmonth",yearmonth)

        // 조건절이 이용될 yearmonth ('201902' 같은 형식) 을 만드는 구문.
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR).toString()
        val month = cal.get(Calendar.MONTH).toString().padStart(2,'0')
        //val yearmonth = "$year$month"

        // 데이터베이스 에서 4대보험 세율을 조회
        val cur = db.query("income_tax_table",
                arrayOf("tax_1", "tax_2", "tax_3", "tax_4", "tax_5", "tax_6", "tax_7", "tax_8", "tax_9", "tax_10", "tax_11"), "yearmonth <= ? and money_start <= ? and money_end > ?",
                arrayOf(yearmonth,searchMoneyUnit.toString(),searchMoneyUnit.toString()), null, null, "yearmonth desc", "1")
        if (cur.moveToFirst()) {
            // 결과값이 있을 때
            resultTax = cur.getLong(cur.getColumnIndex("tax_$family"))
            cur.close()
            debug("결과값",resultTax)
        } else {
            // 결과값이 없을 때
            cur.close()
            debug("결과 없음")

            // 기준표 보다 큰 금액일 경우가 있음. 이 경우는 계산식을 이용한다.
            val cur2 = db.query("income_tax_table",
                    arrayOf("money_start","tax_1", "tax_2", "tax_3", "tax_4", "tax_5", "tax_6", "tax_7", "tax_8", "tax_9", "tax_10", "tax_11"), "yearmonth <= ? and money_end = 0",
                    arrayOf(yearmonth), null, null, "yearmonth desc", "1")
            if (cur2.moveToFirst()) {
                // 기준액이 표의 최대값과 같거나 클 경우에만 해당.
                if( searchMoneyUnit >= cur2.getInt(cur2.getColumnIndex("money_start"))){
                    // 표의 최대 세액
                    val maxTax = cur2.getInt(cur2.getColumnIndex("tax_$family"))
                    debug("표에서 최대 세액",maxTax)

                    // 부가 세액
                    val overTax = calculatorExtendInterval(yearmonth, money)
                    debug("추가치에 대한 계산 부가 세액",overTax)
                    resultTax = maxTax.toLong() + overTax
                }
            }
            cur2.close()
        }
        return resultTax
    }

    /**
     * 초과되는 기준금액에 한해서 부과되는 소득세 계산
     * (천 단위 로 연산)
     */
    private fun calculatorExtendInterval(yearmonth:String, value: Long): Long{
        val v201802 = arrayOf(
                arrayOf("45000","100000",(0.42*0.98).toString()),
                arrayOf("28000","45000",(0.40*0.98).toString()),
                arrayOf("14000","28000",(0.38*0.98).toString()),
                arrayOf("10000","14000",(0.35*0.98).toString())
        )

        val v201702 = arrayOf(
                arrayOf("45000","10000000",(0.42*0.98).toString()),
                arrayOf("28000","45000",(0.40*0.98).toString()),
                arrayOf("10000","28000",(0.35*0.98).toString())
        )

        when (yearmonth){
            "201802" -> {
                return calculateIntervalMultiplicationWrap(v201802,value)
            }
            "201702" -> {
                return calculateIntervalMultiplicationWrap(v201702,value)
            }
        }
        return 0
    }

    /**
     * 구간 계산 전에 단위간의 처리를 해줌.
     * 구간이 1천원 단위로 되어 있기 때문.
     */
    private fun calculateIntervalMultiplicationWrap(interVal: Array<Array<String>>, v : Long) : Long{
        return calculateIntervalMultiplication(interVal,v/1000) * 1000
    }
    /**
     * 인수 로는 '구간' 배열 과 값
     * 구간에 따른 계산을 함.
     * 최소값의 오버 구간에 대해서만 계산함.
     */
    private fun calculateIntervalMultiplication(interVal: Array<Array<String>>, v : Long) : Long
    {
         var res : Long = 0
        interVal.forEach {
            val x : Int = it[0].toInt()
            val y : Int = it[1].toInt()
            val r : Double = it[2].toDouble()
            if(v >= x){
                val add = if(v > y){
                    (y - x) * r
                } else {
                    (v - x) * r
                }
                res += add.toInt()
            }
        }
        return res
    }

    /**
     * 디버깅 메서드
     * 변수가 두개 넘어올 경우의 처리 추가
     * @param msg 메시지
     */
    @Suppress("unused")
    private fun debug(msg: String, msg2 : Any = "") {
        @Suppress("ConstantConditionIf")
        if (isDebug) {
            Log.d(TAG, "$msg $msg2")
        }
    }

    companion object {
        private const val TAG = "[EXIZT-DEBUG][IncomeTaxDao]"
        private const val isDebug = true
    }
}
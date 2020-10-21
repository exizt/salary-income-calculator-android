package kr.asv.salarycalculator.app.fragments.dummy

import java.util.*


object WordDictionaryContent {
    var isUsed: Boolean = false
    // 리스트를 만들 때 이용하는데, index position 으로 접근하기 때문에, 충분함.
    val ITEMS: MutableList<Item> = ArrayList()
    // ITEM_MAP 은 사용 안 하고 있음. 앞으로도 별로 필요 없을 듯 함.
    // private val ITEM_MAP: MutableMap<String, Item> = HashMap()

    fun addItem(item: Item) {
        ITEMS.add(item)
        // ITEM_MAP[item.cid] = item
        isUsed = true
    }

    data class Item(var id: Int = 0, var cid: String = "", var name: String? = "", var description: String? = "", var process: String? = "", var history: String? = "")
}

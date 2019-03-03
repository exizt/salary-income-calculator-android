package kr.asv.apps.salarycalculator.fragments.dummy

import java.util.*


object WordDictionaryContent {
    var isUsed: Boolean = false
    val ITEMS: MutableList<Item> = ArrayList()
    private val ITEM_MAP: MutableMap<String, Item> = HashMap()

    fun addItem(item: Item) {
        ITEMS.add(item)
        ITEM_MAP[item.id] = item
        isUsed = true
    }

    data class Item(var id: Int = 0, var cid: String = "", var name: String? = "", var description: String? = "", var process: String? = "", var history: String? = "")
}

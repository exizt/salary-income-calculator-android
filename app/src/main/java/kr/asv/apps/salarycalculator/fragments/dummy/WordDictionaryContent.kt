package kr.asv.apps.salarycalculator.fragments.dummy

import java.util.*


object WordDictionaryContent {
	val ITEMS: MutableList<Item> = ArrayList()
	private val ITEM_MAP: MutableMap<String, Item> = HashMap()

	fun addItem(item: Item) {
		ITEMS.add(item)
		ITEM_MAP.put(item.id, item)
	}

	data class Item(var key:Int=0, var id:String="", var subject:String?="", var explanation:String?="", var process:String?="", var history:String?="")
}

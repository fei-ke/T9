package com.fei_ke.t9.home

object Panel {
    sealed class Item(val text: String) {
        class Number(val key: Int, text: String) : Item(text)
        class Delete : Item("←")
        class Clear : Item("x")
    }

    val items = listOf(
        Item.Number(1, "1"),
        Item.Number(2, "2 ABC"),
        Item.Number(3, "3 DEF"),
        Item.Number(4, "4 GHI"),
        Item.Number(5, "5 JKL"),
        Item.Number(6, "6 MNO"),
        Item.Number(7, "7 PQRS"),
        Item.Number(8, "8 TUV"),
        Item.Number(9, "9 WXYZ"),
        Item.Clear(),
        Item.Number(0, "0"),
        Item.Delete()
    )
}
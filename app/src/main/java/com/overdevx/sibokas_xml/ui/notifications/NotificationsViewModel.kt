package com.overdevx.sibokas_xml.ui.notifications

import androidx.lifecycle.ViewModel
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.data.API.Card

class NotificationsViewModel : ViewModel() {

    val cards = getCardsMockup()

    /** LOCAL MOCKUP */
    private fun getCardsMockup(): List<Card> {
        val cards: MutableList<Card> = mutableListOf()
        cards.add(Card(id = "1", text = "••••  5282", icon = R.drawable.building))
        cards.add(Card(id = "2", text = "••••  4450", icon = R.drawable.building))
        cards.add(Card(id = "3", text = "••••  3498", icon = R.drawable.building))
        cards.add(Card(id = "4", text = "••••  0244", icon = R.drawable.building))
        return cards
    }
}
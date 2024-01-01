package com.overdevx.sibokas_xml.data.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CountdownViewModel : ViewModel() {
    val countdownLiveData = MutableLiveData<Long>()
}
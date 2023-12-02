package com.overdevx.sibokas_xml.adapter

import android.app.Application
import com.google.android.material.color.DynamicColors

class SibokasApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Apply dynamic color
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
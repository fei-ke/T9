package com.fei_ke.t9

import android.os.Bundle
import android.preference.PreferenceActivity

class LicensesActivity : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.licenses)
    }
}

package org.dean.test.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.dean.test.R

class NavigationActivity: AppCompatActivity() {

    companion object {
        const val TAG = "NavigationActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
    }
}


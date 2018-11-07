package org.dean.test

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import org.dean.test.bottomsheet.BottomSheetActivity
import org.dean.test.navigation.NavigationActivity
import org.dean.test.paging.PagingActivity
import org.dean.test.sensors.SensorActivity

class StartActivity : AppCompatActivity() {

    companion object {
        private data class CardInfo(@StringRes val levelRes: Int,
                                    @StringRes val titleRes: Int,
                                    @StringRes val descRes: Int,
                                    val onClick: (Activity) -> Unit)

        private val cardInfos = listOf(
                CardInfo(R.string.paging_example_level,
                        R.string.paging_example_title,
                        R.string.paging_example_description
                ) { activity -> activity.startActivity(Intent(activity, PagingActivity::class.java)) },

                CardInfo(R.string.navigation_example_level,
                        R.string.navigation_example_title,
                        R.string.navigation_example_description
                ) { activity -> activity.startActivity(Intent(activity, NavigationActivity::class.java)) },

                CardInfo(R.string.bottom_sheet_example_level,
                        R.string.bottom_sheet_example_title,
                        R.string.bottom_sheet_example_description
                ) { activity -> activity.startActivity(Intent(activity, BottomSheetActivity::class.java)) },

                CardInfo(R.string.sensor_example_level,
                        R.string.sensor_sheet_example_title,
                        R.string.sensor_sheet_example_description
                ) { activity -> activity.startActivity(Intent(activity, SensorActivity::class.java)) },

                CardInfo(R.string.example_placeholder_level,
                        R.string.example_placeholder_title,
                        R.string.example_placeholder_description
                ) { }
        )
    }

    private var list: ViewGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        list = findViewById(R.id.start_layout)

        setCards()
    }

    private fun setCards() {
        cardInfos.forEach { cardInfo ->
            val card = layoutInflater.inflate(R.layout.start_card, list, false)
            card?.findViewById<TextView>(R.id.level)?.setText(cardInfo.levelRes)
            card?.findViewById<TextView>(R.id.title)?.setText(cardInfo.titleRes)
            card?.findViewById<TextView>(R.id.description)?.setText(cardInfo.descRes)
            card?.setOnClickListener { cardInfo.onClick(this@StartActivity) }
            list?.addView(card)
        }
    }

}

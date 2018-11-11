package org.dean.test.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.dean.test.R
import org.dean.test.util.toast

class BottomSheetActivity: AppCompatActivity() {

    private lateinit var sheet: View
    private lateinit var bsb: BottomSheetBehavior<View>
    private lateinit var adapter: SimpleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_sheet)

        sheet = findViewById<View>(R.id.sheet)
        bsb = BottomSheetBehavior.from(sheet)

        adapter = SimpleAdapter()
        findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(this@BottomSheetActivity)
            adapter = this@BottomSheetActivity.adapter
        }

    }

    override fun onResume() {
        super.onResume()

        findViewById<View>(R.id.invalidate).setOnClickListener {
            adapter.notifyDataSetChanged()
        }

        sheet.postDelayed({
            bsb.state = BottomSheetBehavior.STATE_EXPANDED
            bsb.state = BottomSheetBehavior.STATE_COLLAPSED
        }, 500)
    }
}

class SimpleAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val SIZE = 20

        private val msgs = IntRange(0, SIZE).map { i ->
            "Message ${i + 1}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object: RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_simple, parent, false)) {}
    }

    override fun getItemCount(): Int {
        return SIZE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as TextView).apply {
            this.text = msgs[position]
            this.setOnClickListener { v ->
                context.toast("Message: ${position + 1} clicked!")
            }
        }


    }

}
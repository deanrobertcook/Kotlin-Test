package org.dean.test

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import org.dean.test.core.ImageMessage
import org.dean.test.core.Message
import org.dean.test.core.TextMessage
import org.dean.test.paging.ListViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val listAdapter = Adapter(this@MainActivity)
        findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = listAdapter

            val viewModel = ViewModelProviders.of(this@MainActivity).get(ListViewModel::class.java)
            viewModel.msgList.observe(this@MainActivity, Observer { t ->
                t?.let { listAdapter.submitList(it) }
            })

        }
    }
}

@GlideModule
class TestGlideModule : AppGlideModule()


class Adapter(private val context: Context): PagedListAdapter<Message, Adapter.Companion.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return when (oldItem) {
                    is TextMessage ->
                        newItem is TextMessage &&
                            oldItem.msg == newItem.msg &&
                                oldItem.time == newItem.time

                    is ImageMessage ->
                        newItem is ImageMessage &&
                                oldItem.time == newItem.time

                    else -> false
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(when (viewType) {
            0    -> inflater.inflate(R.layout.row_message, parent, false)
            else -> inflater.inflate(R.layout.row_image, parent, false)
        })
    }

    override fun getItemViewType(position: Int): Int {
        return (when (getItem(position)) {
            is TextMessage -> 0
            else           -> 1
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            0 -> {
                val msg = getItem(position)
                holder.view.findViewById<TextView>(R.id.id).text = msg?.id.toString()
                holder.view.findViewById<TextView>(R.id.time).text = msg?.time.toString()
                holder.view.findViewById<TextView>(R.id.message).text = (msg as TextMessage?)?.msg
            }
            else -> {
                val msg = getItem(position) as ImageMessage?
                if (msg == null) {
                    (holder.view as ImageView).setImageBitmap(null)
                } else {
                    Glide.with(context)
                            .load(Uri.parse(msg.url))
                            .into(holder.view as ImageView)
                }
            }
        }
    }


}

package org.dean.test

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.dean.test.core.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)

            var page = 0
            val pageObservable = Observable.create<Int> { emitter ->
                emitter.onNext(page)
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)

                        if (!recyclerView!!.canScrollVertically(1)) {
                            page++
                            emitter.onNext(page)
                        }
                    }
                })
            }

            val msgs = MessageService(DownloadClient(OkHttpClient()))

            adapter = Adapter(pageObservable.observeOn(Schedulers.io()).flatMap { msgs.nextPage(it).toObservable() }, this@MainActivity)
        }
    }
}

@GlideModule
class TestGlideModule : AppGlideModule()


class Adapter(msgs: Observable<List<Message>>, private val context: Context): RecyclerView.Adapter<Adapter.Companion.ViewHolder>() {

    private var msgsCache = emptyList<Message>()

    init {
        msgs.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list: List<Message> ->
                    msgsCache += list
                    notifyDataSetChanged()
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
        return (when (msgsCache[position]) {
            is TextMessage -> 0
            else           -> 1
        })
    }

    override fun getItemCount(): Int {
        return msgsCache.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            0 -> {
                val msg = msgsCache[position]
                holder.view.findViewById<TextView>(R.id.id).text = msg.id.toString()
                holder.view.findViewById<TextView>(R.id.time).text = msg.time.toString()
                holder.view.findViewById<TextView>(R.id.message).text = (msg as TextMessage).msg
            }
            else -> {
                Glide.with(context)
                        .load(Uri.parse((msgsCache[position] as ImageMessage).url))
                        .into(holder.view as ImageView)
            }
        }
    }


    companion object {
        class ViewHolder(val view: View): RecyclerView.ViewHolder(view)
    }
}

package org.dean.test

import android.content.Context
import android.databinding.ViewDataBinding
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.dean.test.core.*
import org.dean.test.databinding.RowImageBinding
import org.dean.test.databinding.RowMessageBinding
import com.bumptech.glide.module.AppGlideModule

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = Adapter(MessageService(DownloadClient(OkHttpClient())).nextPage(1).toObservable(), this@MainActivity)
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
                    msgsCache = list
                    notifyDataSetChanged()
                }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(when (viewType) {
            0 -> RowMessageBinding.inflate(inflater, parent, false)
            1 -> RowImageBinding.inflate(inflater, parent, false)
            else -> null
        })
    }

    override fun getItemViewType(position: Int): Int {
        return (when (msgsCache.get(position)) {
            is TextMessage -> 0
            is ImageMessage -> 1
            else -> -1
        })
    }

    override fun getItemCount(): Int {
        return msgsCache.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.binding) {
            is RowMessageBinding -> holder.binding.message = msgsCache[position] as TextMessage
            is RowImageBinding   -> {
                Glide.with(context)
                        .load(Uri.parse((msgsCache[position] as ImageMessage).url))
                        .into(holder.binding.root as ImageView)
            }
        }
    }


    companion object {
        class ViewHolder(val binding: ViewDataBinding?): RecyclerView.ViewHolder(binding?.root)
    }
}

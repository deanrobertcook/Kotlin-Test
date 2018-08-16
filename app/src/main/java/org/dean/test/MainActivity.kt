package org.dean.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.dean.test.core.DownloadClient
import org.dean.test.core.Message
import org.dean.test.core.MessageService
import org.dean.test.core.TextMessage
import org.dean.test.databinding.RowBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = Adapter(MessageService(DownloadClient(OkHttpClient())).nextPage(1).toObservable())
        }
    }
}



class Adapter(msgs: Observable<List<Message>>): RecyclerView.Adapter<Adapter.Companion.ViewHolder>() {

    var msgsCache = emptyList<TextMessage>()

    init {
        msgs.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list: List<Message> ->
                    msgsCache = list.filterIsInstance(TextMessage::class.java)
                    notifyDataSetChanged()
                }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return msgsCache.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.message = msgsCache[position]
    }


    companion object {
        class ViewHolder(val binding: RowBinding): RecyclerView.ViewHolder(binding.root)
    }
}

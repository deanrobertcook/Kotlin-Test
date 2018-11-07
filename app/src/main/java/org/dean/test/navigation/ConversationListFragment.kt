package org.dean.test.navigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.dean.test.R
import java.util.*

class ConversationListFragment : Fragment() {

    private var adapter: ConversationListAdapter? = null
    private var clickSubscription: Disposable? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adapter = ConversationListAdapter(ViewModelProviders.of(this@ConversationListFragment).get(ConversationListViewModel::class.java).conversations)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_conversation_list, container, false).apply {
            this.findViewById<RecyclerView>(R.id.recycler_view).apply {
                layoutManager = LinearLayoutManager(context)
                adapter = this@ConversationListFragment.adapter
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adapter?.wire()
        clickSubscription = adapter?.onConvClicked?.forEach { conv ->
            val action = ConversationListFragmentDirections.actionConvClicked()
            action.setConvName(conv.name)
            NavHostFragment.findNavController(this).navigate(action)
        }
    }

    override fun onPause() {
        adapter?.unWire()
        clickSubscription?.dispose()
        super.onPause()
    }

    override fun onDetach() {
        super.onDetach()
        adapter = null
        clickSubscription = null
    }

}

class ConversationListAdapter(private val conversations: Observable<List<Conversation>>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val clickSubject = PublishSubject.create<Conversation>()
    val onConvClicked: Observable<Conversation> = clickSubject

    private var convsCache = emptyList<Conversation>()

    private lateinit var convsSubscription: Disposable

    fun wire() {
        convsSubscription = conversations
                .observeOn(AndroidSchedulers.mainThread())
                .forEach { cs ->
                    convsCache = cs
                    notifyDataSetChanged()
                }
    }

    fun unWire() {
        convsSubscription.dispose()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return object: RecyclerView.ViewHolder(inflater.inflate(R.layout.navigation_row_conversation, parent, false)) {}
    }

    override fun getItemCount(): Int {
        return convsCache.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val conv = convsCache[position]
        holder.itemView.findViewById<TextView>(R.id.name).text = conv.name
        holder.itemView.setOnClickListener { _ -> clickSubject.onNext(conv) }
    }


}

class ConversationListViewModel: ViewModel() {

    val conversations = Observable.create<List<Conversation>> { em ->
        em.onNext(IntRange(1, 50).map { i ->
            Conversation(UUID.randomUUID(), "Conversation$i", false)
        })
    }

}

data class Conversation(val id: UUID,
                        val name: String,
                        val isGroup: Boolean)

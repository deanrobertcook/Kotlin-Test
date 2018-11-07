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
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.dean.test.R
import java.util.*

class ConversationFragment : Fragment() {

    private var convViewModel: ConversationViewModel? = null
    private var convSubscription: Disposable? = null
    private var convNameView: TextView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        convViewModel = ViewModelProviders
                .of(this@ConversationFragment)
                .get(ConversationViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_conversation, container, false).apply {
            convNameView = this.findViewById(R.id.conv_name)
        }
    }

    override fun onResume() {
        super.onResume()
        convSubscription = convViewModel
                ?.conversation
                ?.forEach { conv ->
                    convNameView?.text = conv.name
                }

        convViewModel?.setConvName(ConversationFragmentArgs.fromBundle(arguments).convName)
    }

    override fun onPause() {
        super.onPause()
        convSubscription?.dispose()
    }

    override fun onDetach() {
        super.onDetach()
        convViewModel = null
        convSubscription = null
    }
}

class ConversationViewModel : ViewModel() {

    private val conversationSubject = PublishSubject.create<Conversation>()
    val conversation: Observable<Conversation> = conversationSubject

    fun setConvName(convName: String) {
        conversationSubject.onNext(Conversation(UUID.randomUUID(), convName, false))
    }

}

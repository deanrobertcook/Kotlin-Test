package org.dean.test.navigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.dean.test.R
import org.dean.test.navigation.model.Conversation
import org.dean.test.navigation.repo.ConversationRepository
import java.util.*
import javax.inject.Inject

class ConversationFragment : Fragment() {

    @Inject
    lateinit var convRepo: ConversationRepository

    private var convSubscription: Disposable? = null
    private var convNameView: TextView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as NavigationActivity).component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_conversation, container, false).apply {
            convNameView = this.findViewById(R.id.conv_name)
        }
    }

    override fun onResume() {
        super.onResume()
        val convId = UUID.fromString(ConversationFragmentArgs.fromBundle(arguments).convId)
        convSubscription = convRepo.getConversation(convId).subscribe { conv ->
            convNameView?.text = conv.name
        }
    }

    override fun onPause() {
        super.onPause()
        convSubscription?.dispose()
    }
}

class ConversationViewModel : ViewModel() {

    private val conversationSubject = PublishSubject.create<Conversation>()
    val conversation: Observable<Conversation> = conversationSubject

    fun setConvName(convName: String) {
        conversationSubject.onNext(Conversation(UUID.randomUUID(), convName, false))
    }

}

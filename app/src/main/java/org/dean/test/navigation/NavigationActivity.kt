package org.dean.test.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.Component
import dagger.Module
import dagger.Provides
import org.dean.test.R
import org.dean.test.navigation.repo.ConversationRepository
import org.dean.test.navigation.repo.DummyConversationRepository
import javax.inject.Singleton

class NavigationActivity: AppCompatActivity() {

    lateinit var component: NavigationComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        component = DaggerNavigationComponent
                .builder()
                .navigationModule(NavigationModule)
                .build()

    }
}

@Singleton
@Component(modules = arrayOf(NavigationModule::class))
interface NavigationComponent {
    fun inject(f: ConversationListFragment?)
    fun inject(f: ConversationFragment)
}

@Module
object NavigationModule {
    @Provides
    @Singleton
     fun provideConvRepo(): ConversationRepository {
        return DummyConversationRepository()
    }
}
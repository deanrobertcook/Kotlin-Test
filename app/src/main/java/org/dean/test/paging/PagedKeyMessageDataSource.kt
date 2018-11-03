package org.dean.test.paging

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import org.dean.test.core.Message
import org.dean.test.core.MessageService

class PagedKeyMessageDataSource(private val msgService: MessageService): PageKeyedDataSource<Int, Message>() {

    companion object {
        const val SERVER_PAGE_SIZE = 50
        const val SERVER_PAGE_COUNT = 10
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Message>) {
        msgService.nextPage(0).subscribe { list ->
            callback.onResult(list, 0, SERVER_PAGE_COUNT * SERVER_PAGE_SIZE, null, 1)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Message>) {
        msgService.nextPage(params.key).subscribe { list ->
            callback.onResult(list, if (params.key == SERVER_PAGE_COUNT -1) null else params.key + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Message>) {
        //Not needed
    }
}

class MessagePageDataSourceFactory(private val msgService: MessageService): DataSource.Factory<Int, Message>() {

    override fun create(): DataSource<Int, Message> {
        return PagedKeyMessageDataSource(msgService)
    }
}
package org.dean.test.paging

import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.toLiveData
import okhttp3.OkHttpClient
import org.dean.test.core.DownloadClient
import org.dean.test.core.MessageService

class ListViewModel: ViewModel() {

    private val msgService = MessageService(DownloadClient(OkHttpClient()))

    val msgList =
            MessagePageDataSourceFactory(msgService).toLiveData(
                    config = PagedList.Config.Builder()
                            .setPageSize(PagedKeyMessageDataSource.SERVER_PAGE_SIZE)
                            .setPrefetchDistance(PagedKeyMessageDataSource.SERVER_PAGE_SIZE * 3)
                            .build()
            )
}
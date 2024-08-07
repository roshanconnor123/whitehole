package com.bera.whitehole.utils.coil

import coil.ImageLoader
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import com.bera.whitehole.api.BotApi
import com.bera.whitehole.data.localdb.entities.RemotePhoto
import com.bera.whitehole.utils.getMimeTypeFromExt

class NetworkFetcher(
    private val botApi: BotApi = BotApi,
    private val remotePhoto: RemotePhoto,
    private val options: Options,
) : Fetcher {
    override suspend fun fetch(): FetchResult? {
        val byteArray = botApi.getFile(remotePhoto.remoteId)
        return if (byteArray != null) {
            val buffer = okio.Buffer().write(byteArray)
            SourceResult(
                source = ImageSource(buffer, options.context),
                mimeType = getMimeTypeFromExt(remotePhoto.photoType),
                dataSource = DataSource.NETWORK
            )
        } else {
            null
        }
    }

    class Factory(
        private val botApi: BotApi = BotApi,
    ) : Fetcher.Factory<RemotePhoto> {
        override fun create(
            data: RemotePhoto,
            options: Options,
            imageLoader: ImageLoader,
        ): Fetcher? = NetworkFetcher(botApi, data, options)
    }
}
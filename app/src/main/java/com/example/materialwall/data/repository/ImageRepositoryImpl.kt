package com.example.materialwall.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.materialwall.common.Resource
import com.example.materialwall.data.local.ImageInfoDatabase
import com.example.materialwall.data.local.entity.ImageInfoEntity
import com.example.materialwall.data.paging.ImageRemoteMediator
import com.example.materialwall.data.remote.PixabayApi
import com.example.materialwall.domain.model.Image
import com.example.materialwall.domain.model.ImageRequest
import com.example.materialwall.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val api: PixabayApi,
    private val db: ImageInfoDatabase
) : ImageRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getImages(
        imageRequest: ImageRequest,
        timestamp: String
    ): Flow<PagingData<ImageInfoEntity>> {
        val query = (imageRequest.id?: "") +
                (imageRequest.searchTerm ?: "") +
                (imageRequest.category ?: "")
        val pagingSourceFactory = {    db.imageInfoDao.getImageInfo(query = query, timestamp = timestamp) }
        return Pager(
            config = PagingConfig(20, enablePlaceholders = false),
            remoteMediator = ImageRemoteMediator(
                db = db,
                api = api,
                requestVar = imageRequest,
                timestamp = timestamp
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun getImageById(imageId: String): Flow<Resource<Image>> = flow {
        emit(Resource.Loading())
        val data = db.imageInfoDao.getImageById(imageId)
        val isFavorite = data.isFavourite
        val image = data.image
        image.isFavourite = isFavorite
        emit(Resource.Success(image))
    }

    override suspend fun updateFavourite(imageId: String, isFavourite : Int) {
        db.imageInfoDao.updateFavouriteImageById(imageId = imageId, isFavourite = isFavourite)
    }

    override fun getFavourites(): Flow<Resource<List<ImageInfoEntity>>> = flow{
        emit(Resource.Loading())
        val imageList = db.imageInfoDao.getFavourites()
        emit(Resource.Success(imageList))
    }
}

/*    override fun getImages(
        id: String?,
        searchTerm: String?,
        imageType: String,
        category: String?,
        order: String?,
        perPage: Int?,
        page: Int,
        editorsChoice: Boolean,
        minWidth: Int,
        minHeight: Int
    ): Flow<Resource<ImageInfo>> = flow {
        emit(Resource.Loading())

        val query = "${id?:""}${searchTerm?:""}${category?:""}"

        val imageInfo: ImageInfo? =  dao.getImageInfo(query)?.toImageInfo()
        var infoNotStored = false
        if (imageInfo == null) infoNotStored = true
        emit(Resource.Loading(data = imageInfo))

        val timeStamp : String = imageInfo?.timeStamp?: ""
        if (infoNotStored ||Duration.between(Instant.parse(timeStamp), Instant.now())
                .toHours() >= 24L
        ) {
            try {
                val remoteImageInfo = api.getImages(
                    id = id,
                    searchTerm = searchTerm,
                    imageType = imageType,
                    category = category,
                    order = order,
                    perPage = perPage,
                    page = page,
                    editorsChoice = editorsChoice,
                    minWidth = minWidth,
                    minHeight = minHeight
                )
                val currentTimestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                dao.deleteImageInfo(query = query)
                dao.insertImageInfo(remoteImageInfo.toImageInfoEntity(timestamp = currentTimestamp, query = query))

            } catch (e: HttpException) {
                emit(
                    Resource.Error(
                        message = "Oops, something went wrong!",
                        data = imageInfo
                    )
                )

            } catch (e: IOException) {
                emit(
                    Resource.Error(
                        message = "Couldn't reach server, check your internet connection.",
                        data = imageInfo
                    )
                )
            }
            val newImageInfo = dao.getImageInfo(query)!!
            emit(Resource.Success(newImageInfo.toImageInfo()))

        } else {
            emit(Resource.Success(imageInfo!!))
        }
    }*/



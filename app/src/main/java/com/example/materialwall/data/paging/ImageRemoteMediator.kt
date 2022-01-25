package com.example.materialwall.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.materialwall.data.local.ImageInfoDatabase
import com.example.materialwall.data.local.entity.ImageInfoEntity
import com.example.materialwall.data.local.entity.ImageInfoRemoteKeyEntity
import com.example.materialwall.data.remote.PixabayApi
import com.example.materialwall.data.remote.dto.toImageInfoEntity
import com.example.materialwall.domain.model.ImageRequest
import retrofit2.HttpException
import java.io.IOException


const val STARTING_PAGE_INDEX = 1
@OptIn(ExperimentalPagingApi::class)
class ImageRemoteMediator(
    private val db: ImageInfoDatabase,
    private val api: PixabayApi,
    private val requestVar: ImageRequest,
    private val timestamp: String
) : RemoteMediator<Int, ImageInfoEntity>() {

    override suspend fun initialize(): InitializeAction {
       /* val cacheTimeout = TimeUnit.HOURS.convert(1, TimeUnit.MILLISECONDS)
        return if (System.currentTimeMillis() - db.imageInfoDao. >= cacheTimeout) {
            // Cached data is up-to-date, so there is no need to re-fetch from network.
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            // Need to refresh cached data from network; returning LAUNCH_INITIAL_REFRESH here
            // will also block RemoteMediator's APPEND and PREPEND from running until REFRESH
            // succeeds.

        }*/
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ImageInfoEntity>
    ): MediatorResult {
        try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                    prevKey
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                    nextKey
                }
            }
            val response = api.getImages(
                id = requestVar.id,
                searchTerm = requestVar.searchTerm,
                imageType = requestVar.imageType,
                category = requestVar.category,
                order = requestVar.order,
                perPage = 50,
                page = loadKey,
                editorsChoice = requestVar.editorsChoice,
                minWidth = requestVar.minWidth,
                minHeight = requestVar.minHeight
            )

            val endOfPaging = response.hits.size < 20

            val query = (requestVar.id ?: "") +
                    (requestVar.searchTerm ?: "") +
                    (requestVar.category ?: "")

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.imageInfoRemoteKeyDao.clearRemoteKeys()
                    db.imageInfoDao.deleteImageInfo(query)
                }

                val prevKey = if (loadKey == STARTING_PAGE_INDEX) null else loadKey.minus(1)
                val nextKey = if (endOfPaging) null else loadKey.plus(1)
                val repoId = (1..1000000).random()

                val imageInfoRemoteKeyEntityList = response.hits.map { ImageInfoRemoteKeyEntity(repoId,query, prevKey,nextKey) }
                db.imageInfoRemoteKeyDao.insertAll(imageInfoRemoteKeyEntityList)

                db.imageInfoDao.insertImageInfo(
                    response.toImageInfoEntity(
                        batchId = repoId,
                        query = query,
                        timestamp = timestamp
                    )
                )
            }
            return MediatorResult.Success(endOfPaging)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ImageInfoEntity>):
            ImageInfoRemoteKeyEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.batchId?.let { id ->
                db.withTransaction {
                    db.imageInfoRemoteKeyDao.remoteKeysById(id = id)
                }
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ImageInfoEntity>): ImageInfoRemoteKeyEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { imageInfo ->
                db.withTransaction {
                    db.imageInfoRemoteKeyDao.remoteKeysById(imageInfo.batchId)
                }
            }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ImageInfoEntity>):
            ImageInfoRemoteKeyEntity? {
        return state.pages.lastOrNull() {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { imageInfoEntity ->
            db.withTransaction {
                db.imageInfoRemoteKeyDao.remoteKeysById(
                    imageInfoEntity.batchId
                )
            }
        }
    }
}

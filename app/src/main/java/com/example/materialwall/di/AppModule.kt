package com.example.materialwall.di

import android.app.Application
import androidx.room.Room
import com.example.materialwall.common.Constants
import com.example.materialwall.data.Util.GsonParser
import com.example.materialwall.data.local.Converters
import com.example.materialwall.data.local.ImageInfoDatabase
import com.example.materialwall.data.remote.PixabayApi
import com.example.materialwall.data.repository.ImageRepositoryImpl
import com.example.materialwall.domain.repository.ImageRepository
import com.example.materialwall.domain.use_case.GetFavouritesUseCase
import com.example.materialwall.domain.use_case.GetImageByIdUseCase
import com.example.materialwall.domain.use_case.GetImagesUseCase
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    @Provides
    @Singleton
    fun providesLoggingInterceptor() : HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    @Provides
    @Singleton
    fun providesOkHttpClient(interceptor: HttpLoggingInterceptor) : OkHttpClient{
        return OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
        }.build()

    }

    @Provides
    @Singleton
    fun providesPixabayApi(client: OkHttpClient) : PixabayApi{
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PixabayApi::class.java)
    }

    @Provides
    @Singleton
    fun providesImageInfoDatabase(app :Application) : ImageInfoDatabase{
        return Room.databaseBuilder(
            app, ImageInfoDatabase::class.java, "image_db"
        ).addTypeConverter(Converters(GsonParser(gson = Gson())))
            .build()
    }


    @Provides
    @Singleton
    fun providesImageRepository(api : PixabayApi, db : ImageInfoDatabase) : ImageRepository{
        return ImageRepositoryImpl(api, db)
    }

    @Provides
    @Singleton
    fun provideGetImagesUseCase( repository: ImageRepository) : GetImagesUseCase{
        return GetImagesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetImageByIdUseCase( repository: ImageRepository) : GetImageByIdUseCase{
        return GetImageByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetFavouritesUseCase( repository: ImageRepository) : GetFavouritesUseCase{
        return GetFavouritesUseCase(repository)
    }


}
package com.codedev.videoapp.di

import android.app.Application
import androidx.room.Room
import com.codedev.videoapp.data.api.VideoApi
import com.codedev.videoapp.data.room.QueryDatabase
import com.codedev.videoapp.domain.repository.VideoRepository
import com.codedev.videoapp.domain.repository.VideoRepositoryImpl
import com.codedev.videoapp.domain.use_cases.*
import com.codedev.videoapp.domain.util.VideoUseCase
import com.codedev.videoapp.ui.util.Constants.API_KEY
import com.codedev.videoapp.ui.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideQueryDatabase(application: Application): QueryDatabase {
        return Room.databaseBuilder(
            application,
            QueryDatabase::class.java,
            "query_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRetrofitInstance(): VideoApi {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request: Request =
                    chain.request().newBuilder().addHeader("Authorization", API_KEY).build()
                chain.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(VideoApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRepository(videoApi: VideoApi, db: QueryDatabase): VideoRepository {
        return VideoRepositoryImpl(videoApi, db.queryDao)
    }

    @Provides
    @Singleton
    fun provideUseCases(videoRepository: VideoRepository): VideoUseCase {
        return VideoUseCase(
            searchVideo = SearchVideo(videoRepository),
            getVideo = GetVideo(videoRepository),
            getVideos = GetVideos(videoRepository),
            searchQuery = SearchQuery(videoRepository),
            getQueries = GetQueries(videoRepository),
            deleteQuery = DeleteQuery(videoRepository),
            insertQuery = InsertQuery(videoRepository),
        )
    }
}
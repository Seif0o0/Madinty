package com.madinaty.app.di

import com.madinaty.app.data.services.*
import com.madinaty.app.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        var httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        return httpClient.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(httpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(Constants.BASE_URL)
        .client(httpClient)
        .build()

    @Singleton
    @Provides
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Singleton
    @Provides
    fun provideDepartmentsService(retrofit: Retrofit): DepartmentsService =
        retrofit.create(DepartmentsService::class.java)

    @Singleton
    @Provides
    fun provideOffersService(retrofit: Retrofit): OffersService =
        retrofit.create(OffersService::class.java)

    @Singleton
    @Provides
    fun providePlacesService(retrofit: Retrofit): PlacesService =
        retrofit.create(PlacesService::class.java)

    @Singleton
    @Provides
    fun provideCommonInfoService(retrofit: Retrofit): CommonInfoService =
        retrofit.create(CommonInfoService::class.java)

    @Singleton
    @Provides
    fun provideRegionsService(retrofit: Retrofit): RegionsService =
        retrofit.create(RegionsService::class.java)

}
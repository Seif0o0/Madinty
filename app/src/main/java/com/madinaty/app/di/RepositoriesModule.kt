package com.madinaty.app.di

import com.madinaty.app.data.repository.*
import com.madinaty.app.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoriesModule {
    @Binds
    @ViewModelScoped
    abstract fun providePhoneLoginRepository(repoImpl: PhoneLoginRepositoryImpl): PhoneLoginRepository

    @Binds
    @ViewModelScoped
    abstract fun provideHomeRepository(repoImpl: HomeRepositoryImpl): HomeRepository

    @Binds
    @ViewModelScoped
    abstract fun provideOffersRepository(repoImpl: OffersRepositoryImpl): OffersRepository

    @Binds
    @ViewModelScoped
    abstract fun providePlacesRepository(repoImpl: PlacesRepositoryImpl): PlacesRepository

    @Binds
    @ViewModelScoped
    abstract fun providePinOffersRepository(repoImpl: PinOffersRepositoryImpl): PinOffersRepository

    @Binds
    @ViewModelScoped
    abstract fun provideProfileRepository(repoImpl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @ViewModelScoped
    abstract fun provideCommonInfoRepository(repoImpl: CommonInfoRepositoryImpl): CommonInfoRepository

    @Binds
    @ViewModelScoped
    abstract fun provideRegionsRepository(repoImpl: RegionsRepositoryImpl): RegionsRepository

    @Binds
    @ViewModelScoped
    abstract fun provideAddPlaceRepository(repoImpl: AddPlaceRepositoryImpl): AddPlaceRepository

    @Binds
    @ViewModelScoped
    abstract fun provideAddRemoveFavouritesRepository(repoImpl: AddRemoveFavouriteRepositoryImpl): AddRemoveFavouriteRepository

    @Binds
    @ViewModelScoped
    abstract fun provideFavouritesRepository(repoImpl: AddRemoveFavouriteRepositoryImpl): FavouritesRepository

    @Binds
    @ViewModelScoped
    abstract fun provideDataStoreRepository(repoImpl: DataStoreRepositoryImpl): DataStoreRepository

}
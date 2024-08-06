package inc.pneuma.xule.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import inc.pneuma.xule.data.impl.PreferenceRepositoryImpl
import inc.pneuma.xule.domain.repository.PreferenceRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Singleton
    @Binds
    fun bindRepository(impl: PreferenceRepositoryImpl): PreferenceRepository

}
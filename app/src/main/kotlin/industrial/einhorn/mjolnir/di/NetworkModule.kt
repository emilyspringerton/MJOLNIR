package industrial.einhorn.mjolnir.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import industrial.einhorn.mjolnir.BuildConfig
import industrial.einhorn.mjolnir.data.remote.EmilyApi
import industrial.einhorn.mjolnir.data.remote.FatBabyApi
import industrial.einhorn.mjolnir.data.remote.IdunaApi
import industrial.einhorn.mjolnir.data.remote.IdunaAuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideOkHttpClient(authInterceptor: IdunaAuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides @Singleton
    @Named("iduna")
    fun provideIdunaRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.IDUNA_BASE_URL + "/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    @Named("emily")
    fun provideEmilyRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.EMILY_BASE_URL + "/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideIdunaApi(@Named("iduna") retrofit: Retrofit): IdunaApi =
        retrofit.create(IdunaApi::class.java)

    @Provides @Singleton
    @Named("fatbaby")
    fun provideFatBabyRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.FATBABY_BASE_URL + "/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideEmilyApi(@Named("emily") retrofit: Retrofit): EmilyApi =
        retrofit.create(EmilyApi::class.java)

    @Provides @Singleton
    fun provideFatBabyApi(@Named("fatbaby") retrofit: Retrofit): FatBabyApi =
        retrofit.create(FatBabyApi::class.java)
}

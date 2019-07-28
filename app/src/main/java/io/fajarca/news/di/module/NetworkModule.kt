package io.fajarca.news.di.module

import android.app.Application
import dagger.Module
import dagger.Provides
import io.fajarca.news.BuildConfig
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideHttpCache(application : Application) : Cache {
        val cacheSize : Long = 10 * 10 * 1024
        return Cache(application.cacheDir , cacheSize)
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor() : HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor : HttpLoggingInterceptor , cache : Cache , authorizationInterceptor : Interceptor) : OkHttpClient {
        val client = OkHttpClient.Builder()

        client.connectTimeout(25 , TimeUnit.SECONDS)
        client.writeTimeout(25 , TimeUnit.SECONDS)
        client.readTimeout(25 , TimeUnit.SECONDS)

        client.cache(cache)
        client.addInterceptor(authorizationInterceptor)

        if (BuildConfig.DEBUG) {
            client.addInterceptor(loggingInterceptor)
        }

        return client.build()
    }


    @Provides
    @Singleton
    fun provideAuthorizationInterceptor() : Interceptor {
        return object : Interceptor {

            override fun intercept(chain: Interceptor.Chain): Response {
                return chain.proceed(chain.request()
                        .newBuilder()
                        .header("Authorization", BuildConfig.NEWS_API_KEY)
                        .build())
            }

        }
    }

    @Provides
    @Singleton
    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl : String , okHttpClient : OkHttpClient) : Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient).build()
    }
}
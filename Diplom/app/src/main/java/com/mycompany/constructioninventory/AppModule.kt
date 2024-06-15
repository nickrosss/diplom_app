package com.mycompany.constructioninventory

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.mycompany.constructioninventory.data.LoginRepository
import com.mycompany.constructioninventory.data.LoginRepositoryImpl
import com.mycompany.constructioninventory.data.RentLogRepository
import com.mycompany.constructioninventory.data.RentLogRepositoryImpl
import com.mycompany.constructioninventory.data.ToolsRepository
import com.mycompany.constructioninventory.data.ToolsRepositoryImpl
import com.mycompany.constructioninventory.data.UserRepository
import com.mycompany.constructioninventory.data.UserRepositoryImpl
import com.mycompany.constructioninventory.network.ToolsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(sharedPreferences: SharedPreferences): Retrofit { // Добавьте sharedPreferences
        val authInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            val token = sharedPreferences.getString("user_token", null)
            if (token != null) {
                requestBuilder.addHeader("Authorization", "Token $token")
            }

            // Логирование запроса с заголовками:
            Log.d("AuthInterceptor", "Headers: $token")

            chain.proceed(requestBuilder.build())
        }

        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .build()
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideToolsApi(retrofit: Retrofit): ToolsApi {
        return retrofit.create(ToolsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideToolsRepository(toolsApi: ToolsApi): ToolsRepository {
        return ToolsRepositoryImpl(toolsApi)
    }

    @Provides
    @Singleton
    fun provideLoginRepository(toolsApi: ToolsApi): LoginRepository {
        return LoginRepositoryImpl(toolsApi)
    }

    @Provides
    @Singleton
    fun provideRentLogRepository(toolsApi: ToolsApi): RentLogRepository {
        return RentLogRepositoryImpl(toolsApi)
    }

    @Provides
    @Singleton
    fun provideUserRepository(toolsApi: ToolsApi): UserRepository {
        return UserRepositoryImpl(toolsApi)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
    }
}
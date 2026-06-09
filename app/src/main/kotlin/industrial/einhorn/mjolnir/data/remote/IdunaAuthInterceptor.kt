package industrial.einhorn.mjolnir.data.remote

import industrial.einhorn.mjolnir.data.repository.AuthRepository
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class IdunaAuthInterceptor @Inject constructor(
    private val authRepo: AuthRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = authRepo.getTokenSync()
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}

package com.charitylink.app.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import okhttp3.ResponseBody
import retrofit2.http.DELETE
import retrofit2.http.PUT


/** Defines the HTTP endpoints exposed by the CharityLink backend. */
interface ApiService {
    @POST("api/ai/chat")
    suspend fun chat(@Body body: ChatRequest): ChatResponse
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @GET("api/campaigns")
    suspend fun getCampaigns(): List<Campaign>

    @POST("api/campaigns/{id}/donate")
    suspend fun donate(
        @Path("id") id: Int,
        @Body body: DonateRequest
    ): DonateResponse

    @GET("api/users/{email}/contributions")
    suspend fun getContributions(
        @Path("email") email: String
    ): List<Contribution>

    @POST("api/ai/impact")
    suspend fun getImpact(@Body body: ImpactRequest): ImpactResponse
    @GET("api/admin/fraud-flags")
    suspend fun getFraudFlags(@Query("requesterEmail") email: String): FraudResponse
    @GET("api/analytics")
    suspend fun getAnalytics(): AnalyticsResponse
    @POST("api/campaigns")
    suspend fun createCampaign(@Body body: CampaignInput): ResponseBody

    @PUT("api/campaigns/{id}")
    suspend fun updateCampaign(@Path("id") id: Int, @Body body: CampaignInput): ResponseBody

    @PUT("api/campaigns/{id}/verify")
    suspend fun verifyCampaign(@Path("id") id: Int, @Body body: VerifyRequest): ResponseBody

    @DELETE("api/campaigns/{id}")
    suspend fun deleteCampaign(@Path("id") id: Int, @Query("requesterEmail") email: String): ResponseBody
    @POST("api/mpesa/stkpush")
    suspend fun stkPush(@Body body: StkPushRequest): StkPushResponse

    @GET("api/mpesa/status/{id}")
    suspend fun stkStatus(@Path("id") id: String): StkStatusResponse
}

/** Shared Retrofit client used by the app's ViewModel. */
object ApiClient {

    // 10.0.2.2 points to the development computer from the Android emulator.
    // For a physical phone, replace this with the computer's LAN IP address.
    private const val BASE_URL = "https://vagrancy-unstuffed-washbowl.ngrok-free.dev/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // BODY is useful while developing; avoid shipping verbose logging in production.
        level = HttpLoggingInterceptor.Level.NONE
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(40, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(40, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(40, java.util.concurrent.TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("ngrok-skip-browser-warning", "true")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .build()


    val api: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}

/** Body for creating or editing a campaign. The backend reads requesterEmail to check the role. */
data class CampaignInput(
    val title: String,
    val org: String,
    val desc: String,
    val short: String,
    val target: Int,
    val category: String,
    val icon: String,
    val color: String,
    val requesterEmail: String
)

/** Body for verifying or unverifying a campaign (admin only). */
data class VerifyRequest(
    val verified: Boolean,
    val requesterEmail: String
)
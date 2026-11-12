package com.charitylink.app.data

data class ImpactItem(val num: String, val lbl: String)

data class Campaign(
    val id: Int,
    val icon: String,
    val category: String,
    val title: String,
    val org: String,
    val desc: String,
    val short: String,
    val raised: Int,
    val target: Int,
    val donors: Int,
    val verified: Boolean,
    val impact: List<ImpactItem>,
    val usage: String,
    val color: String,
    val ownerEmail: String? = null
)

data class User(
    val name: String,
    val email: String,
    val role: String,
    val photoUrl: String? = null,
    val memberSince: String? = null,
    val totalContributed: Int = 0,
    val campaignsSupported: Int = 0
)

data class Contribution(
    val id: String,
    val campaignId: Int,
    val campaignTitle: String,
    val category: String,
    val donorName: String,
    val donorEmail: String,
    val amount: Int,
    val timestamp: String,
    val paymentMethod: String
)

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(
    val name: String, val email: String, val password: String,
    val role: String = "donor", val adminCode: String? = null
)
data class AuthResponse(val user: User)
data class DonateRequest(
    val amount: Int, val donorEmail: String?, val donorName: String?,
    val paymentMethod: String
)
data class DonateResponse(val success: Boolean, val campaign: Campaign, val contribution: Contribution)
data class ImpactRequest(val amount: Int, val campaignTitle: String, val category: String)
data class ImpactResponse(val text: String, val source: String)
data class ChatRequest(val message: String, val email: String?)
data class ChatResponse(val reply: String, val source: String)
data class FraudFlag(
    val id: String,
    val severity: String,
    val type: String,
    val title: String,
    val detail: String
)

data class FraudResponse(val flags: List<FraudFlag>, val summary: String?)
data class AnalyticsTotals(
    val raised: Long, val donors: Int, val campaigns: Int,
    val contributions: Int, val users: Int, val verifiedPct: Int
)
data class LabelValue(val label: String, val value: Long)
data class TopCampaign(
    val title: String, val raised: Long, val target: Long,
    val icon: String, val color: String
)
data class DayAmount(val date: String, val amount: Long)
data class AnalyticsResponse(
    val totals: AnalyticsTotals,
    val byCategory: List<LabelValue>,
    val topCampaigns: List<TopCampaign>,
    val overTime: List<DayAmount>
)
data class StkPushRequest(
    val campaignId: Int,
    val amountKES: Int,
    val phone: String,
    val donorEmail: String?,
    val donorName: String?
)
data class StkPushResponse(
    val ok: Boolean?,
    val checkoutRequestId: String,
    val reference: String?,
    val simulated: Boolean?
)
data class StkStatusResponse(val status: String)
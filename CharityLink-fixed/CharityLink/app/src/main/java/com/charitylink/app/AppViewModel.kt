package com.charitylink.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charitylink.app.data.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.delay

class AppViewModel : ViewModel() {
    var user by mutableStateOf<User?>(null)
    var campaigns by mutableStateOf<List<Campaign>>(emptyList())
    var contributions by mutableStateOf<List<Contribution>>(emptyList())
    var loading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    private fun message(e: Exception): String =
        if (e is HttpException) {
            try {
                JSONObject(e.response()?.errorBody()?.string() ?: "").getString("error")
            } catch (x: Exception) { "Request failed (${e.code()})" }
        } else "Cannot reach the server. Is the backend running?"

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            loading = true; error = null
            try {
                user = ApiClient.api.login(LoginRequest(email.trim(), password)).user
                onSuccess()
            } catch (e: Exception) { error = message(e) }
            loading = false
        }
    }

    fun register(name: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            loading = true; error = null
            try {
                user = ApiClient.api.register(RegisterRequest(name.trim(), email.trim(), password)).user
                onSuccess()
            } catch (e: Exception) { error = message(e) }
            loading = false
        }
    }

    fun loadCampaigns() {
        viewModelScope.launch {
            loading = true; error = null
            try { campaigns = ApiClient.api.getCampaigns() }
            catch (e: Exception) { error = message(e) }
            loading = false
        }
    }

    fun donate(campaign: Campaign, amount: Int, onDone: () -> Unit) {
        viewModelScope.launch {
            loading = true; error = null
            try {
                val u = user
                ApiClient.api.donate(
                    campaign.id,
                    DonateRequest(amount, u?.email, u?.name, "Mobile App")
                )
                loadCampaigns()
                onDone()
            } catch (e: Exception) { error = message(e) }
            loading = false
        }
    }

    fun loadContributions() {
        val email = user?.email ?: return
        viewModelScope.launch {
            try { contributions = ApiClient.api.getContributions(email) }
            catch (e: Exception) { error = message(e) }
        }
    }
    val chat = mutableStateListOf<Pair<Boolean, String>>()
    var chatLoading by mutableStateOf(false)

    fun sendChat(text: String) {
        val msg = text.trim()
        if (msg.isEmpty()) return
        chat.add(true to msg)
        viewModelScope.launch {
            chatLoading = true
            try {
                val r = ApiClient.api.chat(ChatRequest(msg, user?.email))
                chat.add(false to r.reply)
            } catch (e: Exception) {
                chat.add(false to message(e))
            }
            chatLoading = false
        }
    }
    var flags by mutableStateOf<List<FraudFlag>>(emptyList())
    var flagSummary by mutableStateOf<String?>(null)

    fun loadFlags() {
        val email = user?.email ?: return
        viewModelScope.launch {
            loading = true; error = null
            try {
                val r = ApiClient.api.getFraudFlags(email)
                flags = r.flags
                flagSummary = r.summary
            } catch (e: Exception) { error = message(e) }
            loading = false
        }
    }
    var analytics by mutableStateOf<AnalyticsResponse?>(null)

    fun loadAnalytics() {
        viewModelScope.launch {
            try { analytics = ApiClient.api.getAnalytics() }
            catch (e: Exception) { error = message(e) }
        }
    }
    var mpesaState by mutableStateOf("idle")   // idle, waiting, success, failed, timeout
    var mpesaMessage by mutableStateOf<String?>(null)

    fun resetMpesa() { mpesaState = "idle"; mpesaMessage = null }

    fun payWithMpesa(campaign: Campaign, amount: Int, phone: String) {
        viewModelScope.launch {
            mpesaState = "waiting"; mpesaMessage = null
            try {
                val u = user
                val push = ApiClient.api.stkPush(
                    StkPushRequest(campaign.id, amount, phone.trim(), u?.email, u?.name)
                )
                var result = "timeout"
                for (i in 1..30) {
                    delay(2000)
                    val s = ApiClient.api.stkStatus(push.checkoutRequestId).status
                    if (s == "completed" || s == "failed") { result = s; break }
                }
                mpesaState = when (result) {
                    "completed" -> "success"
                    "failed" -> "failed"
                    else -> "timeout"
                }
                if (result == "completed") { loadCampaigns(); loadContributions() }
            } catch (e: Exception) {
                mpesaMessage = message(e)
                mpesaState = "failed"
            }
        }
    }
    fun saveCampaign(existingId: Int?, input: CampaignInput, onDone: () -> Unit) {
        val email = user?.email ?: return
        viewModelScope.launch {
            loading = true; error = null
            try {
                val body = input.copy(requesterEmail = email)
                if (existingId == null) ApiClient.api.createCampaign(body)
                else ApiClient.api.updateCampaign(existingId, body)
                loadCampaigns()
                onDone()
            } catch (e: Exception) { error = message(e) }
            loading = false
        }
    }

    fun setVerified(campaign: Campaign, verified: Boolean) {
        val email = user?.email ?: return
        viewModelScope.launch {
            error = null
            try {
                ApiClient.api.verifyCampaign(campaign.id, VerifyRequest(verified, email))
                loadCampaigns()
            } catch (e: Exception) { error = message(e) }
        }
    }

    fun removeCampaign(campaign: Campaign) {
        val email = user?.email ?: return
        viewModelScope.launch {
            error = null
            try {
                ApiClient.api.deleteCampaign(campaign.id, email)
                loadCampaigns()
            } catch (e: Exception) { error = message(e) }
        }
    }
    fun logout() { user = null; contributions = emptyList() }

}
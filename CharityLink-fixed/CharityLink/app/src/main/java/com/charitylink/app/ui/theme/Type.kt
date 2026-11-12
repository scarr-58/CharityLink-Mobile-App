package com.charitylink.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import com.charitylink.app.InterFamily
import com.charitylink.app.PlayfairFamily

private val base = Typography()

val Typography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = PlayfairFamily),
    displayMedium = base.displayMedium.copy(fontFamily = PlayfairFamily),
    displaySmall = base.displaySmall.copy(fontFamily = PlayfairFamily),
    headlineLarge = base.headlineLarge.copy(fontFamily = PlayfairFamily, fontWeight = FontWeight.Bold),
    headlineMedium = base.headlineMedium.copy(fontFamily = PlayfairFamily, fontWeight = FontWeight.Bold),
    headlineSmall = base.headlineSmall.copy(fontFamily = PlayfairFamily, fontWeight = FontWeight.Bold),
    titleLarge = base.titleLarge.copy(fontFamily = PlayfairFamily, fontWeight = FontWeight.Bold),
    titleMedium = base.titleMedium.copy(fontFamily = InterFamily),
    titleSmall = base.titleSmall.copy(fontFamily = InterFamily),
    bodyLarge = base.bodyLarge.copy(fontFamily = InterFamily),
    bodyMedium = base.bodyMedium.copy(fontFamily = InterFamily),
    bodySmall = base.bodySmall.copy(fontFamily = InterFamily),
    labelLarge = base.labelLarge.copy(fontFamily = InterFamily),
    labelMedium = base.labelMedium.copy(fontFamily = InterFamily),
    labelSmall = base.labelSmall.copy(fontFamily = InterFamily)
)
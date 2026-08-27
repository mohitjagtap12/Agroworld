package com.example

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class DiseaseAnalysisResult(
    val isSuccess: Boolean,
    val crop: String,
    val disease: String,
    val confidence: String, // e.g. "High", "Moderate", "Low"
    val severity: String, // e.g. "Low", "Moderate", "High", "Severe", "None", "Unknown"
    val symptoms: List<String>,
    val possibleCauses: List<String>,
    val recommendedAction: List<String>,
    val prevention: List<String>,
    val imageQuality: String, // "good", "adequate", "poor", "unclear"
    val isUnclearOrPoorQuality: Boolean,
    val errorMessage: String? = null,
    val modelName: String = "gemini-3.5-flash"
)

data class SavedDiseaseScan(
    val id: String,
    val farmerId: String = "FARMER_MH_01",
    val cropName: String,
    val diseaseName: String,
    val confidence: String,
    val severity: String,
    val symptoms: List<String>,
    val possibleCauses: List<String>,
    val recommendedAction: List<String>,
    val prevention: List<String>,
    val imageQuality: String,
    val modelName: String = "gemini-3.5-flash",
    val imageBitmap: Bitmap? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val formattedDate: String = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).format(Date())
)

object AiDiseaseService {
    private const val TAG = "AiDiseaseService"
    private const val GEMINI_MODEL = "gemini-3.5-flash"
    private const val GEMINI_ENDPOINT =
        "https://generativelanguage.googleapis.com/v1beta/models/$GEMINI_MODEL:generateContent"

    // OkHttpClient with 60s timeout as mandated in skills for Vision API calls
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Converts a Bitmap into a Base64 JPEG string for the Gemini REST API.
     */
    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        // Resize if too large to ensure fast, reliable network transfer within token constraints
        val maxDimension = 1024
        val scale = if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
            val max = maxOf(bitmap.width, bitmap.height)
            maxDimension.toFloat() / max
        } else 1f

        val scaledBitmap = if (scale < 1f) {
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt(),
                (bitmap.height * scale).toInt(),
                true
            )
        } else {
            bitmap
        }

        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    /**
     * Converts a Uri into a Bitmap safely.
     */
    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading image Uri: ${e.message}")
            null
        }
    }

    /**
     * Calls the REAL Gemini API to analyze the actual uploaded crop image.
     */
    suspend fun analyzeCropImage(
        context: Context,
        bitmap: Bitmap,
        selectedCrop: String
    ): DiseaseAnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key is not configured or is placeholder.")
            return@withContext DiseaseAnalysisResult(
                isSuccess = false,
                crop = selectedCrop,
                disease = "",
                confidence = "",
                severity = "",
                symptoms = emptyList(),
                possibleCauses = emptyList(),
                recommendedAction = emptyList(),
                prevention = emptyList(),
                imageQuality = "unclear",
                isUnclearOrPoorQuality = false,
                errorMessage = "AI disease analysis is temporarily unavailable. Please configure the GEMINI_API_KEY in the Secrets panel."
            )
        }

        val base64Image = try {
            bitmapToBase64(bitmap)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to encode image to Base64", e)
            return@withContext DiseaseAnalysisResult(
                isSuccess = false,
                crop = selectedCrop,
                disease = "",
                confidence = "",
                severity = "",
                symptoms = emptyList(),
                possibleCauses = emptyList(),
                recommendedAction = emptyList(),
                prevention = emptyList(),
                imageQuality = "poor",
                isUnclearOrPoorQuality = true,
                errorMessage = "Invalid image format. Please select or capture another photo."
            )
        }

        val systemInstructionText = """
            You are an agricultural crop disease analysis assistant.
            Analyze the provided crop/leaf image carefully.
            Identify the most likely visible disease, pest, nutrient deficiency, or other problem if possible.
            Do not guess when the image is unclear.
            If the image quality is insufficient or the disease cannot be identified reliably, say so clearly.
            The response must be based on the actual image.
            
            Return a structured JSON object strictly matching this schema:
            {
              "crop": "$selectedCrop",
              "disease": "Disease or problem name (e.g., 'Early Blight', 'Powdery Mildew', or 'Healthy Crop')",
              "confidence": "High" | "Moderate" | "Low",
              "severity": "Low" | "Moderate" | "Severe" | "None" | "Unknown",
              "symptoms": ["Symptom 1", "Symptom 2", ...],
              "possible_causes": ["Cause 1", "Cause 2", ...],
              "recommended_action": ["Action 1", "Action 2", ...],
              "prevention": ["Prevention tip 1", "Prevention tip 2", ...],
              "image_quality": "good" | "adequate" | "poor" | "unclear"
            }
            
            If the image is not a plant/crop, or is too blurry/unclear to identify the problem reliably:
            Set "image_quality" to "poor" or "unclear", "disease" to "Unable to identify problem reliably", "confidence" to "Low".
        """.trimIndent()

        val promptText = "Analyze this $selectedCrop image and provide diagnostic disease assessment in structured JSON."

        try {
            // Build the Gemini API JSON Request Body
            val jsonBody = JSONObject().apply {
                // Contents
                val contentsArray = JSONArray()
                val contentObj = JSONObject()
                val partsArray = JSONArray()

                // Text part
                partsArray.put(JSONObject().put("text", promptText))

                // Image part
                val inlineDataObj = JSONObject().apply {
                    put("mimeType", "image/jpeg")
                    put("data", base64Image)
                }
                partsArray.put(JSONObject().put("inlineData", inlineDataObj))

                contentObj.put("parts", partsArray)
                contentsArray.put(contentObj)
                put("contents", contentsArray)

                // System Instruction
                val systemInstructionObj = JSONObject().apply {
                    val sysParts = JSONArray()
                    sysParts.put(JSONObject().put("text", systemInstructionText))
                    put("parts", sysParts)
                }
                put("systemInstruction", systemInstructionObj)

                // Generation Config with JSON Schema / responseMimeType
                val genConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.1)
                    put("topP", 0.95)
                }
                put("generationConfig", genConfig)
            }

            val url = "$GEMINI_ENDPOINT?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful || responseBody.isBlank()) {
                Log.e(TAG, "Gemini API HTTP ${response.code}: $responseBody")
                return@withContext DiseaseAnalysisResult(
                    isSuccess = false,
                    crop = selectedCrop,
                    disease = "",
                    confidence = "",
                    severity = "",
                    symptoms = emptyList(),
                    possibleCauses = emptyList(),
                    recommendedAction = emptyList(),
                    prevention = emptyList(),
                    imageQuality = "unknown",
                    isUnclearOrPoorQuality = false,
                    errorMessage = "AI disease analysis is temporarily unavailable. Please try again."
                )
            }

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                Log.e(TAG, "Gemini returned no candidates: $responseBody")
                return@withContext DiseaseAnalysisResult(
                    isSuccess = false,
                    crop = selectedCrop,
                    disease = "",
                    confidence = "",
                    severity = "",
                    symptoms = emptyList(),
                    possibleCauses = emptyList(),
                    recommendedAction = emptyList(),
                    prevention = emptyList(),
                    imageQuality = "unknown",
                    isUnclearOrPoorQuality = false,
                    errorMessage = "AI disease analysis is temporarily unavailable. Please try again."
                )
            }

            val candidate = candidates.getJSONObject(0)
            val content = candidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val rawText = parts?.optJSONObject(0)?.optString("text") ?: ""

            if (rawText.isBlank()) {
                return@withContext DiseaseAnalysisResult(
                    isSuccess = false,
                    crop = selectedCrop,
                    disease = "",
                    confidence = "",
                    severity = "",
                    symptoms = emptyList(),
                    possibleCauses = emptyList(),
                    recommendedAction = emptyList(),
                    prevention = emptyList(),
                    imageQuality = "unknown",
                    isUnclearOrPoorQuality = false,
                    errorMessage = "AI disease analysis is temporarily unavailable. Please try again."
                )
            }

            parseGeminiResponse(rawText, selectedCrop)
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "Gemini API request timed out", e)
            DiseaseAnalysisResult(
                isSuccess = false,
                crop = selectedCrop,
                disease = "",
                confidence = "",
                severity = "",
                symptoms = emptyList(),
                possibleCauses = emptyList(),
                recommendedAction = emptyList(),
                prevention = emptyList(),
                imageQuality = "unknown",
                isUnclearOrPoorQuality = false,
                errorMessage = "Request timed out. Please check your internet connection and try again."
            )
        } catch (e: java.io.IOException) {
            Log.e(TAG, "Network IO error during Gemini API call", e)
            DiseaseAnalysisResult(
                isSuccess = false,
                crop = selectedCrop,
                disease = "",
                confidence = "",
                severity = "",
                symptoms = emptyList(),
                possibleCauses = emptyList(),
                recommendedAction = emptyList(),
                prevention = emptyList(),
                imageQuality = "unknown",
                isUnclearOrPoorQuality = false,
                errorMessage = "Internet unavailable or network error. Please check your network and try again."
            )
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during Gemini API call", e)
            DiseaseAnalysisResult(
                isSuccess = false,
                crop = selectedCrop,
                disease = "",
                confidence = "",
                severity = "",
                symptoms = emptyList(),
                possibleCauses = emptyList(),
                recommendedAction = emptyList(),
                prevention = emptyList(),
                imageQuality = "unknown",
                isUnclearOrPoorQuality = false,
                errorMessage = "AI disease analysis is temporarily unavailable. Please try again."
            )
        }
    }

    private fun parseGeminiResponse(rawText: String, defaultCrop: String): DiseaseAnalysisResult {
        return try {
            var cleaned = rawText.trim()
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.removePrefix("```json")
            } else if (cleaned.startsWith("```")) {
                cleaned = cleaned.removePrefix("```")
            }
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.removeSuffix("```")
            }
            cleaned = cleaned.trim()

            val json = JSONObject(cleaned)
            val crop = json.optString("crop", defaultCrop).ifBlank { defaultCrop }
            val disease = json.optString("disease", "Unknown Problem").ifBlank { "Unknown Problem" }
            val confidence = json.optString("confidence", "Moderate").ifBlank { "Moderate" }
            val severity = json.optString("severity", "Moderate").ifBlank { "Moderate" }
            val imageQuality = json.optString("image_quality", "good").lowercase()

            val symptomsList = jsonArrayToList(json.optJSONArray("symptoms"))
            val causesList = jsonArrayToList(json.optJSONArray("possible_causes"))
            val actionList = jsonArrayToList(json.optJSONArray("recommended_action"))
            val preventionList = jsonArrayToList(json.optJSONArray("prevention"))

            val isUnclear = imageQuality == "poor" ||
                    imageQuality == "unclear" ||
                    disease.contains("unidentified", ignoreCase = true) ||
                    disease.contains("unclear", ignoreCase = true) ||
                    disease.contains("unable to identify", ignoreCase = true)

            DiseaseAnalysisResult(
                isSuccess = true,
                crop = crop,
                disease = disease,
                confidence = confidence,
                severity = severity,
                symptoms = symptomsList,
                possibleCauses = causesList,
                recommendedAction = actionList,
                prevention = preventionList,
                imageQuality = imageQuality,
                isUnclearOrPoorQuality = isUnclear,
                modelName = GEMINI_MODEL
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Gemini structured JSON response: ${e.message}", e)
            DiseaseAnalysisResult(
                isSuccess = false,
                crop = defaultCrop,
                disease = "",
                confidence = "",
                severity = "",
                symptoms = emptyList(),
                possibleCauses = emptyList(),
                recommendedAction = emptyList(),
                prevention = emptyList(),
                imageQuality = "unknown",
                isUnclearOrPoorQuality = false,
                errorMessage = "AI disease analysis is temporarily unavailable. Please try again."
            )
        }
    }

    private fun jsonArrayToList(array: JSONArray?): List<String> {
        if (array == null) return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until array.length()) {
            val item = array.optString(i)
            if (item.isNotBlank()) {
                list.add(item)
            }
        }
        return list
    }
}

package com.android.shared.network

sealed class NetworkError(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class HttpError(val statusCode: Int, message: String) : NetworkError("HTTP $statusCode: $message")
    class NetworkException(message: String, cause: Throwable) : NetworkError(message, cause)
    class SerializationError(message: String, cause: Throwable) : NetworkError(message, cause)
    class UnknownError(message: String, cause: Throwable? = null) : NetworkError(message, cause)
}
package com.example.notariax.data.remote.dto

data class PostResponse(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)

data class PostRequest(
    val userId: Int,
    val title: String,
    val body: String
)

data class CommentResponse(
    val id: Int,
    val postId: Int,
    val name: String,
    val email: String,
    val body: String
)

data class AlbumResponse(
    val id: Int,
    val userId: Int,
    val title: String
)

data class PhotoResponse(
    val id: Int,
    val albumId: Int,
    val title: String,
    val url: String,
    val thumbnailUrl: String
)

data class TodoResponse(
    val id: Int,
    val userId: Int,
    val title: String,
    val completed: Boolean
)

data class ExternalUserResponse(
    val id: Int,
    val name: String,
    val email: String
)

package com.example.notariax.data.remote

import com.example.notariax.data.remote.dto.AlbumResponse
import com.example.notariax.data.remote.dto.CommentResponse
import com.example.notariax.data.remote.dto.ExternalUserResponse
import com.example.notariax.data.remote.dto.PhotoResponse
import com.example.notariax.data.remote.dto.PostRequest
import com.example.notariax.data.remote.dto.PostResponse
import com.example.notariax.data.remote.dto.TodoResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ExternalApiService {
    @GET("posts")
    suspend fun getPosts(@Query("_limit") limit: Int = 3): List<PostResponse>

    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: Int): PostResponse

    @GET("comments")
    suspend fun getComments(@Query("_limit") limit: Int = 1): List<CommentResponse>

    @GET("albums")
    suspend fun getAlbums(@Query("_limit") limit: Int = 1): List<AlbumResponse>

    @GET("photos")
    suspend fun getPhotos(
        @Query("albumId") albumId: Int = 1,
        @Query("_limit") limit: Int = 1
    ): List<PhotoResponse>

    @GET("todos")
    suspend fun getTodos(@Query("_limit") limit: Int = 1): List<TodoResponse>

    @GET("users")
    suspend fun getUsers(@Query("_limit") limit: Int = 1): List<ExternalUserResponse>

    @POST("posts")
    suspend fun createPost(@Body request: PostRequest): PostResponse
}

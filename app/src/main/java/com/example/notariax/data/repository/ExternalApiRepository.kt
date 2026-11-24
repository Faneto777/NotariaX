package com.example.notariax.data.repository

import com.example.notariax.data.remote.ExternalApiService
import com.example.notariax.data.remote.dto.PostRequest
import com.example.notariax.data.remote.dto.PostResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class ExternalEndpointResult(
    val route: String,
    val resultSummary: String
)

data class ExternalApiOverview(
    val featuredPost: PostResponse,
    val endpointResults: List<ExternalEndpointResult>
)

class ExternalApiRepository(private val service: ExternalApiService) {

    suspend fun loadOverview(): ExternalApiOverview = coroutineScope {
        val posts = async { service.getPosts() }
        val comments = async { service.getComments() }
        val albums = async { service.getAlbums() }
        val photos = async { service.getPhotos() }
        val todos = async { service.getTodos() }
        val users = async { service.getUsers() }
        val featuredPost = service.getPostById(1)

        ExternalApiOverview(
            featuredPost = featuredPost,
            endpointResults = listOf(
                ExternalEndpointResult("GET /posts", "${posts.await().size} registros recibidos"),
                ExternalEndpointResult("GET /posts/{id}", "Post ${featuredPost.id}: ${featuredPost.title}"),
                ExternalEndpointResult("GET /comments", "${comments.await().size} registro de ejemplo"),
                ExternalEndpointResult("GET /albums", "${albums.await().size} registro de ejemplo"),
                ExternalEndpointResult("GET /photos", "${photos.await().size} registro de ejemplo"),
                ExternalEndpointResult("GET /todos", "${todos.await().size} registro de ejemplo"),
                ExternalEndpointResult("GET /users", "${users.await().size} registro de ejemplo")
            )
        )
    }

    suspend fun createDemoPost(comment: String): PostResponse = service.createPost(
        PostRequest(
            userId = 1,
            title = "Sugerencia para NotaXo",
            body = comment
        )
    )
}

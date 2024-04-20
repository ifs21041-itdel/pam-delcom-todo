package com.ifs21041.delcomtodo.data.remote.retrofit

import com.ifs21041.delcomtodo.data.remote.response.DelcomAddLostFoundResponse
import com.ifs21041.delcomtodo.data.remote.response.DelcomAddTodoResponse
import com.ifs21041.delcomtodo.data.remote.response.DelcomLoginResponse
import com.ifs21041.delcomtodo.data.remote.response.DelcomLostFoundResponse
import com.ifs21041.delcomtodo.data.remote.response.DelcomLostFoundsResponse
import com.ifs21041.delcomtodo.data.remote.response.DelcomResponse
import com.ifs21041.delcomtodo.data.remote.response.DelcomTodoResponse
import com.ifs21041.delcomtodo.data.remote.response.DelcomTodosResponse
import com.ifs21041.delcomtodo.data.remote.response.DelcomUserResponse
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
interface IApiService {
    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): DelcomResponse
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): DelcomLoginResponse
    @GET("users/me")
    suspend fun getMe(): DelcomUserResponse
    @FormUrlEncoded
    @POST("todos")
    suspend fun postTodo(
        @Field("title") title: String,
        @Field("description") description: String,
    ): DelcomAddTodoResponse
    @FormUrlEncoded
    @PUT("todos/{id}")
    suspend fun putTodo(
        @Path("id") todoId: Int,
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("is_finished") isFinished: Int,
    ): DelcomResponse
    @GET("todos")
    suspend fun getTodos(
        @Query("is_finished") isFinished: Int?,
    ): DelcomTodosResponse
    @GET("todos/{id}")
    suspend fun getTodo(
        @Path("id") todoId: Int,
    ): DelcomTodoResponse
    @DELETE("todos/{id}")
    suspend fun deleteTodo(
        @Path("id") todoId: Int,
    ): DelcomResponse
    @FormUrlEncoded
    @POST("lost-founds")
    suspend fun postLostfound(
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("status") status: String,
    ): DelcomAddLostFoundResponse
    @FormUrlEncoded
    @PUT("lost-founds/{id}")
    suspend fun putLostfound(
        @Path("id") lostFoundId: Int,
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("status") status: String,
        @Field("is_completed") isCompleted: Int,
    ): DelcomResponse
    @GET("lost-founds")
    suspend fun getLostfounds(
        @Query("is_completed") isCompleted: Int?,
        @Query("is_me") userId: Int?,
        @Query("status") status: String,
    ): DelcomLostFoundsResponse
    @GET("lost-founds/{id}")
    suspend fun getLostfound(
        @Path("id") lostFoundId: Int,
    ): DelcomLostFoundResponse
    @DELETE("lost-founds/{id}")
    suspend fun deleteLostfound(
        @Path("id") lostFoundId: Int,
    ): DelcomResponse
}
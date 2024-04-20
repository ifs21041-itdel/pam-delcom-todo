package com.ifs21041.delcomtodo.data.repository

import com.google.gson.Gson
import com.ifs21041.delcomtodo.data.remote.MyResult
import com.ifs21041.delcomtodo.data.remote.response.DelcomResponse
import com.ifs21041.delcomtodo.data.remote.retrofit.IApiService
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
class LostRepository private constructor(
    private val apiService: IApiService,
) {
    fun postLostfound(
        title: String,
        description: String,
        status:String,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(
                MyResult.Success(
                    apiService.postLostfound(title, description, status).data
                )
            )
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, DelcomResponse::class.java)
                        .message
                )
            )
        }
    }
    fun putLostfound(
        lostFoundId: Int,
        title: String,
        description: String,
        status: String,
        isCompleted: Boolean,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(
                MyResult.Success(
                    apiService.putLostfound(
                        lostFoundId,
                        title,
                        description,
                        status,
                        if (isCompleted) 1 else 0
                    )
                )
            )
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, DelcomResponse::class.java)
                        .message
                )
            )
        }
    }
    fun getLostfounds(
        isCompleted: Int?,
        userId: Int?,
        status: String,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(MyResult.Success(apiService.getLostfounds(
                isCompleted,
                userId,
                status,)))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, DelcomResponse::class.java)
                        .message
                )
            )
        }
    }
    fun getLostfound(
        lostFoundId: Int,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(MyResult.Success(apiService.getLostfound(lostFoundId)))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, DelcomResponse::class.java)
                        .message
                )
            )
        }
    }
    fun deleteLostfound(
        lostFoundId: Int,
    ) = flow {
        emit(MyResult.Loading)
        try {
//get success message
            emit(MyResult.Success(apiService.deleteLostfound(lostFoundId)))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, DelcomResponse::class.java)
                        .message
                )
            )
        }
    }
    companion object {
        @Volatile
        private var INSTANCE: LostRepository? = null
        fun getInstance(
            apiService: IApiService,
        ): LostRepository {
            synchronized(LostRepository::class.java) {
                INSTANCE = LostRepository(
                    apiService
                )
            }
            return INSTANCE as LostRepository
        }
    }
}

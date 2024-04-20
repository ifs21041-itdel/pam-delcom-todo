package com.ifs21041.delcomtodo.presentation.lostandfound

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ifs21041.delcomtodo.ViewModelFactory
import com.ifs21041.delcomtodo.data.remote.MyResult
import com.ifs21041.delcomtodo.data.remote.response.DataAddLostFoundResponse
import com.ifs21041.delcomtodo.data.remote.response.DelcomLostFoundResponse
import com.ifs21041.delcomtodo.data.remote.response.DelcomLostFoundsResponse
import com.ifs21041.delcomtodo.data.remote.response.DelcomResponse
import com.ifs21041.delcomtodo.data.repository.LostRepository

class LostfoundViewModel(
    private val lostRepository: LostRepository
) : ViewModel() {
    fun getLostfounds(
        isCompleted: Int?,
        userId: Int?,
        status: String,
    ): LiveData<MyResult<DelcomLostFoundsResponse>> {
        return lostRepository.getLostfounds(isCompleted,userId,status, ).asLiveData()
    }
    fun getLostfound(lostFoundId: Int): LiveData<MyResult<DelcomLostFoundResponse>>{
        return lostRepository.getLostfound(lostFoundId).asLiveData()
    }
    fun postLostfound(
        title: String,
        description: String,
        status: String,
    ): LiveData<MyResult<DataAddLostFoundResponse>>{
        return lostRepository.postLostfound(
            title,
            description,
            status
        ).asLiveData()
    }
    fun putLostfound(
        lostFoundId: Int,
        title: String,
        description: String,
        status: String,
        isCompleted: Boolean,
    ): LiveData<MyResult<DelcomResponse>> {
        return lostRepository.putLostfound(
            lostFoundId,
            title,
            description,
            status,
            isCompleted,
        ).asLiveData()
    }
    fun deleteLostfound(lostFoundId: Int): LiveData<MyResult<DelcomResponse>> {
        return lostRepository.deleteLostfound(lostFoundId).asLiveData()
    }
    companion object {
        @Volatile
        private var INSTANCE: LostfoundViewModel? = null
        fun getInstance(
            lostRepository: LostRepository
        ): LostfoundViewModel {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = LostfoundViewModel(
                    lostRepository
                )
            }
            return INSTANCE as LostfoundViewModel
        }
    }
}

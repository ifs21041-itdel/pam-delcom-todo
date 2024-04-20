package com.ifs21041.delcomtodo.data.remote.response

import com.google.gson.annotations.SerializedName

data class DelcomTodoResponse(

	@field:SerializedName("data")
	val data: DataTodoResponse,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class TodoResponse(

	@field:SerializedName("cover")
	val cover: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("is_finished")
	val isFinished: Int
)

data class DataTodoResponse(

	@field:SerializedName("todo")
	val todo: TodoResponse
)

package com.ifs21041.delcomtodo.presentation.todo
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ifs21041.delcomtodo.R
import com.ifs21041.delcomtodo.ViewModelFactory
import com.ifs21041.delcomtodo.data.local.entity.DelcomTodoEntity
import com.ifs21041.delcomtodo.data.model.DelcomTodo
import com.ifs21041.delcomtodo.data.remote.MyResult
import com.ifs21041.delcomtodo.data.remote.response.TodoResponse
import com.ifs21041.delcomtodo.databinding.ActivityTodoDetailBinding
import com.ifs21041.delcomtodo.helper.Utils.Companion.observeOnce

class TodoDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTodoDetailBinding
    private val viewModel by viewModels<TodoViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var isChanged: Boolean = false

    private var isFavorite: Boolean = false
    private var delcomTodo: DelcomTodoEntity? = null
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == TodoManageActivity.RESULT_CODE) {
            recreate()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
    }
    private fun setupView() {
        showComponent(false)
        showLoading(false)
    }
    private fun setupAction() {
        val todoId = intent.getIntExtra(KEY_TODO_ID, 0)
        if (todoId == 0) {
            finish()
            return
        }
        observeGetTodo(todoId)
        binding.appbarTodoDetail.setNavigationOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra(KEY_IS_CHANGED, isChanged)
            setResult(RESULT_CODE, resultIntent)
            finishAfterTransition()
        }
    }
    private fun observeGetTodo(todoId: Int) {
        viewModel.getTodo(todoId).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    loadTodo(result.data.data.todo)
                }
                is MyResult.Error -> {
                    Toast.makeText(
                        this@TodoDetailActivity,
                        result.error,
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                    finishAfterTransition()
                }
            }
        }
    }
    private fun loadTodo(todo: TodoResponse) {
        showComponent(true)
        binding.apply {
            tvTodoDetailTitle.text = todo.title
            tvTodoDetailDate.text = "Created at: ${todo.createdAt}"
            tvTodoDetailDesc.text = todo.description
            cbTodoDetailIsFinished.isChecked = todo.isFinished == 1
            cbTodoDetailIsFinished.setOnCheckedChangeListener { _, isChecked ->
                viewModel.putTodo(
                    todo.id,
                    todo.title,
                    todo.description,
                    isChecked
                ).observeOnce {
                    when (it) {
                        is MyResult.Error -> {
                            if (isChecked) {
                                Toast.makeText(
                                    this@TodoDetailActivity,
                                    "Failed to complete todo: " + todo.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@TodoDetailActivity,
                                    "Failed to cancel completing todo: " + todo.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is MyResult.Success -> {
                            if (isChecked) {
                                Toast.makeText(
                                    this@TodoDetailActivity,
                                    "Successfully completed todo: " + todo.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@TodoDetailActivity,
                                    "Successfully canceled completing todo.: " + todo.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            if ((todo.isFinished == 1) != isChecked) {
                                isChanged = true
                            }
                        }
                        else -> {}
                    }
                }
            }
            ivTodoDetailActionFavorite.setOnClickListener {
                if(isFavorite){
                    setFavorite(false)
                    if(delcomTodo != null){
                        viewModel.deleteLocalTodo(delcomTodo!!)
                    }

                    Toast.makeText(
                        this@TodoDetailActivity,
                        "Todo berhasil dihapus dari daftar favorite",
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    delcomTodo = DelcomTodoEntity(
                        id = todo.id,
                        title = todo.title,
                        description = todo.description,
                        isFinished = todo.isFinished,
                        cover = todo.cover,
                        createdAt = todo.createdAt,
                        updatedAt = todo.updatedAt,
                    )

                    setFavorite(true)
                    viewModel.insertLocalTodo(delcomTodo!!)

                    Toast.makeText(
                        this@TodoDetailActivity,
                        "Todo berhasil ditambahkan ke daftar favorite",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            ivTodoDetailActionDelete.setOnClickListener {
                val builder = AlertDialog.Builder(this@TodoDetailActivity)
                builder.setTitle("Confirm Delete Todo")
                    .setMessage("Are you sure you want to delete this todo?")
                builder.setPositiveButton("YES") { _, _ ->
                    observeDeleteTodo(todo.id)
                }
                builder.setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss() // Menutup dialog
                }
                val dialog = builder.create()
                dialog.show()
            }
            ivTodoDetailActionEdit.setOnClickListener {
                val delcomTodo = DelcomTodo(
                    todo.id,
                    todo.title,
                    todo.description,
                    todo.isFinished == 1,
                    todo.cover
                )
                val intent = Intent(
                    this@TodoDetailActivity,
                    TodoManageActivity::class.java
                )
                intent.putExtra(TodoManageActivity.KEY_IS_ADD, false)
                intent.putExtra(TodoManageActivity.KEY_TODO, delcomTodo)
                launcher.launch(intent)
            }
        }
    }
    private fun setFavorite(status: Boolean){
        isFavorite = status
        if(status){
            binding.ivTodoDetailActionFavorite
                .setImageResource(R.drawable.ic_favorite_24)
        }else{
            binding.ivTodoDetailActionFavorite
                .setImageResource(R.drawable.ic_favorite_border_24)
        }
    }

    private fun observeDeleteTodo(todoId: Int) {
        showComponent(false)
        showLoading(true)
        viewModel.deleteTodo(todoId).observeOnce {
            when (it) {
                is MyResult.Error -> {
                    showComponent(true)
                    showLoading(false)
                    Toast.makeText(
                        this@TodoDetailActivity,
                        "Failed to delete todo: ${it.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is MyResult.Success -> {
                    showLoading(false)
                    Toast.makeText(
                        this@TodoDetailActivity,
                        "Successfully deleted todo",
                        Toast.LENGTH_SHORT
                    ).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra(KEY_IS_CHANGED, true)
                    setResult(RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }
                else -> {}
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbTodoDetail.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showComponent(status: Boolean) {
        binding.llTodoDetail.visibility =
            if (status) View.VISIBLE else View.GONE
    }
    companion object {
        const val KEY_TODO_ID = "todo_id"
        const val KEY_IS_CHANGED = "is_changed"
        const val RESULT_CODE = 1001
    }
}
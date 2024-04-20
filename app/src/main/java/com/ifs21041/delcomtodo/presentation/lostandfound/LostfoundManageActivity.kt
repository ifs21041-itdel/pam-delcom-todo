package com.ifs21041.delcomtodo.presentation.lostandfound

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ifs21041.delcomtodo.ViewModelFactory
import com.ifs21041.delcomtodo.data.model.DelcomLost
import com.ifs21041.delcomtodo.data.remote.MyResult
import com.ifs21041.delcomtodo.databinding.ActivityLostfoundManageBinding
import com.ifs21041.delcomtodo.helper.Utils.Companion.observeOnce

class LostfoundManageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLostfoundManageBinding
    private val viewModel by viewModels<LostfoundViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostfoundManageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAtion()
    }
    private fun setupView() {
        showLoading(false)
    }
    private fun setupAtion() {
        val isAddLostfound = intent.getBooleanExtra(KEY_IS_ADD, true)
        if (isAddLostfound) {
            manageAddTodo()
        } else {
            val delcomLosts = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    intent.getParcelableExtra(KEY_LOST, DelcomLost::class.java)
                }
                else -> {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra<DelcomLost>(KEY_LOST)
                }
            }
            if (delcomLosts == null) {
                finishAfterTransition()
                return
            }
            manageEditTodo(delcomLosts)
        }
        binding.appbarLFManage.setNavigationOnClickListener {
            finishAfterTransition()
        }
    }
    private fun manageAddTodo() {
        binding.apply {
            btnLFSave.setOnClickListener {
                val title = etLFTitle.text.toString()
                val description = etLFDesc.text.toString()
                val status = etStatus.text.toString()
                if (title.isEmpty() || description.isEmpty()) {
                    AlertDialog.Builder(this@LostfoundManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage("No empty data allowed!")
                        setPositiveButton("Understand") { _, _ -> }
                        create()
                        show()
                    }
                    return@setOnClickListener
                }
                observePostTodo(title, description,status)
            }
        }
    }
    private fun observePostTodo(title: String, description: String, status: String) {
        viewModel.postLostfound(title, description, status).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    val resultIntent = Intent()
                    setResult(RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }
                is MyResult.Error -> {
                    AlertDialog.Builder(this@LostfoundManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage(result.error)
                        setPositiveButton("OK") { _, _ -> }
                        create()
                        show()
                    }
                    showLoading(false)
                }
            }
        }
    }
    private fun manageEditTodo(lostfound: DelcomLost) {
        binding.apply {
            appbarLFManage.title = "Change Lost and Found"
            etLFTitle.setText(lostfound.title)
            etLFDesc.setText(lostfound.description)
            etStatus.setText(lostfound.status)
            btnLFSave.setOnClickListener {
                val title = etLFTitle.text.toString()
                val description = etLFDesc.text.toString()
                if (title.isEmpty() || description.isEmpty()) {
                    AlertDialog.Builder(this@LostfoundManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage("No empty data allowed!")
                        setPositiveButton("OK") { _, _ -> }
                        create()
                        show()
                    }
                    return@setOnClickListener
                }
                observePutTodo(lostfound.id, title, description, lostfound.status, lostfound.isComplete)
            }
        }
    }
    private fun observePutTodo(
        lostFoundId: Int,
        title: String,
        description: String,
        status: String,
        isCompleted: Boolean,
    ) {
        viewModel.putLostfound(
            lostFoundId,
            title,
            description,
            status,
            isCompleted
        ).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    val resultIntent = Intent()
                    setResult(RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }
                is MyResult.Error -> {
                    AlertDialog.Builder(this@LostfoundManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage(result.error)
                        setPositiveButton("OK") { _, _ -> }
                        create()
                        show()
                    }
                    showLoading(false)
                }
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbLF.visibility =
            if (isLoading) View.VISIBLE else View.GONE

        binding.btnLFSave.isActivated = !isLoading

        binding.btnLFSave.text =
            if (isLoading) "" else "Save"
    }
    companion object {
        const val KEY_IS_ADD = "is_add"
        const val KEY_LOST = "lost"
        const val RESULT_CODE = 1002
    }
}
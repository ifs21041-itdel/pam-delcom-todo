package com.ifs21041.delcomtodo.presentation.lostandfound

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ifs21041.delcomtodo.ViewModelFactory
import com.ifs21041.delcomtodo.data.model.DelcomLost
import com.ifs21041.delcomtodo.data.remote.MyResult
import com.ifs21041.delcomtodo.data.remote.response.LostFoundResponse
import com.ifs21041.delcomtodo.databinding.ActivityLostfoundDetailBinding
import com.ifs21041.delcomtodo.helper.Utils.Companion.observeOnce

class LostfoundDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLostfoundDetailBinding
    private val viewModel by viewModels<LostfoundViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var isChanged: Boolean = false
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == LostfoundManageActivity.RESULT_CODE) {
            recreate()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostfoundDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
    }
    private fun setupView() {
        showComponent(false)
        showLoading(false)
    }
    private fun setupAction() {
        val lostFoundId = intent.getIntExtra(KEY_LOSTFOUND_ID, 0)
        if (lostFoundId == 0) {
            finish()
            return
        }
        observeGetTodo(lostFoundId)
        binding.appbarTodoDetail.setNavigationOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra(KEY_IS_CHANGED, isChanged)
            setResult(RESULT_CODE, resultIntent)
            finishAfterTransition()
        }
    }
    private fun observeGetTodo(lostFoundId: Int) {
        viewModel.getLostfound(lostFoundId).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    loadTodo(result.data.data.lostFound)
                }
                is MyResult.Error -> {
                    Toast.makeText(
                        this@LostfoundDetailActivity,
                        result.error,
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                    finishAfterTransition()
                }
            }
        }
    }
    private fun loadTodo(lostfound: LostFoundResponse) {
        showComponent(true)
        binding.apply {
            tvLFDetailTitle.text = lostfound.title
            tvLFDetailDate.text = "Made at: ${lostfound.createdAt}"
            tvLFDetailStatus.text = "Status : ${lostfound.status}"
            tvLFDetailDesc.text = lostfound.description
            cbLFDetailIsCompleted.isChecked = lostfound.isCompleted == 1
            cbLFDetailIsCompleted.setOnCheckedChangeListener { _, isChecked ->
                viewModel.putLostfound(
                    lostfound.id,
                    lostfound.title,
                    lostfound.description,
                    lostfound.status,
                    isChecked,
                ).observeOnce {
                    when (it) {
                        is MyResult.Error -> {
                            if (isChecked) {
                                Toast.makeText(
                                    this@LostfoundDetailActivity,
                                    "Failed to complete: " + lostfound.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@LostfoundDetailActivity,
                                    "Failed to cancel completion: " + lostfound.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is MyResult.Success -> {
                            if (isChecked) {
                                Toast.makeText(
                                    this@LostfoundDetailActivity,
                                    "Successfully completed: " + lostfound.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@LostfoundDetailActivity,
                                    "Successfully canceled: " + lostfound.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            if ((lostfound.isCompleted == 1) != isChecked) {
                                isChanged = true
                            }
                        }
                        else -> {}
                    }
                }
            }
            ivLFDetailActionDelete.setOnClickListener {
                val builder = AlertDialog.Builder(this@LostfoundDetailActivity)
                builder.setTitle("Confirm delete todo.")
                    .setMessage("Are you sure you want to delete this todo?")
                builder.setPositiveButton("Ya") { _, _ ->
                    observeDeleteTodo(lostfound.id)
                }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss() // Menutup dialog
                }
                val dialog = builder.create()
                dialog.show()
            }
            ivLFDetailActionEdit.setOnClickListener {
                val delcomLost = DelcomLost(
                    lostfound.id,
                    lostfound.userId,
                    lostfound.title,
                    lostfound.description,
                    lostfound.status,
                    lostfound.isCompleted == 1,
                )
                val intent = Intent(
                    this@LostfoundDetailActivity,
                    LostfoundManageActivity::class.java
                )
                intent.putExtra(LostfoundManageActivity.KEY_IS_ADD, false)
                intent.putExtra(LostfoundManageActivity.KEY_LOST, delcomLost)
                launcher.launch(intent)
            }
        }
    }
    private fun observeDeleteTodo(lostFoundId: Int) {
        showComponent(false)
        showLoading(true)
        viewModel.deleteLostfound(lostFoundId).observeOnce {
            when (it) {
                is MyResult.Error -> {
                    showComponent(true)
                    showLoading(false)
                    Toast.makeText(
                        this@LostfoundDetailActivity,
                        "Failed to delete todo: ${it.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is MyResult.Success -> {
                    showLoading(false)
                    Toast.makeText(
                        this@LostfoundDetailActivity,
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
        binding.pbLFDetail.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showComponent(status: Boolean) {
        binding.llLFDetail.visibility =
            if (status) View.VISIBLE else View.GONE
    }
    companion object {
        const val KEY_LOSTFOUND_ID = "lost_id"
        const val KEY_IS_CHANGED = "is_changed"
        const val RESULT_CODE = 1001
    }
}
package com.guhungry.photomanipulator.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.guhungry.photomanipulator.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ExampleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupViewModel()
        setupRecyclerView()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(ExampleViewModel::class.java)
        viewModel.application = application
    }

    private fun setupView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private val exampleAdapter = ExampleRecyclerViewAdapter(this@MainActivity)

    private fun setupRecyclerView() {
        binding.listExamples.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = exampleAdapter
        }

        viewModel.getExamples().observe(this) {
            exampleAdapter.setItem(it)
        }
    }
}

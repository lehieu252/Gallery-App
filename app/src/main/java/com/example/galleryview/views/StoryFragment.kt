package com.example.galleryview.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.galleryview.R
import com.example.galleryview.adapters.StoryAdapter
import com.example.galleryview.databinding.FragmentStoryBinding
import com.example.galleryview.viewmodels.MainViewModel

class StoryFragment : Fragment() {
    private lateinit var binding: FragmentStoryBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_story, container, false)
        val adapter = context?.let { StoryAdapter(it) }
        context?.let { viewModel.getAllItemView(it) }
        viewModel.itemView.observe(viewLifecycleOwner,{
            if (adapter != null) {
                val size = if(it.size > 10){
                    10
                }
                else{
                    it.size
                }
                adapter.data = it.subList(0,size)
            }
        })
        binding.recyclerView.adapter = adapter
        onMenuClickItem()
        return binding.root
    }


    private fun onMenuClickItem() {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.sEdit -> {
                    Toast.makeText(context, "This feature is being developed", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.sCreate -> {
                    Toast.makeText(context, "This feature is being developed", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.sSearch -> {
                    Toast.makeText(context, "This feature is being developed", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}
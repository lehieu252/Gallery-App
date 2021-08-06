package com.example.galleryview.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galleryview.R
import com.example.galleryview.adapters.ItemAdapter
import com.example.galleryview.databinding.FragmentPictureBinding
import com.example.galleryview.viewmodels.MainViewModel
import com.example.galleryview.viewmodels.MainViewModelFactory

class PictureFragment : Fragment() {
    private lateinit var binding: FragmentPictureBinding
    private val viewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_picture, container, false)
        setUpGridView()
        return binding.root
    }

    private fun setUpGridView(){
        val itemAdapter = activity?.let { ItemAdapter(it) }
        val layoutManager = GridLayoutManager(activity, 4)
        binding.gridView.layoutManager = layoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (itemAdapter!!.isHeader(position)) {
                    return layoutManager.spanCount
                } else return 1
            }
        }
        binding.gridView.adapter = itemAdapter;
        viewModel.itemList.observe(viewLifecycleOwner, Observer {
            if (itemAdapter != null) {
                itemAdapter.data = it
            }
            Log.d("List", it.toString())
        })
    }
}
package com.example.galleryview.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
    private lateinit var viewModel: MainViewModel
    private lateinit var viewModelFactory: MainViewModelFactory
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelFactory = MainViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel.getAllItems(requireContext())

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_picture, container, false)
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
            Log.d("List Image: ", it.toString())
            if (itemAdapter != null) {
                itemAdapter.data = it
            }
        })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        context?.let { viewModel.getAllItems(it) }
    }
}
package com.example.galleryview.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galleryview.R
import com.example.galleryview.adapters.AlbumAdapter
import com.example.galleryview.databinding.FragmentAlbumBinding
import com.example.galleryview.viewmodels.MainViewModel

class AlbumFragment : Fragment() {
    private lateinit var binding: FragmentAlbumBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_album, container, false)
        showAlbums()
        viewModel.hideBottomNav.observe(viewLifecycleOwner,{
            if(it){
                viewModel.showBottomNavigation()
            }
        })
        return binding.root
    }

    private fun showAlbums() {
        context?.let { viewModel.getAllAlbums(it) }
        val adapter = context?.let { AlbumAdapter(it) }
        val layoutManager = GridLayoutManager(context, 3)
        binding.gridView.layoutManager = layoutManager
        viewModel.albums.observe(viewLifecycleOwner, {
            if (adapter != null) {
                adapter.data = it
            }
        })
        binding.gridView.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        context?.let { viewModel.getAllAlbums(it) }
    }
}
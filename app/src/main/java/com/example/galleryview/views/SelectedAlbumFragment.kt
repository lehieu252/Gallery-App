package com.example.galleryview.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galleryview.R
import com.example.galleryview.adapters.AlbumAdapter
import com.example.galleryview.databinding.FragmentSelectedAlbumBinding
import com.example.galleryview.models.Album
import com.example.galleryview.viewmodels.MainViewModel

class SelectedAlbumFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentSelectedAlbumBinding
    private lateinit var adapter: AlbumAdapter

    companion object {
        const val TYPE_SELECT = 3
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_selected_album, container, false)
        binding.topAppBar.title = "Choose album"
        showAlbums()
        return binding.root
    }

    private fun showAlbums() {
        context?.let { viewModel.getAllAlbums(it) }
        adapter = context?.let { AlbumAdapter(it, TYPE_SELECT) }!!
        val layoutManager = GridLayoutManager(context, 3)
        binding.gridView.layoutManager = layoutManager
        viewModel.albums.observe(viewLifecycleOwner, {
            adapter.data = it
        })
        binding.gridView.adapter = adapter
        adapter.setItemClick(object : AlbumAdapter.ItemClick {
            override fun onItemClick(view: View, position: Int, album: Album) {
                if(adapter.type == TYPE_SELECT){
                    Toast.makeText(context,"Select ${album.name}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
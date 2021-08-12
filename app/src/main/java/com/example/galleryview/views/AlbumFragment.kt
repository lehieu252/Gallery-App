package com.example.galleryview.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galleryview.R
import com.example.galleryview.adapters.AlbumAdapter
import com.example.galleryview.databinding.FragmentAlbumBinding
import com.example.galleryview.models.Album
import com.example.galleryview.utils.AppUtil
import com.example.galleryview.viewmodels.MainViewModel

class AlbumFragment : Fragment() {
    private lateinit var binding: FragmentAlbumBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: AlbumAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_album, container, false)
        showAlbums()
        viewModel.hideBottomNav.observe(viewLifecycleOwner, {
            if (it) {
                viewModel.showBottomNavigation()
            }
        })
        return binding.root
    }

    private fun showAlbums() {
        context?.let { viewModel.getAllAlbums(it) }
        adapter = context?.let { AlbumAdapter(it, AppUtil.TYPE_VIEW) }!!
        val layoutManager = GridLayoutManager(context, 3)
        binding.gridView.layoutManager = layoutManager
        viewModel.albums.observe(viewLifecycleOwner, {
            adapter.data = it
        })
        binding.gridView.adapter = adapter
        adapter.setItemClick(object : AlbumAdapter.ItemClick {
            override fun onItemClick(view: View, position: Int, album: Album) {
                if(adapter.type == AppUtil.TYPE_VIEW){
                    val bundle = Bundle()
                    bundle.putString("album_name",album.name)
                    findNavController().navigate(R.id.action_albumFragment_to_albumViewFragment,bundle)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        context?.let { viewModel.getAllAlbums(it) }
    }
}
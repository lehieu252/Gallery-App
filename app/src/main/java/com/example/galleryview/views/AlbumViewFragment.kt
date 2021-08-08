package com.example.galleryview.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galleryview.R
import com.example.galleryview.adapters.ItemAdapter
import com.example.galleryview.databinding.FragmentAlbumViewBinding
import com.example.galleryview.viewmodels.AlbumViewModel
import com.example.galleryview.viewmodels.AlbumViewModelFactory
import com.example.galleryview.viewmodels.MainViewModel

class AlbumViewFragment : Fragment() {
    private lateinit var binding: FragmentAlbumViewBinding
    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var albumViewModelFactory: AlbumViewModelFactory
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var bundle: Bundle
    private lateinit var albumName: String

    companion object {
        const val TYPE_ALBUM_FRAGMENT = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_album_view, container, false)
        bundle = requireArguments()
        albumName = bundle.getString("album_name").toString()
        mainViewModel.hideBottomNavigation()
        albumViewModelFactory = AlbumViewModelFactory(albumName)
        albumViewModel =
            ViewModelProvider(this, albumViewModelFactory).get(AlbumViewModel::class.java)
        context?.let { albumViewModel.getItemsByAlbum(it) }

        setUpToolBar()
        showItem()

        return binding.root
    }

    private fun setUpToolBar() {
        albumViewModel.album.observe(viewLifecycleOwner, {
            binding.topAppBar.title = it.name
            binding.topAppBar.subtitle = "${it.imagesCount} images ${it.videosCount} videos"
//            (activity as AppCompatActivity).setSupportActionBar(binding.topAppBar)
        })
    }

    private fun showItem() {
        val adapter = context?.let { ItemAdapter(it, TYPE_ALBUM_FRAGMENT) }
        if (adapter != null) {
            adapter.albumName = albumName
        }
        val layoutManager = GridLayoutManager(context, 4)
        binding.gridView.layoutManager = layoutManager
        albumViewModel.itemList.observe(viewLifecycleOwner, {
            if (adapter != null) {
                adapter.data = it
                binding.gridView.adapter = adapter
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.showBottomNavigation()
    }
}
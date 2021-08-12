package com.example.galleryview.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galleryview.R
import com.example.galleryview.adapters.AlbumAdapter
import com.example.galleryview.databinding.FragmentSelectedAlbumBinding
import com.example.galleryview.models.Album
import com.example.galleryview.utils.AppUtil
import com.example.galleryview.viewmodels.MainViewModel

class SelectedAlbumFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentSelectedAlbumBinding
    private lateinit var adapter: AlbumAdapter
    private lateinit var bundle: Bundle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_selected_album, container, false)
        showAlbums()
        return binding.root
    }

    private fun showAlbums() {
        bundle = requireArguments()
        val selectMode = bundle.getInt("select_mode")
        Log.d("select_mode", selectMode.toString())
        if (selectMode == AppUtil.MODE_COPY) {
            binding.topAppBar.title = "Choose album to copy"
        } else if (selectMode == AppUtil.MODE_MOVE) {
            binding.topAppBar.title = "Choose album to move"
        }
        context?.let { viewModel.getAllAlbums(it) }
        adapter = context?.let { AlbumAdapter(it, AppUtil.TYPE_SELECT) }!!
        val layoutManager = GridLayoutManager(context, 3)
        binding.gridView.layoutManager = layoutManager
        viewModel.albums.observe(viewLifecycleOwner, {
            adapter.data = it
        })
        binding.gridView.adapter = adapter
        adapter.setItemClick(object : AlbumAdapter.ItemClick {
            override fun onItemClick(view: View, position: Int, album: Album) {
                if (adapter.type == AppUtil.TYPE_SELECT) {
                    if (selectMode == AppUtil.MODE_COPY) {
                        viewModel.copySelectedItems(context!!, viewModel.selectedList, album)
                    } else if (selectMode == AppUtil.MODE_MOVE) {
                        viewModel.moveSelectedItems(context!!, viewModel.selectedList, album)
                    }
                    Toast.makeText(context, "Selected ${album.name}", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_selectedAlbumFragment_to_pictureFragment)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.showBottomNavigation()
    }
}
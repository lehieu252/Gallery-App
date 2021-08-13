package com.example.galleryview.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.galleryview.R
import com.example.galleryview.adapters.ViewPAdapter
import com.example.galleryview.databinding.FragmentViewBinding
import com.example.galleryview.utilities.AppUtil
import com.example.galleryview.viewmodels.AlbumViewModel
import com.example.galleryview.viewmodels.MainViewModel

class ViewFragment : Fragment() {
    private lateinit var binding: FragmentViewBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var albumViewModel: AlbumViewModel

    private lateinit var viewPagerAdapter: ViewPAdapter
    private lateinit var bundle: Bundle
    private var type = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view, container, false)
        bundle = requireArguments()
        showItemView()
        return binding.root
    }

    private fun showItemView() {
        type = bundle.getInt("type")
        val curPos = bundle.getInt("position")
        viewPagerAdapter = activity?.let { ViewPAdapter(it) }!!
        if (type == AppUtil.FRAGMENT_PICTURE) {
            viewModel.hideBottomNavigation()
            context?.let { viewModel.getAllItemView(it) }
            viewModel.itemView.observe(viewLifecycleOwner, Observer {
                viewPagerAdapter.data = it
                binding.viewPager.adapter = viewPagerAdapter
                binding.viewPager.setCurrentItem(curPos, false)
            })
        } else if (type == AppUtil.FRAGMENT_ALBUM) {
            albumViewModel = ViewModelProvider(this).get(AlbumViewModel::class.java)
            val albumName = bundle.getString("album_name")
            context?.let {
                if (albumName != null) {
                    albumViewModel.getItemsByAlbum(it,albumName)
                }
            }
            albumViewModel.itemListByAlbum.observe(viewLifecycleOwner, {
                viewPagerAdapter.data = it
                binding.viewPager.adapter = viewPagerAdapter
                binding.viewPager.setCurrentItem(curPos, false)
            })
        }
    }

}



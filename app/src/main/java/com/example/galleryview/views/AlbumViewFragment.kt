package com.example.galleryview.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galleryview.R
import com.example.galleryview.adapters.ItemAdapter
import com.example.galleryview.adapters.OnItemClick
import com.example.galleryview.databinding.FragmentAlbumViewBinding
import com.example.galleryview.models.Item
import com.example.galleryview.utilities.AppUtil
import com.example.galleryview.viewmodels.AlbumViewModel
import com.example.galleryview.viewmodels.MainViewModel

class AlbumViewFragment : Fragment() {
    private lateinit var binding: FragmentAlbumViewBinding
    private lateinit var albumViewModel: AlbumViewModel
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var bundle: Bundle
    private lateinit var albumName: String
    private lateinit var itemAdapter: ItemAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_album_view, container, false)
        bundle = requireArguments()
        albumName = bundle.getString("album_name").toString()
        if(mainViewModel.hideBottomNav.value != true) {
            mainViewModel.hideBottomNavigation()
        }
        albumViewModel =
            ViewModelProvider(this).get(AlbumViewModel::class.java)
        context?.let { albumViewModel.getItemsByAlbum(it, albumName) }

        setUpToolBar()
        showItem()
        onclickFunctionNavigation()
        onMenuClickItem()
        albumViewModel.hideFunctionNav.observe(viewLifecycleOwner, {
            if (it) {
                binding.functionMenu.visibility = View.GONE
                val animate = TranslateAnimation(
                    0f, 0f, 0f, binding.functionMenu.height.toFloat()
                )
                animate.duration = 100
                binding.functionMenu.startAnimation(animate)
                itemAdapter.isSelectedMode = false
                itemAdapter.notifyDataSetChanged()
            } else {
                val animate =
                    TranslateAnimation(0F, 0F, binding.functionMenu.height.toFloat(), 0F)
                animate.duration = 100
                binding.functionMenu.startAnimation(animate)
                binding.functionMenu.visibility = View.VISIBLE
            }
        })
        return binding.root
    }

    private fun setUpToolBar() {
        albumViewModel.album.observe(viewLifecycleOwner, {
            binding.topAppBar.title = it.name
            binding.topAppBar.subtitle = "${it.imagesCount} images ${it.videosCount} videos"
        })
    }

    private fun showItem() {
        itemAdapter = context?.let { ItemAdapter(it, AppUtil.FRAGMENT_ALBUM) }!!
        itemAdapter.albumName = albumName
        val layoutManager = GridLayoutManager(context, 4)
        binding.gridView.layoutManager = layoutManager
        albumViewModel.itemListByAlbum.observe(viewLifecycleOwner) {
            itemAdapter.data = it
        }
        binding.gridView.adapter = itemAdapter

        itemAdapter.setItemClick(object : OnItemClick {
            override fun onItemClick(view: View, position: Int, item: Item) {
                if (itemAdapter.isSelectedMode) {
                    if (itemAdapter.selectedList[position]) {
                        mainViewModel.selectedList.add(item)
                    } else {
                        mainViewModel.selectedList.remove(item)
                    }
                    Log.d("selected", mainViewModel.selectedList.toString())
                }
            }

            override fun onItemLongClick(view: View, position: Int, item: Item) {
                mainViewModel.selectedList.clear()
                itemAdapter.selectedList.clear()
                albumViewModel.showFunctionNavigation()
            }
        })
    }

    private fun onclickFunctionNavigation() {
        binding.functionMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.copy -> {
                    if (mainViewModel.selectedList.size == 0) {
                        Toast.makeText(context, "Select file to copy", Toast.LENGTH_SHORT).show()
                    } else {
                        albumViewModel.hideFunctionNavigation()
                        val bundle = Bundle()
                        bundle.putInt("select_mode", AppUtil.MODE_COPY)
                        bundle.putInt("screen_type", AppUtil.FRAGMENT_ALBUM_VIEW)
                        bundle.putString("album_name", albumName)
                        findNavController().navigate(
                            R.id.action_albumViewFragment_to_selectedAlbumFragment,
                            bundle
                        )
                    }
                    true
                }
                R.id.move -> {
                    if (mainViewModel.selectedList.size == 0) {
                        Toast.makeText(context, "Select file to move", Toast.LENGTH_SHORT).show()
                    } else {
                        albumViewModel.hideFunctionNavigation()
                        val bundle = Bundle()
                        bundle.putInt("select_mode", AppUtil.MODE_MOVE)
                        bundle.putInt("screen_type", AppUtil.FRAGMENT_ALBUM_VIEW)
                        bundle.putString("album_name", albumName)
                        findNavController().navigate(
                            R.id.action_albumViewFragment_to_selectedAlbumFragment,
                            bundle
                        )
                    }
                    true
                }
                R.id.delete -> {
                    if (mainViewModel.selectedList.size == 0) {
                        Toast.makeText(context, "Select file to delete", Toast.LENGTH_SHORT).show()
                    } else {
                        context?.let { it1 ->
                            mainViewModel.deleteSelectedItem(
                                it1,
                                mainViewModel.selectedList
                            )
                        }
                        albumViewModel.hideFunctionNavigation()
                        mainViewModel.selectedList.clear()
                        itemAdapter.selectedList.clear()
                        context?.let { it1 -> albumViewModel.getItemsByAlbum(it1, albumName) }
                    }
                    true
                }
                R.id.cancel -> {
                    Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show()
                    albumViewModel.hideFunctionNavigation()
                    mainViewModel.selectedList.clear()
                    itemAdapter.selectedList.clear()
                    true
                }
                else -> false
            }
        }
    }

    private fun onMenuClickItem() {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.pEdit -> {
                    itemAdapter.toSelectedMode()
                    albumViewModel.showFunctionNavigation()
                    true
                }
                R.id.pSelectAll -> {
                    Toast.makeText(context, "This feature is being developed", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.pCreate -> {
                    Toast.makeText(context, "This feature is being developed", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.pSlide -> {
                    Toast.makeText(context, "This feature is being developed", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.pSearch ->{
                    Toast.makeText(context, "This feature is being developed", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

}
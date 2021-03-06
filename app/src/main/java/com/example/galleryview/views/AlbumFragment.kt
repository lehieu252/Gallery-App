package com.example.galleryview.views

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galleryview.R
import com.example.galleryview.adapters.AlbumAdapter
import com.example.galleryview.databinding.FragmentAlbumBinding
import com.example.galleryview.models.Album
import com.example.galleryview.utilities.AppUtil
import com.example.galleryview.viewmodels.MainViewModel

class AlbumFragment : Fragment() {
    private lateinit var binding: FragmentAlbumBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: AlbumAdapter
    private lateinit var createAlbumDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_album, container, false)
        if (viewModel.hideBottomNav.value != false) {
            viewModel.showBottomNavigation()
        }
        showAlbums()
        onMenuClickItem()
        onclickFunctionNavigation()
        initDialog()

        viewModel.hideAlbumFunctionNav.observe(viewLifecycleOwner, {
            if (it) {
                binding.functionAlbumMenu.visibility = View.GONE
                val animate = TranslateAnimation(
                    0f, 0f, 0f, binding.functionAlbumMenu.height.toFloat()
                )
                animate.duration = 100
                binding.functionAlbumMenu.startAnimation(animate)
                adapter.isSelectedMode = false
                adapter.notifyDataSetChanged()
            } else {
                val animate =
                    TranslateAnimation(0F, 0F, binding.functionAlbumMenu.height.toFloat(), 0F)
                animate.duration = 100
                binding.functionAlbumMenu.startAnimation(animate)
                binding.functionAlbumMenu.visibility = View.VISIBLE
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
                if (adapter.type == AppUtil.TYPE_VIEW) {
                    if (!adapter.isSelectedMode) {
                        val bundle = Bundle()
                        bundle.putString("album_name", album.name)
                        findNavController().navigate(
                            R.id.action_albumFragment_to_albumViewFragment,
                            bundle
                        )
                    } else {
                        if (adapter.selectedList[position]) {
                            viewModel.selectedAlbum.add(album)
                        } else {
                            viewModel.selectedAlbum.remove(album)
                        }
                        Log.d("selected_album", viewModel.selectedAlbum.toString())
                    }
                }
            }

            override fun onItemLongClick(view: View, position: Int, album: Album) {
                if (adapter.type == AppUtil.TYPE_VIEW) {
                    viewModel.selectedAlbum.clear()
                    adapter.selectedList.clear()
                    if (!adapter.isSelectedMode) {
                        adapter.toSelectedMode()
                        viewModel.hideBottomNavigation()
                        viewModel.showAlbumFNav()
                    }

                }
            }
        })
    }


    private fun onMenuClickItem() {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.aEdit -> {
                    adapter.toSelectedMode()
                    viewModel.hideBottomNavigation()
                    viewModel.showAlbumFNav()
                    true
                }
                R.id.aSelectAll -> {
                    Toast.makeText(context, "This feature is being developed", Toast.LENGTH_SHORT)
                        .show()
                    true
                }
                R.id.aSearch -> {
                    Toast.makeText(context, "This feature is being developed", Toast.LENGTH_SHORT)
                        .show()
                    true
                }
                R.id.aCreate -> {
                    showDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun onclickFunctionNavigation() {
        binding.functionAlbumMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.aGroup -> {
                    Toast.makeText(context, "This feature is being developed", Toast.LENGTH_SHORT)
                        .show()
                    true
                }
                R.id.aMove -> {
                    Toast.makeText(context, "This feature is being developed", Toast.LENGTH_SHORT)
                        .show()
                    true
                }
                R.id.aDelete -> {
                    if (viewModel.selectedAlbum.size == 0) {
                        Toast.makeText(context, "Select file to delete", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                        context?.let { it1 ->
                            viewModel.deleteSelectedAlbum(
                                it1,
                                viewModel.selectedAlbum
                            )
                        }
                        viewModel.hideAlbumFNav()
                        viewModel.showBottomNavigation()
                        viewModel.selectedList.clear()
                        adapter.selectedList.clear()
                    }
                    true
                }
                R.id.aCancel -> {
                    Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show()
                    viewModel.hideAlbumFNav()
                    viewModel.showBottomNavigation()
                    viewModel.selectedList.clear()
                    adapter.selectedList.clear()
                    true
                }
                else -> false
            }
        }
    }


    private fun initDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        createAlbumDialog = builder.create()
        val view = layoutInflater.inflate(R.layout.create_album_dialog, null)
        createAlbumDialog.setView(view)
        createAlbumDialog.setCancelable(false)
        val editText = view.findViewById<TextView>(R.id.album_create_text)
        val cancelBtn = view.findViewById<Button>(R.id.cancel_dialog_btn)
        val createBtn = view.findViewById<Button>(R.id.create_dialog_btn)

        createBtn.setOnClickListener {
            viewModel.insertAlbum(editText.text.toString())
            createAlbumDialog.dismiss()
        }
        cancelBtn.setOnClickListener {
            createAlbumDialog.dismiss()
        }
    }

    private fun showDialog() {
        createAlbumDialog.show()
    }


    override fun onResume() {
        super.onResume()
        context?.let { viewModel.getAllAlbums(it) }
    }


}
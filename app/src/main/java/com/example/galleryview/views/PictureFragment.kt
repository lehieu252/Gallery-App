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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galleryview.R
import com.example.galleryview.adapters.ItemAdapter
import com.example.galleryview.adapters.OnItemClick
import com.example.galleryview.databinding.FragmentPictureBinding
import com.example.galleryview.models.Item
import com.example.galleryview.utilities.AppUtil
import com.example.galleryview.viewmodels.MainViewModel

class PictureFragment : Fragment() {
    private lateinit var binding: FragmentPictureBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var itemAdapter: ItemAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_picture, container, false)
        if(viewModel.hideBottomNav.value == true){
            viewModel.showBottomNavigation()
        }
        setUpGridView()
        onMenuClickItem()
        onclickFunctionNavigation()

        viewModel.hideFunctionNav.observe(viewLifecycleOwner, {
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

    private fun setUpGridView() {
        context?.let { viewModel.getAllItems(it) }
        itemAdapter = context?.let { ItemAdapter(it, AppUtil.FRAGMENT_PICTURE) }!!
        val layoutManager = GridLayoutManager(activity, 4)
        binding.gridView.layoutManager = layoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (itemAdapter!!.isHeader(position)) {
                    return layoutManager.spanCount
                } else return 1
            }
        }
        viewModel.itemList.observe(viewLifecycleOwner, Observer {
            itemAdapter.data = it
        })
        binding.gridView.adapter = itemAdapter;
        binding.gridView.setHasFixedSize(true)
        itemAdapter.setItemClick(object : OnItemClick {
            override fun onItemClick(view: View, position: Int, item: Item) {
                if (itemAdapter.isSelectedMode) {
                    if (itemAdapter.selectedList[position]) {
                        viewModel.selectedList.add(item)
                    } else {
                        viewModel.selectedList.remove(item)
                    }
                    Log.d("selected", viewModel.selectedList.toString())
                }
            }

            override fun onItemLongClick(view: View, position: Int, item: Item) {
                viewModel.selectedList.clear()
                itemAdapter.selectedList.clear()
                viewModel.hideBottomNavigation()
                viewModel.showFunctionNavigation()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        context?.let { viewModel.getAllItems(it) }
    }

    private fun onMenuClickItem() {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.pEdit -> {
                    itemAdapter.toSelectedMode()
                    viewModel.hideBottomNavigation()
                    viewModel.showFunctionNavigation()
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

    private fun onclickFunctionNavigation() {
        binding.functionMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.copy -> {
                    if (viewModel.selectedList.size == 0) {
                        Toast.makeText(context, "Select file to copy", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.hideFunctionNavigation()
                        val bundle = Bundle()
                        bundle.putInt("select_mode", AppUtil.MODE_COPY)
                        bundle.putInt("screen_type", AppUtil.FRAGMENT_PICTURE)
                        findNavController().navigate(R.id.action_pictureFragment_to_selectedAlbumFragment,bundle)
                    }
                    true
                }
                R.id.move -> {
                    if (viewModel.selectedList.size == 0) {
                        Toast.makeText(context, "Select file to move", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.hideFunctionNavigation()
                        val bundle = Bundle()
                        bundle.putInt("select_mode", AppUtil.MODE_MOVE)
                        bundle.putInt("screen_type", AppUtil.FRAGMENT_PICTURE)
                        findNavController().navigate(R.id.action_pictureFragment_to_selectedAlbumFragment, bundle)
                    }
                    true
                }
                R.id.delete -> {
                    if (viewModel.selectedList.size == 0) {
                        Toast.makeText(context, "Select file to delete", Toast.LENGTH_SHORT).show()
                    } else {
                        context?.let { it1 -> viewModel.deleteSelectedItem(it1, viewModel.selectedList) }
                        viewModel.hideFunctionNavigation()
                        viewModel.showBottomNavigation()
                        viewModel.selectedList.clear()
                        itemAdapter.selectedList.clear()
                    }
                    true
                }
                R.id.cancel -> {
                    Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show()
                    viewModel.hideFunctionNavigation()
                    viewModel.showBottomNavigation()
                    viewModel.selectedList.clear()
                    itemAdapter.selectedList.clear()
                    true
                }
                else -> false
            }
        }
    }
}
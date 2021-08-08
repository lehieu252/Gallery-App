package com.example.galleryview.views

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galleryview.R
import com.example.galleryview.adapters.ItemAdapter
import com.example.galleryview.databinding.FragmentPictureBinding
import com.example.galleryview.viewmodels.MainViewModel

class PictureFragment : Fragment() {
    private lateinit var binding: FragmentPictureBinding
    private val viewModel: MainViewModel by activityViewModels()

    companion object {
        const val TYPE_PICTURE_FRAGMENT = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_picture, container, false)
//        binding.topAppBar.inflateMenu(R.menu.picture_top_app_bar)
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.pEdit -> {
                    Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show()
                    Log.d("Click", "edit")
                    true
                }
                R.id.pSelectAll -> {
                    Toast.makeText(context, "SelectAll", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.pCreate -> {
                    Toast.makeText(context, "Create", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.pSlide -> {
                    Toast.makeText(context, "SlideShow", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        setUpGridView()
        return binding.root
    }

    private fun setUpGridView() {
        val itemAdapter = activity?.let { ItemAdapter(it, TYPE_PICTURE_FRAGMENT) }
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
            if (itemAdapter != null) {
                itemAdapter.data = it
            }
//            Log.d("List", it.toString())
        })
        binding.gridView.adapter = itemAdapter;

    }

    override fun onResume() {
        super.onResume()
        context?.let { viewModel.getAllItems(it) }
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.pEdit -> {
//                Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show()
//                Log.d("Click", "edit")
//            }
//            R.id.pSelectAll -> {
//                Toast.makeText(context, "SelectAll", Toast.LENGTH_SHORT).show()
//            }
//            R.id.pCreate -> {
//                Toast.makeText(context, "Create", Toast.LENGTH_SHORT).show()
//            }
//            R.id.pSlide -> {
//                Toast.makeText(context, "SlideShow", Toast.LENGTH_SHORT).show()
//            }
//        }
//        return true
//    }
}
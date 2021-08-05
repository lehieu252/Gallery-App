package com.example.galleryview.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.galleryview.R
import com.example.galleryview.adapters.ViewPAdapter
import com.example.galleryview.databinding.FragmentViewBinding
import com.example.galleryview.models.Item
import com.example.galleryview.viewmodels.MainViewModel
import com.example.galleryview.viewmodels.MainViewModelFactory

class ViewFragment : Fragment() {
    private lateinit var binding: FragmentViewBinding
    private lateinit var viewModelFactory: MainViewModelFactory
    private lateinit var viewModel: MainViewModel
    private var listPath: ArrayList<Item> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view, container, false)
        viewModelFactory = MainViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        val bundle = arguments
        val curPos = bundle?.getInt("position")
        context?.let { viewModel.getAllItemView(it) }
        val viewPAdapter = activity?.let { ViewPAdapter(it) }
        viewModel.itemView.observe(viewLifecycleOwner, Observer {
            if (viewPAdapter != null) {
                viewPAdapter.data = it
            }
        })
//        binding.indicator.setupWithViewPager(binding.viewPager, listPath, 70F)
        binding.viewPager.adapter = viewPAdapter
        binding.viewPager.currentItem = 1

//        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            override fun onPageScrolled(
//                position: Int,
//                positionOffset: Float,
//                positionOffsetPixels: Int
//            ) {
//            }
//
//            override fun onPageSelected(position: Int) {
//            }
//
//            override fun onPageScrollStateChanged(state: Int) {
//            }
//
//        })
        return binding.root
    }

}



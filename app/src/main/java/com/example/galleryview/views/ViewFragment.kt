package com.example.galleryview.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.galleryview.R
import com.example.galleryview.adapters.ViewPAdapter
import com.example.galleryview.databinding.FragmentViewBinding
import com.example.galleryview.viewmodels.MainViewModel

class ViewFragment : Fragment() {
    private lateinit var binding: FragmentViewBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var viewPagerAdapter: ViewPAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view, container, false)
        showItemView()
        viewModel.hideBottomNavigation()
        return binding.root
    }

    private fun showItemView() {
        val bundle = arguments
        val curPos = bundle?.getInt("position")
        viewPagerAdapter = activity?.let { ViewPAdapter(it) }!!
        viewModel.itemView.observe(viewLifecycleOwner, Observer {
            viewPagerAdapter.data = it
            Log.d("list_data", it.toString())
        })
        binding.viewPager.adapter = viewPagerAdapter

        binding.viewPager.post {
            if (curPos != null) {
                binding.viewPager.setCurrentItem(curPos, false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.showBottomNavigation()
    }
}



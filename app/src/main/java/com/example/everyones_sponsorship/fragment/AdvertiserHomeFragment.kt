package com.example.everyones_sponsorship.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.everyones_sponsorship.databinding.FragmentAdvertiserHomeBinding
import com.example.everyones_sponsorship.databinding.FragmentChattingBinding

class AdvertiserHomeFragment : Fragment() {

    private var mBinding : FragmentAdvertiserHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAdvertiserHomeBinding.inflate(inflater, container, false)

        mBinding = binding

        return mBinding?.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}

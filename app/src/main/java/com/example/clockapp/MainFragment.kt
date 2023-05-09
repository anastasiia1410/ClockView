package com.example.clockapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.clockapp.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel.clockAngelLD.observe(viewLifecycleOwner) { clockAngel ->
                val (hours, minutes, seconds) = clockAngel
                clock.timeHours = hours
                clock.timeMinutes = minutes
                clock.timeSecond = seconds
            }
            viewModel.startArrows()
            clock.weather = Weather.Sunny(22)
            clock.heartBeat = 86
        }
    }
}
package com.kme.kaltura.kmeapplication.view.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kme.kaltura.kmeapplication.databinding.DialogConnectionPreviewBinding
import com.kme.kaltura.kmeapplication.util.SoundAmplitudeMeter
import com.kme.kaltura.kmeapplication.util.widget.SoundAmplitudeView
import com.kme.kaltura.kmeapplication.viewmodel.ConnectionPreviewViewModel
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ConnectionPreviewDialog : DialogFragment() {

    private val peerConnectionViewModel: ConnectionPreviewViewModel by viewModel()

    private lateinit var binding: DialogConnectionPreviewBinding

    private val measureScope = CoroutineScope(Dispatchers.IO)
    private var amplitudeMeter: SoundAmplitudeMeter? = null

    private var listener: PreviewListener? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogConnectionPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        amplitudeMeter = SoundAmplitudeMeter(view.context)
        startMeasure()

        peerConnectionViewModel.createPreview(binding.preview)

        with(binding) {
            btnToggleMicro.isSelected = true
            btnToggleCamera.isSelected = true
            btnSwitchCamera.isSelected = true

            btnToggleMicro.setOnClickListener {
                it.isSelected = !it.isSelected
            }
            btnToggleCamera.setOnClickListener {
                it.isSelected = !it.isSelected
                if (it.isSelected) startMeasure() else stopMeasure()
                peerConnectionViewModel.enablePreview(it.isSelected)
            }
            btnSwitchCamera.setOnClickListener {
                it.isSelected = !it.isSelected
                peerConnectionViewModel.switchCamera()
            }

            btnAccept.setOnClickListener {
                listener?.onPreviewSettingsAccepted(
                    btnToggleMicro.isSelected,
                    btnToggleCamera.isSelected,
                    btnSwitchCamera.isSelected
                )
                dismiss()
            }
        }
    }

    private fun startMeasure() {
        measureScope.launch {
            amplitudeMeter?.start()
            while (true) {
                amplitudeMeter?.getAmplitude()?.let {
                    val amplitude =
                        if (it > SoundAmplitudeMeter.MAX_AMPLITUDE) SoundAmplitudeMeter.MAX_AMPLITUDE else it
                    val value =
                        (amplitude * SoundAmplitudeView.MAX_VALUE) / SoundAmplitudeMeter.MAX_AMPLITUDE

                    CoroutineScope(Dispatchers.Main).launch {
                        binding.amplitudeView.setValue(value)
                    }
                }
                delay(100)
            }
        }
    }

    private fun stopMeasure() {
        amplitudeMeter?.stop()
        measureScope.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopMeasure()
        peerConnectionViewModel.stopPreview()
    }

    fun setListener(listener: PreviewListener) {
        this.listener = listener
    }

    interface PreviewListener {
        fun onPreviewSettingsAccepted(
            micEnabled: Boolean,
            camEnabled: Boolean,
            frontCamEnabled: Boolean
        )
    }

    companion object {

        const val TAG = "ConnectionPreviewDialog"

        @JvmStatic
        fun newInstance() = ConnectionPreviewDialog().apply {
            arguments = Bundle().apply {}
        }
    }

}
package com.thebradfo.flickrbrowser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_image_dialog.*

/**
 * Fragment host for the dialog that shows an image item on click.
 */
class ImageDialogFragment: DialogFragment() {

    private val imageUri: String
        get() = requireNotNull(arguments?.getString(IMAGE_ARG, ""))

    private val imageDescription: String?
        get() = arguments?.getString(DESCRIPTION_ARG, "")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_image_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(requireContext())
            .load(imageUri)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .placeholder(R.drawable.ic_image_24px)
            .error(R.drawable.ic_broken_image_24px)
            .fitCenter()
            .into(image_view)

        description.text = imageDescription

        close.setOnClickListener { dismissAllowingStateLoss() }
    }

    override fun onStart() {
        super.onStart()

        // set the dialog size so it effectively matches the window width and wraps it's height
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        private const val IMAGE_ARG = "image_url"
        private const val DESCRIPTION_ARG = "description"

        @JvmStatic
        fun openFragment(
            fragmentManager: FragmentManager,
            imagUri: String,
            description: String?
        ) {

            ImageDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(IMAGE_ARG, imagUri)
                    putString(DESCRIPTION_ARG, description)
                }
                show(fragmentManager, null)
            }
        }
    }
}

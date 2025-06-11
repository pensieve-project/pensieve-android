package ru.hse.pensieve.ui.posts_view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.hse.pensieve.databinding.FragmentCommentsBinding
import ru.hse.pensieve.posts.PostViewModel
import java.util.UUID

class CommentsFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by activityViewModels()
    private lateinit var adapter: CommentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postIdString = arguments?.getString("postId") ?: return
        val postId = UUID.fromString(postIdString)

        adapter = CommentsAdapter(emptyList())
        binding.commentsRecyclerView.adapter = adapter
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.loadComments(postId)

        viewModel.commentsWithAuthors.observe(viewLifecycleOwner) { comments ->
            adapter.updateComments(comments!!)
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnSend.setOnClickListener {
            val commentText = binding.commentEditText.text.toString()

            if (commentText.isNotEmpty()) {
                binding.commentEditText.text = null
                viewModel.leaveComment(postId, commentText)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 610
            behavior.isHideable = true
        }
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(postId: UUID): CommentsFragment {
            return CommentsFragment().apply {
                arguments = Bundle().apply {
                    putString("postId", postId.toString())
                }
            }
        }
    }
}
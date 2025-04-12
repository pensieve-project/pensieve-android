package ru.hse.pensieve.ui.posts_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import kotlinx.coroutines.launch
import ru.hse.pensieve.databinding.FragmentPostsFeedBinding
import ru.hse.pensieve.posts.PostViewModel

class PostsFeedFragment : Fragment() {
    private var _binding: FragmentPostsFeedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by activityViewModels()
    private lateinit var adapter: PostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostsFeedBinding.inflate(inflater, container, false)

        val recyclerView = FragmentRecyclerView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        (binding.root as ViewGroup).apply {
            removeAllViews()
            addView(recyclerView)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = (binding.root as ViewGroup).getChildAt(0) as FragmentRecyclerView

        adapter = PostsAdapter(childFragmentManager, viewModel)
        recyclerView.apply {
            adapter = this@PostsFeedFragment.adapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = false
            }

            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(this)

            setHasFixedSize(false)
            overScrollMode = View.OVER_SCROLL_NEVER
            itemAnimator = null
        }

        viewModel.allPosts.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts ?: emptyList())
        }

        loadPosts()
    }

    private fun loadPosts() {
        lifecycleScope.launch {
            viewModel.getAllPosts()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PostsFeedFragment()
    }
}

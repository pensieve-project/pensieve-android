package ru.hse.pensieve.ui.posts_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView

class FragmentRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private val fragmentManager: FragmentManager
        get() = (context as FragmentActivity).supportFragmentManager

    private val attachedFragments = mutableMapOf<Int, Fragment>()

    init {
        addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                val holder = getChildViewHolder(view) as? FragmentViewHolder ?: return
                val position = holder.bindingAdapterPosition
                if (position != NO_POSITION) {
                    attachFragment(position, holder)
                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                val holder = getChildViewHolder(view) as? FragmentViewHolder ?: return
                val position = holder.bindingAdapterPosition
                if (position != NO_POSITION) {
                    detachFragment(position)
                }
            }
        })
    }

    private fun attachFragment(position: Int, holder: FragmentViewHolder) {
        val fragment = holder.fragment
        val existingFragment = attachedFragments[position]
        if (existingFragment != fragment) {
            if (existingFragment != null) {
                detachFragment(position)
            }
            fragmentManager.beginTransaction()
                .add(holder.container.id, fragment)
                .commitNowAllowingStateLoss()
            attachedFragments[position] = fragment
        }
    }

    private fun detachFragment(position: Int) {
        attachedFragments[position]?.let { fragment ->
            fragmentManager.beginTransaction()
                .remove(fragment)
                .commitNowAllowingStateLoss()
            attachedFragments.remove(position)
        }
    }
}
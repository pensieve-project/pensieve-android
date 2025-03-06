package ru.hse.pensieve.ui.postcreation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.hse.pensieve.databinding.ThemeItemThemeBinding
import ru.hse.pensieve.themes.models.Theme
import ru.hse.pensieve.R

class ThemeAdapter(
    private val themes: List<Theme>,
    private val onItemClick: (Theme) -> Unit
) : RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder>() {

    override fun getItemCount(): Int = themes.size
    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ThemeItemThemeBinding.inflate(inflater, parent, false)
        return ThemeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        val theme = themes[position]
        holder.bind(theme)

        changeThemeCardOnClick(holder, position == selectedPosition)

        holder.itemView.setOnClickListener {
            val previousSelected = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedPosition)
            onItemClick(theme)
        }
    }

    private fun changeThemeCardOnClick(holder: ThemeViewHolder, selected: Boolean) {
        val majorColor = if (selected) {
            R.color.brown950
        } else {
            R.color.beige50
        }
        val minorColor = if (selected) {
            R.color.beige50
        } else {
            R.color.brown950
        }

        holder.binding.themeCard.setBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, majorColor)
        )
        holder.binding.titleTextView.setTextColor(
            ContextCompat.getColor(holder.itemView.context, minorColor)
        )
        holder.binding.authorTextView.setTextColor(
            ContextCompat.getColor(holder.itemView.context, minorColor)
        )
        holder.binding.timeStampTextView.setTextColor(
            ContextCompat.getColor(holder.itemView.context, minorColor)
        )
    }

    class ThemeViewHolder(val binding: ThemeItemThemeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(theme: Theme) {
            binding.titleTextView.text = theme.title
            binding.authorTextView.text = "Author: ${theme.authorId.toString()}" // change to username
            binding.timeStampTextView.text = "Created: ${theme.timeStamp.toString()}" /// change format
        }
    }
}
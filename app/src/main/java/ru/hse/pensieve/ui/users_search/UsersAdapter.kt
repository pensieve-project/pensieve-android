package ru.hse.pensieve.ui.users_search

import android.graphics.BitmapFactory
import androidx.recyclerview.widget.RecyclerView
import ru.hse.pensieve.search.models.User

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.ItemUserBinding

class UsersAdapter(
    private var users: List<User>,
    private val onItemClick: (Set<User>) -> Unit,
    private var isMultiSelectMode: Boolean,
    private val getUsername: (User) -> String,
    private val getUserAvatar: (User) -> ByteArray?
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    override fun getItemCount(): Int = users.size
    private val selectedUsers = mutableSetOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)

        changeUserCard(holder, selectedUsers.contains(user))

        holder.itemView.setOnClickListener {
            handleUserClick(user)
        }
    }

    private fun handleUserClick(user: User) {
        if (isMultiSelectMode) {
            toggleUserSelection(user)
        } else {
            selectSingleUser(user)
        }
    }

    private fun toggleUserSelection(user: User) {
        if (selectedUsers.contains(user)) {
            selectedUsers.remove(user)
        } else {
            selectedUsers.add(user)
        }
        notifyItemChanged(users.indexOf(user))
        onItemClick(selectedUsers)
    }

    private fun selectSingleUser(user: User) {
        val previousSelected = selectedUsers.toSet()
        selectedUsers.clear()
        selectedUsers.add(user)

        previousSelected.forEach { oldUser ->
            notifyItemChanged(users.indexOf(oldUser))
        }
        notifyItemChanged(users.indexOf(user))
        onItemClick(selectedUsers)
    }

    private fun changeUserCard(holder: UserViewHolder, selected: Boolean) {
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

        holder.binding.userCard.setBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, majorColor)
        )
        holder.binding.usernameTextView.setTextColor(
            ContextCompat.getColor(holder.itemView.context, minorColor)
        )
    }

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }

    inner class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.usernameTextView.text = getUsername(user)
            val avatar = getUserAvatar(user)
            if (avatar == null) {
                binding.avatarImageView.setImageResource(R.drawable.default_avatar)
            } else {
                val avatarBitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.size)
                binding.avatarImageView.setImageBitmap(avatarBitmap)
            }
        }
    }
}
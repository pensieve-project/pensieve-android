package ru.hse.pensieve.ui.feed

import androidx.recyclerview.widget.RecyclerView
import ru.hse.pensieve.search.models.User

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.hse.pensieve.databinding.ItemUserBinding

class UsersAdapter(
    private var users: List<User>,
    private val onItemClick: (User) -> Unit
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    override fun getItemCount(): Int = users.size
    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)

        holder.itemView.setOnClickListener {
            onItemClick(user)
        }
    }

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }

    class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.usernameTextView.text = user.username
        }
    }
}
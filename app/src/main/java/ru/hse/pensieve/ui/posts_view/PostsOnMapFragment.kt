package ru.hse.pensieve.ui.posts_view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import ru.hse.pensieve.R
import ru.hse.pensieve.posts.PostViewModel
import java.util.UUID

class PostsOnMapFragment : Fragment() {
    private lateinit var mapView: MapView
    private lateinit var postsType: String
    private lateinit var id: UUID

    private val viewModel: PostViewModel by activityViewModels()

    companion object {
        private const val ARG_POSTS_TYPE = "posts_type"
        private const val ARG_ID = "id"

        fun newInstance(postsType: String, id: UUID): PostsOnMapFragment {
            return PostsOnMapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_POSTS_TYPE, postsType)
                    putString(ARG_ID, id.toString())
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())
        postsType = requireArguments().getString(ARG_POSTS_TYPE)!!
        id = UUID.fromString(requireArguments().getString(ARG_ID))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_posts_on_map, container, false).also {
            mapView = it.findViewById(R.id.mapview)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (postsType == "USERS_POSTS") {
            viewModel.getAllUsersPosts(id)
        }
        else if (postsType == "THEMES_POSTS") {
            viewModel.getAllThemesPosts(id)
        }
        mapView = view.findViewById(R.id.mapview)

        mapView.map.move(
            CameraPosition(Point(59.9386, 30.3141), 12.0f, 0f, 0f)
        )

        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            if (posts != null) {
                for (post in posts) {
                    val imageProvider =
                        ImageProvider.fromBitmap(post!!.photo!!.toBitmap())
                    val placemarkObject = mapView.mapWindow.map.mapObjects.addPlacemark().apply {
                        geometry = Point(post.location!!.latitude, post.location.longitude)
                        setIcon(imageProvider)
                        setIconStyle(IconStyle().apply {
                            scale = 0.2f
                        })
                    }
                    val placemarkTapListener = MapObjectTapListener { _, _ ->
                        val intent = Intent(requireContext(), OpenPostActivity::class.java).apply {
                            putExtra("POST_ID", post.postId.toString())
                        }
                        startActivity(intent)
                        true
                    }
                    placemarkObject.addTapListener(placemarkTapListener)
                }
            }
        }
    }

    private fun ByteArray.toBitmap(): Bitmap? {
        return BitmapFactory.decodeByteArray(this, 0, this.size)
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}

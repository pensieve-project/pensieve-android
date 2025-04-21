package ru.hse.pensieve.ui.map

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import ru.hse.pensieve.BuildConfig
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.FragmentLocationSearchBinding
import ru.hse.pensieve.posts.CreatePostViewModel
import ru.hse.pensieve.ui.postcreation.CreatePostActivity
import java.util.Locale

class LocationSearchFragment : Fragment() {
    private var _binding: FragmentLocationSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private lateinit var geocoder: Geocoder
    private lateinit var viewModel: CreatePostViewModel

    var chosenPoint: ru.hse.pensieve.posts.models.Point? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(requireContext())
        geocoder = Geocoder(requireContext(), Locale.getDefault())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(CreatePostViewModel::class.java)

        setupButtons()
        updateNavigationButtons()

        mapView = binding.mapView
        mapView.map.move(
            CameraPosition(Point(55.751244, 37.6176), 10f, 0f, 0f)
        )

        binding.searchButton.setOnClickListener {
            when {
                binding.searchEditText.text.isNullOrEmpty() -> {
                    Toast.makeText(context, "Введите название места или координаты", Toast.LENGTH_SHORT).show()
                }
                isCoordinateInput(binding.searchEditText.text.toString()) -> {
                    searchByCoordinates(binding.searchEditText.text.toString())
                }
                else -> {
                    searchByQuery(binding.searchEditText.text.toString())
                }
            }
        }
    }

    private fun isCoordinateInput(input: String): Boolean {
        val coordinatePattern = """^-?\d{1,3}\.\d+,\s*-?\d{1,3}\.\d+$""".toRegex()
        return coordinatePattern.matches(input)
    }

    private fun searchByCoordinates(coordinates: String) {
        try {
            val parts = coordinates.split(",")
            val lat = parts[0].trim().toDouble()
            val lon = parts[1].trim().toDouble()

            val point = Point(lat, lon)
            chosenPoint = ru.hse.pensieve.posts.models.Point(lat, lon)
            moveCameraToPoint(point)

            // Можно добавить маркер
            mapView.map.mapObjects.clear()

            val imageProvider =
                ImageProvider.fromResource(requireContext(), R.drawable.geo_light)

            mapView.mapWindow.map.mapObjects.addPlacemark().apply {
                geometry = point
                setIcon(imageProvider)
            }

            // Получаем адрес по координатам
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            addresses?.firstOrNull()?.let {
                val address = it.getAddressLine(0)
                binding.locationInfo.text = "Адрес: $address\nКоординаты: $lat, $lon"
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Некорректные координаты", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchByQuery(query: String) {
        try {
            val addresses = geocoder.getFromLocationName(query, 1)
            addresses?.firstOrNull()?.let {
                val lat = it.latitude
                val lon = it.longitude
                val point = Point(lat, lon)
                chosenPoint = ru.hse.pensieve.posts.models.Point(lat, lon)

                moveCameraToPoint(point)

                // Добавляем маркер
                mapView.map.mapObjects.clear()

                val imageProvider =
                    ImageProvider.fromResource(requireContext(), R.drawable.geo_light)

                mapView.mapWindow.map.mapObjects.addPlacemark().apply {
                    geometry = point
                    setIcon(imageProvider)
                }

                val address = it.getAddressLine(0)
                binding.locationInfo.text = "Адрес: $address\nКоординаты: $lat, $lon"
            } ?: run {
                Toast.makeText(context, "Место не найдено", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка поиска", Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveCameraToPoint(point: Point) {
        mapView.map.move(
            CameraPosition(point, 15f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = LocationSearchFragment()
    }

    private fun setupButtons() {
        binding.btnClose.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnNext.setOnClickListener {
            viewModel.postLocation.value = ru.hse.pensieve.posts.models.Point(59.9386, 30.3141)
//            viewModel.postLocation.value = chosenPoint
            (requireActivity() as CreatePostActivity).nextStep()
        }

        binding.btnPrev.setOnClickListener {
            (requireActivity() as CreatePostActivity).prevStep()
        }
    }

    private fun updateNavigationButtons() {
        binding.btnPrev.visibility = View.VISIBLE
        binding.btnNext.visibility = View.VISIBLE
    }
}
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
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
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
    private lateinit var searchManager: SearchManager
    private var searchSession: Session? = null

    var chosenPoint: ru.hse.pensieve.posts.models.Point? = null

    private val searchListener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            if (response.collection.children.isEmpty()) {
                Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
                return
            }

            val firstResult = response.collection.children.first().obj
            val point = firstResult?.geometry?.firstOrNull()?.point
                ?: return Toast.makeText(context, "Failed to determine coordinates", Toast.LENGTH_SHORT).show()

            chosenPoint = ru.hse.pensieve.posts.models.Point(point.latitude, point.longitude)
            moveCameraToPoint(point)
            addMarker(point)

            val name = firstResult.name ?: "Unknown place"
            val address = firstResult.metadataContainer.run {
                getItem(ToponymObjectMetadata::class.java)?.address?.formattedAddress
                    ?: getItem(BusinessObjectMetadata::class.java)?.address?.formattedAddress
                    ?: "No address"
            }

            binding.locationInfo.text = """
                Name: $name
                Address: $address
                Coordinates: ${point.latitude}, ${point.longitude}
            """.trimIndent()
        }

        override fun onSearchError(error: Error) {
            Toast.makeText(context, "Error in search", Toast.LENGTH_SHORT).show()
        }
    }

    private val mapInputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            searchReverseGeocode(point)
        }

        override fun onMapLongTap(map: Map, point: Point) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)

        setupButtons()
        updateNavigationButtons()

        mapView = binding.mapView
        mapView.map.addInputListener(mapInputListener)
        mapView.map.move(
            CameraPosition(Point(59.9386, 30.3141), 10f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )

        binding.searchButton.setOnClickListener {
            when {
                binding.searchEditText.text.isNullOrEmpty() -> {
                    Toast.makeText(context, "Place name or coordinates...", Toast.LENGTH_SHORT).show()
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
            searchReverseGeocode(point)
        } catch (e: Exception) {
            Toast.makeText(context, "Incorrect coordinates", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchByQuery(query: String) {
        searchSession = searchManager.submit(
            query,
            VisibleRegionUtils.toPolygon(mapView.map.visibleRegion),
            SearchOptions().apply {
                searchTypes = SearchType.BIZ.value or SearchType.GEO.value
                resultPageSize = 1
            },
            searchListener
        )
    }

    private fun searchReverseGeocode(point: Point) {
        searchSession = searchManager.submit(
            point,
            15,
            SearchOptions().apply {
                searchTypes = SearchType.GEO.value
            },
            searchListener
        )
    }

    private fun moveCameraToPoint(point: Point) {
        mapView.map.move(
            CameraPosition(point, 15f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }

    private fun addMarker(point: Point) {
        mapView.map.mapObjects.clear()
        val imageProvider = ImageProvider.fromResource(requireContext(), R.drawable.geo_light)
        mapView.mapWindow.map.mapObjects.addPlacemark().apply {
            geometry = point
            setIcon(imageProvider)
        }
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
            viewModel.postLocation.value = chosenPoint
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

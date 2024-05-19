    package com.example.tryksave;

    import android.content.ContentValues;
    import android.os.Bundle;
    import android.util.Log;

    import androidx.annotation.NonNull;
    import androidx.fragment.app.Fragment;

    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.TextView;

    import com.google.android.gms.common.api.Status;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.libraries.places.api.Places;
    import com.google.android.libraries.places.api.model.Place;
    import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
    import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
    import com.google.gson.JsonObject;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.firestore.FirebaseFirestore;
    import java.util.HashMap;
    import java.util.Map;

    import java.util.Arrays;

    import retrofit2.Call;
    import retrofit2.Retrofit;
    import retrofit2.converter.gson.GsonConverterFactory;
    import retrofit2.http.GET;
    import retrofit2.http.Query;

    public class FragmentHome extends Fragment {
        private Button estimateButton;
        private LatLng startLocation;
        private LatLng endLocation;
        private TextView estimationTextView;

        private static final String DIRECTIONS_BASE_URL = "https://maps.googleapis.com/maps/api/";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_app, container, false);

            estimationTextView = rootView.findViewById(R.id.estimation_text_view);
            estimateButton = rootView.findViewById(R.id.estimation_button);

            if (!Places.isInitialized()) {
                Places.initialize(requireContext(), "AIzaSyCfEmbt8VnQ0w-FUpIUCAAGpWLhKF14DWQ");
            }

            // Initialize the starting point AutocompleteSupportFragment
            AutocompleteSupportFragment startAutocompleteFragment = (AutocompleteSupportFragment)
                    getChildFragmentManager().findFragmentById(R.id.start_autocomplete_fragment);

            // Initialize the endpoint AutocompleteSupportFragment
            AutocompleteSupportFragment endAutocompleteFragment = (AutocompleteSupportFragment)
                    getChildFragmentManager().findFragmentById(R.id.end_autocomplete_fragment);

            // Specify the types of place data to return
            startAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
            endAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

            // Set up PlaceSelectionListener for the starting point
            startAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    // Handle the selected starting point
                    startLocation = place.getLatLng();
                    Log.i("StartPlaceSelected", "Place: " + place.getName() + ", " + place.getId());
                    // Do something with the selected starting point
                }

                @Override
                public void onError(@NonNull Status status) {
                    // Handle the error
                    Log.i("StartPlaceError", "An error occurred: " + status);
                }
            });

            // Set up PlaceSelectionListener for the endpoint
            endAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    // Handle the selected endpoint
                    endLocation = place.getLatLng();
                    Log.i("EndPlaceSelected", "Place: " + place.getName() + ", " + place.getId());
                    // Do something with the selected endpoint
                }

                @Override
                public void onError(@NonNull Status status) {
                    // Handle the error
                    Log.i("EndPlaceError", "An error occurred: " + status);
                }
            });

            estimateButton.setOnClickListener(v -> {
                if (startLocation != null && endLocation != null) {
                    getRouteInfo(startLocation, endLocation);
                }
            });

            return rootView;
        }

        private void getRouteInfo(LatLng start, LatLng end) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(DIRECTIONS_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            DirectionsService service = retrofit.create(DirectionsService.class);

            Call<JsonObject> call = service.getDirections(
                    start.latitude + "," + start.longitude,
                    end.latitude + "," + end.longitude,
                    "AIzaSyCfEmbt8VnQ0w-FUpIUCAAGpWLhKF14DWQ");

            call.enqueue(new retrofit2.Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject jsonResponse = response.body();
                        parseRouteInfo(jsonResponse);
                    } else {
                        Log.e("DirectionsAPI", "Failed to get directions: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("DirectionsAPI", "Error: " + t.getMessage());
                }
            });
        }

        private void parseRouteInfo(JsonObject jsonResponse) {
            JsonObject route = jsonResponse.getAsJsonArray("routes").get(0).getAsJsonObject();
            JsonObject leg = route.getAsJsonArray("legs").get(0).getAsJsonObject();
            JsonObject distance = leg.getAsJsonObject("distance");
            JsonObject duration = leg.getAsJsonObject("duration");

            String distanceText = distance.get("text").getAsString();
            String durationText = duration.get("text").getAsString();

            final float INITIAL_FARE_PRICE = 10.0f; // Using floating-point for prices
            final float DISTANCE_RATE = 19.0f; // Using floating-point for rate
            final float DURATION_RATE = 2.0f; // Using floating-point for rate

            // Parse distance and duration as floats
            float distanceValue = Float.parseFloat(distanceText.split(" ")[0]); // Extract numeric part
            float durationValue = Float.parseFloat(durationText.split(" ")[0]); // Extract numeric part

            float estimatedFarePrice = (distanceValue * DISTANCE_RATE) + INITIAL_FARE_PRICE + (durationValue * DURATION_RATE);
            String stringEstimatedFarePrice = String.format("%.2f", estimatedFarePrice);

            String estimationMessage = String.format("Distance: %s\nDuration: %s\nEstimated Price: %s",
                    distanceText, durationText, stringEstimatedFarePrice);
            estimationTextView.setText(estimationMessage);


            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> estimation = new HashMap<>();
                estimation.put("distanceText", distanceText);
                estimation.put("durationText", durationText);
                estimation.put("estimatedFarePrice", stringEstimatedFarePrice);
                db.collection("users").document(user.getUid())
                        .collection("estimations")
                        .add(estimation)
                        .addOnSuccessListener(documentReference -> Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId()))
                        .addOnFailureListener(e -> Log.w("Firestore", "Error adding document", e));
            } else {
                Log.w("Firestore", "User is null, cannot save estimation to Firestore");
            }
        }


        interface DirectionsService {
            @GET("directions/json")
            Call<JsonObject> getDirections(
                    @Query("origin") String origin,
                    @Query("destination") String destination,
                    @Query("key") String apiKey
            );
        }
    }
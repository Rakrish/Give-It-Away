package com.example.administrator.fbbooks;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PublishPost extends CustomMenuActivity implements LocationListener {

    private HashMap<String, String> pageInfo;
    private EditText title, author, publisher, publishedDate, addressData;
    private static final int CAMERA_REQUEST = 1888;
    private static final int PLACE_PICKER_REQUEST = 1;
    private ImageView imageView;
    private boolean titleFilled = false;
    private boolean authorFilled = false;
    private boolean publisherFilled = false;
    private boolean publishedDateFilled = false;
    Button capture, locationBtn;
    Bitmap photo;
    private LocationManager locationManager;
    private String provider;
    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_post);
        pd = new ProgressDialog(PublishPost.this);
        if(pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Button post = (Button) findViewById(R.id.post);
        this.imageView = (ImageView) findViewById(R.id.imageView);
        title = (EditText) findViewById(R.id.book_title);
        author = (EditText) findViewById(R.id.author);
        publisher = (EditText) findViewById(R.id.publisher);
        publishedDate = (EditText) findViewById(R.id.publicationDate);
        addressData = (EditText) findViewById(R.id.address);
        publishedDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    dpd(view);
                    publishedDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dpd(view);
                        }
                    });
                }
            }
        });
        capture = (Button) findViewById(R.id.capturePhoto);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        locationBtn = (Button) findViewById(R.id.location);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location = locationManager.getLastKnownLocation(provider);

                // Initialize the location fields
                if (location != null) {
                    Toast.makeText(getApplicationContext(), "Provider " + provider + " has been selected.",
                            Toast.LENGTH_LONG).show();
                    onLocationChanged(location);
                } else {
                    Toast.makeText(getApplicationContext(), "Location not available. Please enter manually",
                            Toast.LENGTH_LONG).show();
                }*/
                try {
                    startActivityForResult(builder.build(PublishPost.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (title.getText().toString().length() == 0) {
                    title.setError("Book title is required");
                    titleFilled = false;
                } else {
                    titleFilled = true;
                }
            }
        });
        author.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (author.getText().toString().length() == 0) {
                    author.setError("Author's name is required");
                    authorFilled = false;
                } else {
                    authorFilled = true;
                }
            }
        });
        publisher.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (publisher.getText().toString().length() == 0) {
                    publisher.setError("Publisher's name is required");
                    publisherFilled = false;
                } else {
                    publisherFilled = true;
                }
            }
        });
        publishedDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (publishedDate.getText().toString().length() == 0) {
                    publishedDate.setError("Published date is required");
                    publishedDateFilled = false;
                } else {
                    publishedDateFilled = true;
                }
            }
        });
        post.setOnClickListener(postData);
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = (double) (location.getLatitude());
        double lng = (double) (location.getLongitude());
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String result = null;
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(
                    lat, lng, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                sb.append(address.getLocality()).append("\n");
                sb.append(address.getPostalCode()).append("\n");
                sb.append(address.getCountryName());
                result = sb.toString();
                addressData.setText(result);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.removeUpdates(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place selectedPlace = PlacePicker.getPlace(data, this);
                addressData.setText(selectedPlace.getAddress());
                // Do something with the place
            }
        }
    }

    public void dpd(View v){
        Calendar c=Calendar.getInstance();
        DatePickerDialog dpd=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                publishedDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));

        dpd.show();

    }

    View.OnClickListener postData = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (titleFilled && authorFilled && publisherFilled && publishedDateFilled) {
                String link = "";
                String msg = "";
                msg = "Name of the book : " + title.getText() + "\n" + "Author : " + author.getText() + "\n" +"Publisher : " + publisher.getText() + "\n" + "Published date : " + publishedDate.getText() + "\n";
                if(addressData.getText().toString().length() != 0){
                    msg += "Address : " + addressData.getText();
                }
                final String content = msg;
                Bundle params = new Bundle();
                if (photo != null) {
                    ByteArrayOutputStream blob = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, blob);
                    byte[] byteArray = blob.toByteArray();
                    params.putString("message", content);
                    params.putByteArray("object_attachment", byteArray);
                    link = "/466492673702571" + "/photos";
                } else {
                    params.putString("message", content);
                    link = "/466492673702571" + "/feed";
                }

                //final String userAccessToken = AccessToken.getCurrentAccessToken().getToken();
                //final AccessToken token = new AccessToken("EAAYsTU0FC0EBAEpNbG1CqklpIxibNSX08wCCvOST2td50CBJv7CPXyTYFZAlOINoop7EGw1xXqWHs2js5THDcU6sm0DsF8LZBQUkZCXPfWtKz2ZBg6koaPZB8bZCPmG2bY49EDPilfzdNuEkxRJQLhpFkK0yVfauxzHJoZAajrptHD5NmyuBjY0", AccessToken.getCurrentAccessToken().getApplicationId(), AccessToken.getCurrentAccessToken().getUserId(), Arrays.asList("publish_actions", "manage_pages", "publish_pages"), null, AccessTokenSource.FACEBOOK_APPLICATION_SERVICE, AccessToken.getCurrentAccessToken().getExpires(), AccessToken.getCurrentAccessToken().getLastRefresh());
                //AccessToken.setCurrentAccessToken(token);
                GraphRequest postRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), link, params, HttpMethod.POST, new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        FacebookRequestError error = response.getError();
                        if (error != null) {
                            Log.e("Error", error.getErrorMessage());

                        } else {
                            String postID = "";
                            JSONObject value = response.getJSONObject();
                            try {
                                postID = value.getString("id");
                                pd.setMessage("Loading");
                                pd.show();
                                Intent intent = new Intent(PublishPost.this, FacebookWebview.class);
                                intent.putExtra("id", postID);
                                intent.putExtra("access_token", AccessToken.getCurrentAccessToken().getToken());
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                postRequest.executeAsync();
            } else {
                if(!titleFilled){;
                    title.setError("Book title is required");
                }
                if(!authorFilled){
                    author.setError("Author's name is required");
                }
                if(!publisherFilled){
                    publisher.setError("Publisher's name is required");
                }
                if(!publishedDateFilled){
                    publishedDate.setError("Published date is required");
                }
            }
        }
    };
}

package com.app.charlotte.myapplication;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationRequest;

import java.util.concurrent.TimeUnit;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by charlotte on 19.05.16.
 */
public class LocationHelper {
    private static LocationHelper ourInstance = new LocationHelper();
    private static final long LOCATION_TIMEOUT_IN_SECONDS = 2;
    private ReactiveLocationProvider locationProvider;

    public static final float MIN_DIST_VIEW_LENGTH=3000.0f;
    public static final float MAX_DIST_VIEW_LENGTH=10000.0f;

    public static LocationHelper getInstance()


    {


        return ourInstance;
    }

    private LocationHelper() {


    }

public float computeDistanceFractionForView(float distance){

    Log.d("TAG", "compute distance fraction for view");

    if (distance>=MAX_DIST_VIEW_LENGTH) {
        distance = MAX_DIST_VIEW_LENGTH;
    }
    else if (distance<MIN_DIST_VIEW_LENGTH) {
        distance=MIN_DIST_VIEW_LENGTH;
    }

    Log.d("TAG", "ergebnisdistanz: "+distance/MAX_DIST_VIEW_LENGTH);

    return distance/MAX_DIST_VIEW_LENGTH;


}


    public String computeDistanceString(float distance)
    {

        String  unitString="m", formatString="%.0f";

        if (distance>=1000)
        {
            distance=distance/1000.0f;
            unitString="km";
            formatString="%.1f";
        }

            return "" + String.format(java.util.Locale.US,formatString,distance)+ " "+unitString;
    }





    public  boolean isAllowLocation() {
        return allowLocation;
    }

    public  void setAllowLocation(boolean allowLocation) {
        allowLocation = allowLocation;
    }

    private  boolean allowLocation = true;


    public  void determineLocation(Context applicationContext, Context activityContext,
                                         final LocationFetchedInteface locationFetchedInteface) {
        locationProvider = new ReactiveLocationProvider(activityContext);


        if (!(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext)== ConnectionResult.SUCCESS) && isAllowLocation()) {
            SingleShotLocationProvider.requestSingleUpdate(activityContext,
                    new SingleShotLocationProvider.LocationCallback() {
                        @Override
                        public void onNewLocationAvailable(Location location) {
                            locationFetchedInteface.hasFetchedLocation(location);
                            Log.d("Location", "my senderLocation is " + location.getLatitude() + " " + location.getLongitude());
                        }
                    });

        }
        else if (isAllowLocation()) {

            LocationRequest req = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                    .setExpirationDuration(TimeUnit.SECONDS.toMillis(LOCATION_TIMEOUT_IN_SECONDS));

            final Observable<Location> goodEnoughQuicklyOrNothingObservable = locationProvider.getUpdatedLocation(req)
               /* .filter(new Func1<Location, Boolean>() {
                    @Override
                    public Boolean call(Location senderLocation) {
                        return senderLocation.getAccuracy() < SUFFICIENT_ACCURACY;
                    }
                })*/
                    .timeout(LOCATION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS, Observable.just((Location) null), AndroidSchedulers.mainThread())
                    .first()
                    .observeOn(AndroidSchedulers.mainThread());


            Subscription subscription= goodEnoughQuicklyOrNothingObservable.subscribe(new Action1<Location>() {
                @Override
                public void call(Location location) {
                    if (location!=null) {
                        Log.d("TAG", "senderLocation: " + location.getLatitude() + location.getLongitude());

                    }
                    locationFetchedInteface.hasFetchedLocation(location);
                }
            });
        }
    }
}

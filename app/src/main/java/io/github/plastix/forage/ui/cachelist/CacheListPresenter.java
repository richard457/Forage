package io.github.plastix.forage.ui.cachelist;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;

import java.util.List;

import javax.inject.Inject;

import io.github.plastix.forage.data.api.OkApiInteractor;
import io.github.plastix.forage.data.local.Cache;
import io.github.plastix.forage.data.local.DatabaseInteractor;
import io.github.plastix.forage.data.location.LocationInteractor;
import io.github.plastix.forage.data.network.NetworkInteractor;
import io.github.plastix.forage.ui.LifecycleCallbacks;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

public class CacheListPresenter implements LifecycleCallbacks {

    private static final double NEARBY_CACHE_RADIUS_MILES = 100;

    private CacheListView view;
    private OkApiInteractor apiInteractor;
    private DatabaseInteractor databaseInteractor;
    private LocationInteractor locationInteractor;
    private NetworkInteractor networkInteractor;
    private Subscription subscription;


    @Inject
    public CacheListPresenter(CacheListView view, OkApiInteractor apiInteractor,
                              DatabaseInteractor databaseInteractor,
                              LocationInteractor locationInteractor,
                              NetworkInteractor networkInteractor) {
        this.view = view;
        this.apiInteractor = apiInteractor;
        this.databaseInteractor = databaseInteractor;
        this.locationInteractor = locationInteractor;
        this.networkInteractor = networkInteractor;
        initalizeSubscription();
    }

    private void initalizeSubscription() {
        this.subscription = Subscriptions.empty();
    }

    public void fetchGeocaches() {
        cancelRequest();

        if (!networkInteractor.hasInternetConnection()) {
            view.onErrorInternet();
        } else if (!locationInteractor.isLocationAvailable()) {
            view.onErrorLocation();
        } else {
            this.subscription = locationInteractor.getUpdatedLocation().flatMap(new Func1<Location, Single<List<Cache>>>() {
                @Override
                public Single<List<Cache>> call(Location location) {
                    return apiInteractor.getNearbyCaches(location, NEARBY_CACHE_RADIUS_MILES);
                }
            }).subscribe(new SingleSubscriber<List<Cache>>() {
                @Override
                public void onSuccess(List<Cache> value) {
                    databaseInteractor.clearAndSaveGeocaches(value);
                }

                @Override
                public void onError(Throwable error) {
                    Log.e("error", error.getMessage(), error);
                    view.onErrorFetch();
                }
            });

        }
    }

    public void cancelRequest() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public void clearCaches() {
        databaseInteractor.clearGeocaches();
    }

    @Override
    public void onStart() {
        locationInteractor.onStart();
    }

    @Override
    public void onStop() {
        locationInteractor.onStop();
    }

    @Override
    public void onResume() {

    }


}

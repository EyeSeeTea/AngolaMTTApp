package org.eyeseetea.malariacare.data.sdk;

import java.util.Set;

/**
 * Created by idelcano on 15/11/2016.
 */

public class SdkPushController extends SdkController{
    /**
     * Push the events in sdk database
     * This method is called before the conversion from surveys to sdk events
     */
    public static void sendEventChanges() {
        /*
        final Set<String> eventUids= new HashSet<>();
        for(EventFlow eventFlow:SdkQueries.getEvents()){
            eventUids.add(eventFlow.getUId());
        }
        Observable<Map<Event,ImportSummary>> eventObserver =
                D2.events().push(eventUids);
        eventObserver.
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<Event,ImportSummary>>() {
                    @Override
                    public void call(Map<Event,ImportSummary> mapEventsImportSummary) {
                        Log.d(TAG, "Push of events finish. Number of events: "+mapEventsImportSummary.size());
                        PushController.converter.saveSurveyStatus(mapEventsImportSummary);
                        PushController.postFinish(true);
                        PushController.isPushing = false;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        errorOnPushEvents(eventUids);
                        PushController.postFinish(false);
                        PushController.isPushing = false;
                        throwable.printStackTrace();
                        Log.e(TAG,
                                "Error pushing Events: " + throwable.getLocalizedMessage());
                    }
                });
        */
    }

    private static void errorOnPushEvents(Set<String> eventUids) {
        //// FIXME: 24/11/201
        //The quarantine workflow is only in pictureapp.
        /*

        PushController.setSurveysAsQuarantine();
        */
    }

}

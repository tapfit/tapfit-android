package co.tapfit.android.helper;

import android.content.Context;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zackmartinsek on 9/30/13.
 */
public class Search {

    public static final String TIME_MORNING = "time_morning";
    public static final String TIME_AFTERNOON = "time_afternoon";
    public static final String TIME_EVENING = "time_evening";

    public static final String WORKOUT_YOGA = "workout_yoga";
    public static final String WORKOUT_PILATES = "workout_pilates";
    public static final String WORKOUT_SPIN = "workout_spin";
    public static final String WORKOUT_CROSSFIT = "workout_crossfit";
    public static final String WORKOUT_DANCE = "workout_dance";
    public static final String WORKOUT_WEIGHTS = "workout_weights";

    public static final String PRICE_LOW = "price_low";
    public static final String PRICE_MED = "price_med";
    public static final String PRICE_HIGH = "price_high";

    private static Search instance;
    private static Context mContext;

    public static Search getInstance(Context context) {
        if (instance == null) {
            instance = new Search(context);
        }
        return instance;
    }

    private Search(Context context) {
        mContext = context;
    }

    public static HashMap<DateTime, DateTime> getWorkoutTimes() {

        HashMap<DateTime, DateTime> timeInterval = new HashMap<DateTime, DateTime>();

        DateTime now = DateTime.now();
        DateTime morningStart = new DateTime(now.year().get(), now.monthOfYear().get(), now.dayOfMonth().get(), 4, 0, 0, 0);
        DateTime morningEnd = morningStart.plusHours(7);
        DateTime afternoonStart = morningEnd.plusMillis(0);
        DateTime afternoonEnd = afternoonStart.plusHours(6);
        DateTime eveningStart = afternoonEnd.plusMillis(0);
        DateTime eveningEnd = eveningStart.plusHours(6);

        DateTime tomorrow = now.plusDays(1);

        if (SharePref.getBooleanPref(mContext, TIME_MORNING, false)) {
            timeInterval.put(morningStart, morningEnd);
            timeInterval.put(morningStart.plusDays(1), morningEnd.plusDays(1));
        }
        if (SharePref.getBooleanPref(mContext, TIME_AFTERNOON, false)) {
            timeInterval.put(afternoonStart, afternoonEnd);
            timeInterval.put(afternoonStart.plusDays(1), afternoonEnd.plusDays(1));
        }
        if (SharePref.getBooleanPref(mContext, TIME_EVENING, false)) {
            timeInterval.put(eveningStart, eveningEnd);
            timeInterval.put(eveningStart.plusDays(1), eveningEnd.plusDays(1));
        }

        if (timeInterval.size() < 1) {
            timeInterval = null;
        }

        return timeInterval;
    }

    public static List<Range<Double>> getPriceRanges() {

        List<Range<Double>> priceRanges = new ArrayList<Range<Double>>();

        if (SharePref.getBooleanPref(mContext, PRICE_LOW, false)) {
            Range<Double> range = new Range<Double>();
            range.low = 0.0;
            range.high = 10.0;
            priceRanges.add(range);
        }
        if (SharePref.getBooleanPref(mContext, PRICE_MED, false)) {
            Range<Double> range = new Range<Double>();
            range.low = 10.0;
            range.high = 20.0;
            priceRanges.add(range);
        }
        if (SharePref.getBooleanPref(mContext, PRICE_HIGH, false)) {
            Range<Double> range = new Range<Double>();
            range.low = 20.0;
            range.high = 100.0;
            priceRanges.add(range);
        }

        if (priceRanges.size() < 1) {
            priceRanges = null;
        }

        return priceRanges;
    }

    public static List<String> getCategoryStrings() {

        List<String> categories = new ArrayList<String>();

        if (SharePref.getBooleanPref(mContext, WORKOUT_YOGA, false)) {
            categories.add("Yoga");
            categories.add("Yoga Meditation Stretch");
            categories.add("Hot Yoga");
            categories.add("Power Yoga");
        }

        if (SharePref.getBooleanPref(mContext, WORKOUT_CROSSFIT, false)) {
            categories.add("Strength Conditioning");
            categories.add("Strength");
            categories.add("Boot Camp");
        }

        if (SharePref.getBooleanPref(mContext, WORKOUT_PILATES, false)) {
            categories.add("Pilates Barre");
            categories.add("Barre");
        }

        if (SharePref.getBooleanPref(mContext, WORKOUT_WEIGHTS, false)) {
            categories.add("Gym");
            categories.add("Equipment");
        }

        if (SharePref.getBooleanPref(mContext, WORKOUT_DANCE, false)) {
            categories.add("Dance");
        }

        if (SharePref.getBooleanPref(mContext, WORKOUT_SPIN, false)) {
            categories.add("Cardio");
        }

        if (categories.size() < 1) {
            categories = null;
        }

        return categories;
    }
}

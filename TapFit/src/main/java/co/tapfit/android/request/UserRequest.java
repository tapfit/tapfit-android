package co.tapfit.android.request;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import co.tapfit.android.helper.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.internal.cu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;

import co.tapfit.android.R;
import co.tapfit.android.database.DatabaseHelper;
import co.tapfit.android.helper.DateTimeDeserializer;
import co.tapfit.android.helper.SharePref;
import co.tapfit.android.model.ClassTime;
import co.tapfit.android.model.CreditCard;
import co.tapfit.android.model.Pass;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.User;
import co.tapfit.android.model.Workout;
import co.tapfit.android.service.ApiService;

/**
 * Created by zackmartinsek on 9/8/13.
 */
public class UserRequest extends Request {

    private static String TAG = UserRequest.class.getSimpleName();

    public static void registerGuest(final Context context, final ResponseCallback callback) {

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        ResultReceiver receiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();

                    JsonParser parser = new JsonParser();

                    User user = null;

                    try
                    {
                        JsonElement element = parser.parse(json);

                        user = gson.fromJson(element, User.class);

                        dbWrapper.createOrUpdateUser(user);

                        Log.d(TAG, "Registered a guest user: " + user.auth_token);

                        SharePref.setIntPref(context, SharePref.CURRENT_USER_ID, user.id);

                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
                        if (callback != null) {
                            callback.sendCallback(null, "Failed to register user");
                        }
                    }

                    if (callback != null)
                    {
                        if (user == null)
                        {
                            callback.sendCallback(user, "Failed to register user");
                        }
                        else
                        {
                            callback.sendCallback(user, "Success registering user");
                        }
                    }
                }
            }
        };

        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(ApiService.URL, getUrl(context) + "users/guest");
        intent.putExtra(ApiService.HTTP_VERB, ApiService.POST);
        intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

        context.startService(intent);
    }

    public static final String EMAIL = "user[email]";
    public static final String PASSWORD = "user[password]";
    public static final String FIRST_NAME = "user[first_name]";
    public static final String LAST_NAME = "user[last_name]";

    public static void registerUser(final Context context, Bundle args, final ResponseCallback callback) {

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        ResultReceiver receiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();

                    JsonParser parser = new JsonParser();

                    User user = null;

                    try
                    {
                        JsonElement element = parser.parse(json);

                        JsonElement errors = element.getAsJsonObject().get("error");

                        if (errors != null)
                        {
                            callback.sendCallback(null, errors.getAsString());
                            return;
                        }

                        user = gson.fromJson(element, User.class);

                        dbWrapper.createOrUpdateUser(user);

                        SharePref.setIntPref(context, SharePref.CURRENT_USER_ID, user.id);

                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
                    }

                    if (callback != null)
                    {
                        if (user == null)
                        {
                            callback.sendCallback(user, "Failed to register user");
                        }
                        else
                        {
                            callback.sendCallback(user, "Success registering user");
                        }
                    }
                }
            }

        };

        User user = dbWrapper.getCurrentUser();
        if (user != null && user.is_guest) {
            args.putString(AUTH_TOKEN, user.auth_token);
        }
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(ApiService.URL, getUrl(context) + "users/register");
        intent.putExtra(ApiService.HTTP_VERB, ApiService.POST);
        intent.putExtra(ApiService.PARAMS, args);
        intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

        context.startService(intent);

    }

    public static void redeemPromoCode(final Context context, String promoCode, final ResponseCallback callback) {

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        ResultReceiver receiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();

                    JsonParser parser = new JsonParser();

                    User user = null;

                    String message = "Error requesting Promo Code";

                    try
                    {
                        JsonElement element = parser.parse(json);

                        Log.d(TAG, "userJson: " + json);

                        JsonElement userElement = element.getAsJsonObject().get("user");
                        if (userElement != null) {

                            user = gson.fromJson(userElement, User.class);

                            dbWrapper.createOrUpdateUser(user);

                            Log.d(TAG, "userCredits: " + user.credit_amount);

                            message = "Success getting promo code";
                        }
                        else
                        {
                            JsonElement errorElement = element.getAsJsonObject().get("error");
                            if (errorElement != null) {
                                message = errorElement.getAsString();
                            }
                        }

                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
                        if (callback != null) {
                            callback.sendCallback(null, "Failed to register user");
                        }
                    }

                    if (callback != null)
                    {
                        if (user == null)
                        {
                            callback.sendCallback(user, message);
                        }
                        else
                        {
                            callback.sendCallback(user, message);
                        }
                    }
                }
            }

        };

        User user = dbWrapper.getCurrentUser();
        if (user != null && user.is_guest == false)
        {

            Bundle args = new Bundle();

            args.putString("promo_code", promoCode);
            args.putString(AUTH_TOKEN, user.auth_token);

            Intent intent = new Intent(context, ApiService.class);
            intent.putExtra(ApiService.URL, getUrl(context) + "me/promo_codes");
            intent.putExtra(ApiService.HTTP_VERB, ApiService.POST);
            intent.putExtra(ApiService.PARAMS, args);
            intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

            context.startService(intent);
        }
        else
        {
            if (callback != null) {
                callback.sendCallback(null, "Need to register");
            }
        }
    }

    public static final String LOGIN_EMAIL = "email";
    public static final String LOGIN_PASSWORD = "password";

    public static void loginUser(final Context context, Bundle args, final ResponseCallback callback) {

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        ResultReceiver receiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();

                    JsonParser parser = new JsonParser();

                    User user = null;

                    try
                    {
                        JsonElement element = parser.parse(json);

                        JsonElement errors = element.getAsJsonObject().get("errors");

                        if (errors != null)
                        {
                            if (callback != null)
                                callback.sendCallback(null, errors.getAsString());
                            return;
                        }

                        user = gson.fromJson(element, User.class);

                        dbWrapper.createOrUpdateUser(user);

                        SharePref.setIntPref(context, SharePref.CURRENT_USER_ID, user.id);

                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
                    }

                    if (callback != null)
                    {
                        if (user == null)
                        {
                            callback.sendCallback(user, "Failed to login user");
                        }
                        else
                        {
                            callback.sendCallback(user, "Success loging in user");
                            UserRequest.favorites(context, null);
                        }
                    }

                    UserRequest.getPaymentMethods(context, null);
                    UserRequest.getPasses(context, null);
                }
            }

        };



        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(ApiService.URL, getUrl(context) + "users/login");
        intent.putExtra(ApiService.HTTP_VERB, ApiService.POST);
        intent.putExtra(ApiService.PARAMS, args);
        intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

        context.startService(intent);

    }

    public static void logoutUser(final Context context, final ResponseCallback callback) {

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        ResultReceiver receiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    User user = dbWrapper.getCurrentUser();

                    SharePref.setIntPref(context, SharePref.CURRENT_USER_ID, -1);

                    if (callback != null)
                    {
                        callback.sendCallback(user, "Logged out user");
                    }
                }
            }

        };


        User user = dbWrapper.getCurrentUser();
        if (user != null) {

            Bundle args = new Bundle();

            Intent intent = new Intent(context, ApiService.class);
            intent.putExtra(ApiService.URL, getUrl(context) + "users/login");
            intent.putExtra(ApiService.HTTP_VERB, ApiService.POST);
            intent.putExtra(ApiService.PARAMS, args);
            intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

            context.startService(intent);
        }
        else {
            if (callback != null) {
                callback.sendCallback(user, "Already logged out");
            }
        }
    }

    public static final String TOKEN = "token";

    public static void setDefaultCard(final Context context, final CreditCard card, final ResponseCallback callback) {

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        final ResultReceiver receiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();

                    JsonParser parser = new JsonParser();

                    try
                    {
                        JsonObject object = parser.parse(json).getAsJsonObject();

                        if (object.get("success").getAsBoolean()){

                            dbWrapper.setDefaultCardForUser(dbWrapper.getCurrentUser(), card);
                        }
                        else {
                            if (callback != null) {
                                callback.sendCallback(null, object.get("error_message").getAsString());
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
                    }

                    if (callback != null) {
                        callback.sendCallback(dbWrapper.getDefaulCard(dbWrapper.getCurrentUser()), "Success setting default card");
                    }
                }
            }
        };

        User currentUser = dbWrapper.getCurrentUser();

        if (currentUser != null) {

            Bundle args = new Bundle();
            args.putString(AUTH_TOKEN, currentUser.auth_token);
            args.putString(TOKEN, card.token);

            Intent intent = new Intent(context, ApiService.class);
            intent.putExtra(ApiService.URL, getUrl(context) + "me/payments/default");
            intent.putExtra(ApiService.HTTP_VERB, ApiService.PUT);
            intent.putExtra(ApiService.PARAMS, args);
            intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

            context.startService(intent);
        } else {
            if (callback != null) {
                callback.sendCallback(null, "Need to sign in");
            }
        }
    }

    public static void deleteCreditCard(final Context context, final CreditCard card, final ResponseCallback callback) {

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        final ResultReceiver receiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();

                    JsonParser parser = new JsonParser();

                    try
                    {
                        JsonObject object = parser.parse(json).getAsJsonObject();

                        if (object.get("success").getAsBoolean()){

                            object.get("default_card").getAsString();

                            dbWrapper.setDefaultCardFromToken(object.get("default_card").getAsString());

                            dbWrapper.deleteCreditCard(card);
                        }
                        else {
                            if (callback != null) {
                                callback.sendCallback(null, object.get("error_message").getAsString());
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
                    }

                    if (callback != null) {
                        callback.sendCallback(card, "Success deleting card");
                    }
                }
            }
        };

        User currentUser = dbWrapper.getCurrentUser();

        if (currentUser != null) {

            Bundle args = new Bundle();
            args.putString(AUTH_TOKEN, currentUser.auth_token);
            args.putString(TOKEN, card.token);

            Intent intent = new Intent(context, ApiService.class);
            intent.putExtra(ApiService.URL, getUrl(context) + "me/payments/delete");
            intent.putExtra(ApiService.HTTP_VERB, ApiService.DELETE);
            intent.putExtra(ApiService.PARAMS, args);
            intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

            context.startService(intent);
        } else {
            if (callback != null) {
                callback.sendCallback(null, "Need to sign in");
            }
        }
    }

    public static void addPaymentMethod(final Context context, Bundle args, final ResponseCallback callback) {
        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        final ResultReceiver receiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();

                    JsonParser parser = new JsonParser();

                    try
                    {
                        JsonObject object = parser.parse(json).getAsJsonObject();

                        if (object.get("success").getAsBoolean()){
                            CreditCard creditCard = gson.fromJson(object, CreditCard.class);
                            creditCard.token = object.get("credit_card").getAsString();

                            creditCard.default_card = true;

                            dbWrapper.addCreditCardToUser(dbWrapper.getCurrentUser(), creditCard);
                        }
                        else {
                            if (callback != null) {
                                callback.sendCallback(null, object.get("error_message").getAsString());
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
                    }

                    if (callback != null) {
                        callback.sendCallback(dbWrapper.getDefaulCard(dbWrapper.getCurrentUser()), "Success adding card");
                    }
                }
            }

        };

        User currentUser = dbWrapper.getCurrentUser();

        if (currentUser != null) {

            args.putString(AUTH_TOKEN, currentUser.auth_token);

            Intent intent = new Intent(context, ApiService.class);
            intent.putExtra(ApiService.URL, getUrl(context) + "me/payments");
            intent.putExtra(ApiService.HTTP_VERB, ApiService.POST);
            intent.putExtra(ApiService.PARAMS, args);
            intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

            context.startService(intent);
        } else {
            if (callback != null) {
                callback.sendCallback(null, "Need to sign in");
            }
        }
    }

    public static void favorites(final Context context, final ResponseCallback callback) {

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        final ResultReceiver receiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();

                    JsonParser parser = new JsonParser();

                    try
                    {
                        JsonObject object = parser.parse(json).getAsJsonObject();

                        JsonArray array = object.getAsJsonArray("places");

                        User currentUser = dbWrapper.getCurrentUser();

                        for (JsonElement element : array)
                        {
                            Place place = gson.fromJson(element, Place.class);

                            for (JsonElement time : element.getAsJsonObject().getAsJsonArray("class_times")) {
                                DateTime dateTime = DateTime.parse(time.getAsString());

                                ClassTime classTime = new ClassTime(dateTime);
                                dbWrapper.createClassTime(classTime);
                                place.addClassTime(dbWrapper, classTime);
                            }

                            dbWrapper.createOrUpdatePlace(place);

                            dbWrapper.addPlaceToFavorites(currentUser, place);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
                    }

                    if (callback != null) {
                        callback.sendCallback(dbWrapper.getFavorites(), "Success getting favorites");
                    }
                }
            }

        };

        User currentUser = dbWrapper.getCurrentUser();

        if (currentUser != null) {

            Bundle args = new Bundle();
            args.putString(AUTH_TOKEN, currentUser.auth_token);

            Intent intent = new Intent(context, ApiService.class);
            intent.putExtra(ApiService.URL, getUrl(context) + "me/favorites");
            intent.putExtra(ApiService.HTTP_VERB, ApiService.GET);
            intent.putExtra(ApiService.PARAMS, args);
            intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

            context.startService(intent);
        } else {
            if (callback != null) {
                callback.sendCallback(null, "Need to sign in");
            }
        }

    }

    public static void getPaymentMethods(final Context context, final ResponseCallback callback) {

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        final ResultReceiver receiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();

                    JsonParser parser = new JsonParser();

                    try
                    {
                        JsonObject object = parser.parse(json).getAsJsonObject();

                        JsonArray array = object.getAsJsonArray("credit_cards");

                        String default_card = object.getAsJsonPrimitive("default").getAsString();

                        User currentUser = dbWrapper.getCurrentUser();

                        for (JsonElement element : array)
                        {
                            CreditCard creditCard = gson.fromJson(element, CreditCard.class);

                            if (default_card.equals(creditCard.token)) {
                                creditCard.default_card = true;
                            }
                            else {
                                creditCard.default_card = false;
                            }

                            dbWrapper.addCreditCardToUser(currentUser, creditCard);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
                        if (callback != null) {
                            callback.sendCallback(null, "Failed getting creditCards");
                        }
                    }

                    if (callback != null) {
                        callback.sendCallback(dbWrapper.getCurrentUser().credit_cards, "Success getting creditCards");
                    }
                }
            }

        };

        User currentUser = dbWrapper.getCurrentUser();

        if (currentUser != null) {

            Bundle args = new Bundle();
            args.putString(AUTH_TOKEN, currentUser.auth_token);

            Intent intent = new Intent(context, ApiService.class);
            intent.putExtra(ApiService.URL, getUrl(context) + "me/payments");
            intent.putExtra(ApiService.HTTP_VERB, ApiService.GET);
            intent.putExtra(ApiService.PARAMS, args);
            intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

            context.startService(intent);
        } else {
            if (callback != null) {
                callback.sendCallback(null, "Need to sign in");
            }
        }

    }

    public static void getMyInfo(final Context context, final ResponseCallback callback) {

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        final ResultReceiver receiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();

                    JsonParser parser = new JsonParser();

                    try
                    {
                        JsonObject object = parser.parse(json).getAsJsonObject();

                        JsonElement element = object.get("user");

                        if (element != null) {
                            User user = gson.fromJson(element, User.class);

                            dbWrapper.createOrUpdateUser(user);

                            SharePref.setIntPref(context, SharePref.CURRENT_USER_ID, user.id);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
                    }

                    if (callback != null) {
                        callback.sendCallback(dbWrapper.getCurrentUser(), "Success getting my info");
                    }
                }
            }

        };

        User currentUser = dbWrapper.getCurrentUser();

        if (currentUser != null) {

            Bundle args = new Bundle();
            args.putString(AUTH_TOKEN, currentUser.auth_token);

            Intent intent = new Intent(context, ApiService.class);
            intent.putExtra(ApiService.URL, getUrl(context) + "me");
            intent.putExtra(ApiService.HTTP_VERB, ApiService.GET);
            intent.putExtra(ApiService.PARAMS, args);
            intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

            context.startService(intent);
        } else {
            if (callback != null) {
                callback.sendCallback(null, "Need to sign in");
            }
        }

    }

    public static void getPasses(final Context context, final ResponseCallback callback) {

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        final ResultReceiver receiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();

                    JsonParser parser = new JsonParser();

                    try
                    {
                        JsonObject object = parser.parse(json).getAsJsonObject();

                        JsonArray array = object.getAsJsonArray("receipts");

                        User currentUser = dbWrapper.getCurrentUser();

                        for (JsonElement element : array)
                        {
                            Pass pass = parsePassJson(element);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
                    }

                    if (callback != null) {
                        callback.sendCallback(dbWrapper.getPasses(), "Success getting favorites");
                    }
                }
            }

        };

        User currentUser = dbWrapper.getCurrentUser();

        if (currentUser != null) {

            Bundle args = new Bundle();
            args.putString(AUTH_TOKEN, currentUser.auth_token);

            Intent intent = new Intent(context, ApiService.class);
            intent.putExtra(ApiService.URL, getUrl(context) + "me/receipts");
            intent.putExtra(ApiService.HTTP_VERB, ApiService.GET);
            intent.putExtra(ApiService.PARAMS, args);
            intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

            context.startService(intent);
        } else {
            if (callback != null) {
                callback.sendCallback(null, "Need to sign in");
            }
        }

    }

    public static Pass parsePassJson(JsonElement element) {
        Pass pass = gson.fromJson(element, Pass.class);

        Workout workout = gson.fromJson(element.getAsJsonObject().get("workout_json"), Workout.class);

        Place place = PlaceRequest.parsePlaceJson(element.getAsJsonObject().get("place_json"));

        pass.workout = workout;
        pass.place = place;
        pass.user = dbWrapper.getCurrentUser();

        dbWrapper.createOrUpdatePass(pass);

        return pass;
    }

}

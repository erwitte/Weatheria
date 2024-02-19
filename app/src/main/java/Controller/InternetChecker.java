package Controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;


/**
 * Checks the availability of an internet connection on the device.
 */
public class InternetChecker {
    private final Context context;

    /**
     * Initializes a new instance of InternetChecker with the application's context.
     *
     * @param context The application's context.
     */
    public InternetChecker(Context context){
        this.context = context;
    }

    /**
     * Determines whether the device currently has an active internet connection.
     *
     * This method checks for both the presence of a network connection and whether that
     * connection has internet access and is validated (e.g., not a captive portal).
     *
     * @return {@code true} if the device has an active internet connection, {@code false} otherwise.
     */
    public boolean hasInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) {
            return false;
        }
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
        if (networkCapabilities == null) {
            return false;
        }
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }
}

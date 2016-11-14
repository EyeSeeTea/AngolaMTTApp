package org.eyeseetea.malariacare.strategies;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.utils.PreferencesState;

public class LoginActivityStrategy extends ALoginActivityStrategy {
    public LoginActivityStrategy(LoginActivity loginActivity) {
        super(loginActivity);
    }

    @Override
    public void onBackPressed() {
        loginActivity.onBackPressed();
    }

    @Override
    public void saveUserCredentials(String serverUrl, String username, String password) {
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_url, serverUrl);
    }
}

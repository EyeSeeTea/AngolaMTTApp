package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.fragments.DashboardUnsentFragment;
import org.eyeseetea.malariacare.fragments.StockSurveysFragment;

/**
 * Created by manuel on 28/12/16.
 */

public class DashboardActivityStrategy extends ADashboardActivityStrategy {

    DashboardActivity mDashboardActivity;
    StockSurveysFragment stockFragment;

    public DashboardActivityStrategy(DashboardActivity dashboardActivity) {
        super(dashboardActivity);
        mDashboardActivity = dashboardActivity;
    }

    @Override
    public void reloadAVFragment() {

    }

    @Override
    public void reloadStockFragment(Activity activity) {
        if (stockFragment != null && stockFragment.isAdded()) {
            stockFragment.reloadHeader(activity);
            stockFragment.reloadData();
        } else {
            showStockFragment(activity, false);
        }
    }

    @Override
    public boolean showStockFragment(Activity activity, boolean isMoveToLeft) {
        stockFragment = new StockSurveysFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(DashboardUnsentFragmentStrategy.IS_STOCK_FRAGMENT, true);
        stockFragment.setArguments(bundle);
        stockFragment.reloadData();
        mDashboardActivity.replaceFragment(R.id.dashboard_stock_container,
                stockFragment);
        stockFragment.reloadHeader(activity);

        return isMoveToLeft;
    }

    @Override
    public void newSurvey(Activity activity) {
        ProgramDB program = new Select().from(ProgramDB.class).querySingle();
        // Put new survey in session
        String orgUnitUid = OrgUnitDB.findUIDByName(PreferencesState.getInstance().getOrgUnit());
        OrgUnitDB orgUnit = OrgUnitDB.findByUID(orgUnitUid);
        SurveyDB survey = new SurveyDB(orgUnit, program, Session.getUserDB());
        survey.save();
        Session.setMalariaSurveyDB(survey);
        //Look for coordinates
        prepareLocationListener(activity, survey);
        activity.findViewById(R.id.common_header).setVisibility(View.GONE);

        mDashboardActivity.initSurvey();
    }

    @Override
    public void sendSurvey() {
        Session.getMalariaSurveyDB().updateSurveyStatus();
        mDashboardActivity.findViewById(R.id.common_header).setVisibility(View.VISIBLE);
    }

    @Override
    public boolean beforeExit(boolean isBackPressed) {
        SurveyDB malariaSurvey = Session.getMalariaSurveyDB();
        if (malariaSurvey != null) {
            boolean isMalariaInProgress = malariaSurvey.isInProgress();
            malariaSurvey.getValuesFromDB();
            //Exit + InProgress -> delete
            if (isBackPressed && isMalariaInProgress) {
                if (isMalariaInProgress) {
                    mDashboardActivity.findViewById(R.id.common_header).setVisibility(View.VISIBLE);
                    Session.setMalariaSurveyDB(null);
                    malariaSurvey.delete();
                }
                isBackPressed = false;
            }
        }
        return isBackPressed;
    }

    @Override
    public void completeSurvey() {
        Session.getMalariaSurveyDB().updateSurveyStatus();
    }

    @Override
    public boolean isHistoricNewReceiptBalanceFragment(Activity activity) {
        return false;
    }

    public static void onLogoutSuccess() {
        DashboardActivity.dashboardActivity.finishAndGo(SettingsActivity.class);
    }
    public void openSentSurvey() {
        mDashboardActivity.getTabHost().setCurrentTabByTag(
                mDashboardActivity.getResources().getString(R.string.tab_tag_assess));
        mDashboardActivity.findViewById(R.id.common_header).setVisibility(View.GONE);
        mDashboardActivity.initSurvey();

    }
}

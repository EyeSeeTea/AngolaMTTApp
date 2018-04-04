package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.data.database.datasources.ProgramLocalDataSource;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.exception.LoadingNavigationControllerException;
import org.eyeseetea.malariacare.domain.usecase.GetUserProgramUIDUseCase;
import org.eyeseetea.malariacare.domain.usecase.HasToGenerateStockProgramUseCase;
import org.eyeseetea.malariacare.fragments.AddBalanceReceiptFragment;
import org.eyeseetea.malariacare.fragments.StockSurveysFragment;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationBuilder;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.GradleVariantConfig;

import java.util.Date;

/**
 * Created by manuel on 28/12/16.
 */

public class DashboardActivityStrategy extends ADashboardActivityStrategy {

    DashboardActivity mDashboardActivity;
    StockSurveysFragment stockFragment;
    private boolean showStock;

    public DashboardActivityStrategy(DashboardActivity dashboardActivity) {
        super(dashboardActivity);
        mDashboardActivity = dashboardActivity;
        showStock = false;
    }

    @Override
    public void reloadAVFragment() {

    }

    @Override
    public void reloadStockFragment(Activity activity) {
        if (showStock) {
            if (stockFragment != null && stockFragment.isAdded()) {
                stockFragment.reloadHeader(activity);
                stockFragment.reloadData();
            } else {
                showStockFragment(activity, false);
            }
        }
    }

    @Override
    public boolean showStockFragment(Activity activity, boolean isMoveToLeft) {
        if (showStock) {
            if (stockFragment == null) {
                stockFragment = new StockSurveysFragment();
            }
            mDashboardActivity.replaceFragment(R.id.dashboard_stock_container,
                    stockFragment);
            stockFragment.reloadData();
            stockFragment.reloadHeader(activity);
        }
        return isMoveToLeft;
    }

    @Override
    public void newSurvey(Activity activity) {

        IProgramRepository programRepository = new ProgramLocalDataSource();
        Program userProgram  = programRepository.getUserProgram();
        ProgramDB program = ProgramDB.findByName(userProgram.getCode());

        TabDB userProgramTab =  TabDB.findTabByProgram(program.getId_program()).get(0);
        try {
            NavigationBuilder.getInstance().buildController(userProgramTab);
        } catch (LoadingNavigationControllerException e) {
            e.printStackTrace();
        }

        // Put new survey in session
        String orgUnitUid = OrgUnitDB.findUIDByName(PreferencesState.getInstance().getOrgUnit());
        OrgUnitDB orgUnit = OrgUnitDB.findByUID(orgUnitUid);
        SurveyDB survey = new SurveyDB(orgUnit, program, Session.getUserDB());
        survey.save();
        Session.setMalariaSurveyDB(survey);
        createStockProgramIfNecessary(activity, survey, orgUnit);
        //Look for coordinates
        prepareLocationListener(activity, survey);
        activity.findViewById(R.id.common_header).setVisibility(View.GONE);

        mDashboardActivity.initSurvey();
    }

    private void createStockProgramIfNecessary(final Activity activity, final SurveyDB malariaSurvey,
            final OrgUnitDB orgUnit) {
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IProgramRepository programRepository = new ProgramLocalDataSource();
        HasToGenerateStockProgramUseCase hasToGenerateStockProgramUseCase =
                new HasToGenerateStockProgramUseCase(mainExecutor, asyncExecutor,
                        programRepository);
        hasToGenerateStockProgramUseCase.execute(malariaSurvey.getProgramDB().getUid(),
                new HasToGenerateStockProgramUseCase.Callback() {
                    @Override
                    public void hasToCreateStock(boolean create) {
                        if (create) {
                            generateStockProgram(malariaSurvey.getEventDate(), orgUnit, activity);
                        }
                    }
                });
    }

    private void generateStockProgram(Date eventDate,
            OrgUnitDB orgUnit,Activity activity) {
        ProgramDB stockProgram = ProgramDB.findByUID(
                activity.getString(R.string.stock_program_uid));
        SurveyDB stockSurvey = new SurveyDB(orgUnit, stockProgram, Session.getUserDB(),
                Constants.SURVEY_ISSUE);
        stockSurvey.setEventDate(eventDate);//asociate the malaria survey to the stock survey
        stockSurvey.save();
        Session.setStockSurveyDB(stockSurvey);
    }


    @Override
    public void sendSurvey() {
        Session.getMalariaSurveyDB().updateSurveyStatus();
        if (Session.getStockSurveyDB() != null) {
            Session.getStockSurveyDB().updateSurveyStatus();
        }
        mDashboardActivity.findViewById(R.id.common_header).setVisibility(View.VISIBLE);
    }

    @Override
    public boolean beforeExit(boolean isBackPressed) {
        SurveyDB malariaSurvey = Session.getMalariaSurveyDB();
        SurveyDB stockSurvey = Session.getStockSurveyDB();
        if (malariaSurvey != null) {
            boolean isMalariaInProgress = malariaSurvey.isInProgress();
            malariaSurvey.getValuesFromDB();
            //Exit + InProgress -> delete
            if (isBackPressed && isMalariaInProgress) {
                if (isMalariaInProgress) {
                    mDashboardActivity.findViewById(R.id.common_header).setVisibility(View.VISIBLE);
                    Session.setMalariaSurveyDB(null);
                    malariaSurvey.delete();
                    Session.setStockSurveyDB(null);
                    if (stockSurvey != null) {
                        stockSurvey.delete();
                    }
                }
                isBackPressed = false;
            }
        }
        return isBackPressed;
    }

    @Override
    public void completeSurvey() {
        Session.getMalariaSurveyDB().updateSurveyStatus();
        //Complete stockSurvey
        if (Session.getStockSurveyDB() != null) {
            Session.getStockSurveyDB().updateSurveyStatus();
        }
    }

    @Override
    public boolean isHistoricNewReceiptBalanceFragment(Activity activity) {
        if (isFragmentActive(activity, AddBalanceReceiptFragment.class,
                R.id.dashboard_stock_container)) {
            return true;
        }
        return false;
    }

    private boolean isFragmentActive(Activity activity, Class fragmentClass, int layout) {
        Fragment currentFragment = activity.getFragmentManager().findFragmentById(layout);
        if (currentFragment.getClass().equals(fragmentClass)) {
            return true;
        }
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

    public void initTabWidget(TabHost tabHost, Fragment reviewFragment,
            final Fragment surveyFragment,
            final boolean isReadOnly) {
         /* set tabs in order */
        LayoutUtils.setTabHosts(mDashboardActivity);
        LayoutUtils.setTabDivider(mDashboardActivity);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                //If change of tab from surveyFragment or FeedbackFragment they could be closed.
                if (isSurveyFragmentActive(surveyFragment)) {
                    mDashboardActivity.onSurveyBackPressed();
                }
                if (isReviewFragmentActive(surveyFragment)) {
                    mDashboardActivity.exitReviewOnChangeTab(null);
                }
                if (tabId.equalsIgnoreCase(
                        mDashboardActivity.getResources().getString(R.string.tab_tag_assess))) {
                    if (!isReadOnly) {
                        reloadFirstFragment();
                    }
                    reloadFirstFragmentHeader();
                } else if (tabId.equalsIgnoreCase(
                        mDashboardActivity.getResources().getString(R.string.tab_tag_improve))) {
                    reloadSecondFragment();
                } else if (tabId.equalsIgnoreCase(
                        mDashboardActivity.getResources().getString(R.string.tab_tag_stock))) {
                    reloadStockFragment(mDashboardActivity);
                } else if (tabId.equalsIgnoreCase(
                        mDashboardActivity.getResources().getString(R.string.tab_tag_monitor))) {
                    if (GradleVariantConfig.isMonitoringFragmentActive()) {
                        reloadFourthFragment();
                    }
                } else if (tabId.equalsIgnoreCase(
                        mDashboardActivity.getResources().getString(R.string.tab_tag_av))) {
                    reloadAVFragment();
                }
            }
        });
        // init tabHost
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).setFocusable(false);
            tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_below_line);
        }
        tabHost.getTabWidget().setStripEnabled(true);
        tabHost.getTabWidget().setRightStripDrawable(R.drawable.background_odd);
        tabHost.getTabWidget().setLeftStripDrawable(R.drawable.background_odd);
    }

    @Override
    public void setStockTab(final TabHost tabHost) {
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IProgramRepository programRepository = new ProgramLocalDataSource();
        GetUserProgramUIDUseCase getUserProgramUIDUseCase = new GetUserProgramUIDUseCase(
                programRepository, mainExecutor, asyncExecutor);
        final HasToGenerateStockProgramUseCase hasToGenerateStockProgramUseCase =
                new HasToGenerateStockProgramUseCase(mainExecutor, asyncExecutor,
                        programRepository);
        getUserProgramUIDUseCase.execute(new GetUserProgramUIDUseCase.Callback() {
            @Override
            public void onSuccess(String uid) {
                hasToGenerateStockProgramUseCase.execute(uid,
                        new HasToGenerateStockProgramUseCase.Callback() {
                            @Override
                            public void hasToCreateStock(boolean create) {
                                showStock = create;
                                if (create) {
                                    addStockTab(tabHost);
                                }
                            }
                        });
            }

            @Override
            public void onError() {
                Log.e(getClass().getName(), "Error getting program");
            }
        });
    }

    private void addStockTab(TabHost tabHost) {
        setTab(tabHost, mDashboardActivity.getResources().getString(
                R.string.tab_tag_stock),
                R.id.tab_stock_layout,
                mDashboardActivity.getResources().getDrawable(
                        R.drawable.tab_stock));
        for (int i = 0; i < tabHost.getTabWidget().getChildCount();
                i++) {
            tabHost.getTabWidget().getChildAt(i).setFocusable(false);
            tabHost.getTabWidget().getChildAt(i).setBackgroundResource(
                    R.drawable.tab_below_line);
        }
    }
}

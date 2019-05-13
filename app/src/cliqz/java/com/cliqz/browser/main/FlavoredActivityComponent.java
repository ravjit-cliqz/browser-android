package com.cliqz.browser.main;

import com.cliqz.browser.annotations.PerActivity;
import com.cliqz.browser.main.search.Freshtab;
import com.cliqz.browser.main.search.Incognito;
import com.cliqz.browser.view.UserSurveyMessage;

import dagger.Subcomponent;

/**
 * @author Ravjit Uppal
 */
@PerActivity
@Subcomponent(modules = {MainActivityModule.class})
public interface FlavoredActivityComponent extends MainActivityComponent {

    void inject(Freshtab freshtab);

    void inject(QuickAccessBar quickAccessBar);

    void inject(UserSurveyMessage userSurveyMessage);
}

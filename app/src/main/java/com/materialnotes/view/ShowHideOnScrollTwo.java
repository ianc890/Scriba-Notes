package com.materialnotes.view;

/**
 * Created by Ian C on 15/06/2016.
 */

import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.materialnotes.R;

import com.shamanland.fab.FloatingActionButton;
import com.shamanland.fab.ScrollDetector;

/**
 * Hides and shows a FloatingActionButton and ActionBar when you scroll up or down
 **
 */
public class ShowHideOnScrollTwo extends ScrollDetector implements Animation.AnimationListener  {

    private final FloatingActionButton fab;
    private final FloatingActionButton fabTwo;
    private final ActionBar actionBar;

    /**
     * Constructor.
     *
     * @param fab  FloatingActionButton
     * @param fabTwo  FloatingActionButton
     * @param actionBar  ActionBar
     */
    public ShowHideOnScrollTwo(FloatingActionButton fab, FloatingActionButton fabTwo, ActionBar actionBar) {
        super(fab.getContext());
        this.fab = fab;
        this.fabTwo = fabTwo;
        this.actionBar = actionBar;
    }

    /** {@inheritDoc} */
    @Override
    public void onScrollDown() {
        if (!areViewsVisible()) {
            fab.setVisibility(View.VISIBLE);
            fabTwo.setVisibility(View.VISIBLE);
            actionBar.show();
            animateFAB(R.anim.floating_action_button_show);
            animateFABTwo(R.anim.floating_action_button_show);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onScrollUp() {
        if (areViewsVisible()) {
            fab.setVisibility(View.GONE);
            fabTwo.setVisibility(View.GONE);
            actionBar.hide();
            animateFAB(R.anim.floating_action_button_hide);
            animateFABTwo(R.anim.floating_action_button_hide);
        }
    }

    /** @return {@code true} if the FAB and the ActionBar are visible; {@code false} if not. */
    private boolean areViewsVisible() {
        return fab.getVisibility() == View.VISIBLE && fabTwo.getVisibility() == View.VISIBLE && actionBar.isShowing();
    }

    /**
     * Animated FAB
     *
     * @param anim the animation.
     */
    private void animateFAB(int anim) {
        Animation a = AnimationUtils.loadAnimation(fab.getContext(), anim);
        a.setAnimationListener(this);
        fab.startAnimation(a);
        setIgnore(true);
    }

    /**
     * Animated FABTwo
     *
     * @param anim the animation.
     */
    private void animateFABTwo(int anim) {
        Animation b = AnimationUtils.loadAnimation(fabTwo.getContext(), anim);
        b.setAnimationListener(this);
        fabTwo.startAnimation(b);
        setIgnore(true);
    }

    /** {@inheritDoc} */
    @Override
    public void onAnimationStart(Animation animation) {
        // Nada
    }

    /** {@inheritDoc} */
    @Override
    public void onAnimationEnd(Animation animation) {
        setIgnore(false);
    }

    /** {@inheritDoc} */
    @Override
    public void onAnimationRepeat(Animation animation) {
        // Nada
    }
}

package com.materialnotes.view;

import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.materialnotes.R;
import com.shamanland.fab.FloatingActionButton;
import com.shamanland.fab.ScrollDetector;

/**
 * Created by Ian C on 04/07/2016.
 */
public class ShowHideOnScrollThree extends ScrollDetector implements Animation.AnimationListener {

    private final FloatingActionButton fab;
    private final FloatingActionButton fabTwo;
    private final FloatingActionButton fabThree;
    private final ActionBar actionBar;

    /**
     * Constructor.
     *
     * @param fab  FloatingActionButton
     * @param fabTwo  FloatingActionButton
     * @param fabThree  FloatingActionButton
     * @param actionBar  ActionBar
     */
    public ShowHideOnScrollThree(FloatingActionButton fab, FloatingActionButton fabTwo, FloatingActionButton fabThree, ActionBar actionBar) {
        super(fab.getContext());
        this.fab = fab;
        this.fabTwo = fabTwo;
        this.fabThree = fabThree;
        this.actionBar = actionBar;
    }

    /** {@inheritDoc} */
    @Override
    public void onScrollDown() {
        if (!areViewsVisible()) {
            fab.setVisibility(View.VISIBLE);
            fabTwo.setVisibility(View.VISIBLE);
            fabThree.setVisibility(View.VISIBLE);
            actionBar.show();
            animateFAB(R.anim.floating_action_button_show);
            animateFABTwo(R.anim.floating_action_button_show);
            animateFABThree(R.anim.floating_action_button_show);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onScrollUp() {
        if (areViewsVisible()) {
            fab.setVisibility(View.GONE);
            fabTwo.setVisibility(View.GONE);
            fabThree.setVisibility(View.GONE);
            actionBar.hide();
            animateFAB(R.anim.floating_action_button_hide);
            animateFABTwo(R.anim.floating_action_button_hide);
            animateFABThree(R.anim.floating_action_button_hide);
        }
    }

    /** @return {@code true} if the FAB and the ActionBar are visible; {@code false} if not. */
    private boolean areViewsVisible() {
        return fab.getVisibility() == View.VISIBLE && fabTwo.getVisibility() == View.VISIBLE && fabThree.getVisibility() == View.VISIBLE && actionBar.isShowing();
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

    /**
     * Animated FABThree
     *
     * @param anim the animation.
     */
    private void animateFABThree(int anim) {
        Animation b = AnimationUtils.loadAnimation(fabThree.getContext(), anim);
        b.setAnimationListener(this);
        fabThree.startAnimation(b);
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

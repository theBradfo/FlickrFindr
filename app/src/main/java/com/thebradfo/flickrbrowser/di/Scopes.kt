package com.thebradfo.flickrbrowser.di

import android.app.Activity
import androidx.fragment.app.Fragment
import javax.inject.Scope

/**
 * A [Scope] for Dagger to retain references bound to an [Activity].
 */
@Scope
@Retention
annotation class ActivityScope

/**
 * A [Scope] for Dagger to retain references bound to a [Fragment].
 */
@Scope
@Retention
annotation class FragmentScope

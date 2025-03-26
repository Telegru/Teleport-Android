package ru.tusco.messenger.ui.components

import android.content.Context
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.R
import org.telegram.ui.Components.RLottieDrawable
import org.telegram.ui.Components.RLottieImageView

class InputExpandAnimatedIconView(context: Context?) : RLottieImageView(context) {
    private var currentState: State? = null
    private var animatingState: TransitState? = null

    private val stateMap: Map<TransitState, RLottieDrawable> =
        object : HashMap<TransitState, RLottieDrawable>() {

            override fun get(key: TransitState): RLottieDrawable {
                val obj = super.get(key)
                if (obj == null) {
                    val res = key.resource
                    return RLottieDrawable(res, res.toString(), AndroidUtilities.dp(48f), AndroidUtilities.dp(48f))
                }
                return obj
            }
        }

    fun setState(state: State, animate: Boolean) {
        if (animate && state == currentState) {
            return
        }
        val fromState = currentState
        currentState = state
        if (!animate || fromState == null || getState(fromState, currentState) == null) {
            val drawable = stateMap[getAnyState(currentState)]
            drawable!!.stop()

            drawable.setProgress(0f, false)
            setAnimation(drawable)
        } else {
            val transitState = getState(fromState, currentState)
            if (transitState == animatingState) {
                return
            }

            animatingState = transitState
            val drawable = stateMap[transitState]
            drawable?.stop()
            drawable?.setProgress(0f, false)
            drawable?.setAutoRepeat(0)
            drawable?.setOnAnimationEndListener { animatingState = null }
            setAnimation(drawable)
            AndroidUtilities.runOnUIThread { drawable?.start() }
        }
    }

    private fun getAnyState(from: State?): TransitState? {
        for (transitState in TransitState.entries) {
            if (transitState.firstState == from) {
                return transitState
            }
        }
        return null
    }

    private fun getState(
        from: State,
        to: State?
    ): TransitState? {
        for (transitState in TransitState.entries) {
            if (transitState.firstState == from && transitState.secondState == to) {
                return transitState
            }
        }
        return null
    }

    private enum class TransitState(
        val firstState: State,
        val secondState: State,
        val resource: Int
    ) {
        EXPAND_TO_COLLAPSE(
            State.EXPANDED,
            State.COLLAPSED,
            R.raw.arrow_down_to_up
        ),
        COLLAPSE_TO_EXPAND(
            State.COLLAPSED,
            State.EXPANDED,
            R.raw.arrow_up_to_down
        )
    }

    enum class State {
        EXPANDED,
        COLLAPSED
    }
}
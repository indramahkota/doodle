package com.nectar.doodle.focus.impl

import com.nectar.doodle.core.View
import com.nectar.doodle.focus.FocusManager
import com.nectar.doodle.focus.FocusTraversalPolicy
import com.nectar.doodle.focus.FocusTraversalPolicy.TraversalType
import com.nectar.doodle.focus.FocusTraversalPolicy.TraversalType.Backward
import com.nectar.doodle.focus.FocusTraversalPolicy.TraversalType.Downward
import com.nectar.doodle.focus.FocusTraversalPolicy.TraversalType.Forward
import com.nectar.doodle.focus.FocusTraversalPolicy.TraversalType.Upward
import com.nectar.doodle.utils.ObservableList
import com.nectar.doodle.utils.PropertyObservers
import com.nectar.doodle.utils.PropertyObserversImpl
import kotlin.math.max

/**
 * Created by Nicholas Eddy on 3/2/18.
 */

class FocusManagerImpl(defaultFocusTraversalPolicy: FocusTraversalPolicy? = null): FocusManager {

    private val defaultFocusTraversalPolicy = defaultFocusTraversalPolicy ?: FocusTraversalPolicyImpl(this)

    private val ancestors = mutableListOf<View>()

    override var focusOwner: View? = null
        private set

    override var focusCycleRoot: View? = null
        private set

    override val focusChanged: PropertyObservers<FocusManager, View?> by lazy { PropertyObserversImpl<FocusManager, View?>(this) }

    override fun focusable(view: View) = view.run { focusable && enabled && visible }

    override fun requestFocus(view: View) = requestFocusInternal(view)

    override fun clearFocus() = requestFocusInternal(null)

    override fun moveFocusForward (          ) = moveFocus(null, Forward )
    override fun moveFocusForward (from: View) = moveFocus(from, Forward )
    override fun moveFocusBackward(from: View) = moveFocus(from, Backward)
    override fun moveFocusUpward  (from: View) = moveFocus(from, Upward  )
    override fun moveFocusDownward(from: View) = moveFocus(from, Downward)

    private fun requestFocusInternal(view: View?) {
        if (focusOwner != view && (view == null || focusable(view))) {
            val oldFocusOwner = focusOwner

            if (oldFocusOwner != null) {
                if (oldFocusOwner.shouldYieldFocus()) {
                    clearAncestorListeners()

                    oldFocusOwner.focusLost(view)

                    stopMonitorProperties(oldFocusOwner)
                } else {
                    return
                }
            }

            focusOwner = view

            focusOwner?.let { focusOwner ->
                focusOwner.focusGained(oldFocusOwner)

                focusCycleRoot = focusOwner.focusCycleRoot_

                startMonitorProperties(focusOwner)

                // Listen for removal of this item or any of its ancestors

                registerAncestorListeners()
            }

            (focusChanged as PropertyObserversImpl<FocusManager, View?>)(oldFocusOwner, view)
        }
    }

    private fun moveFocus(view: View?, traversalType: TraversalType) {
        var focusView      = view ?: focusOwner
        val focusCycleRoot = focusView?.focusCycleRoot_
        val policy         = focusCycleRoot?.focusTraversalPolicy_ ?: defaultFocusTraversalPolicy

        when (traversalType) {
            Forward  -> focusCycleRoot?.let { focusView = policy.next    (focusCycleRoot, focusView) }
            Backward -> focusCycleRoot?.let { focusView = policy.previous(focusCycleRoot, focusView) }
            Upward   -> focusCycleRoot?.let { requestFocus(focusCycleRoot) }
            Downward -> focusView?.let {
                if (it.isFocusCycleRoot_ == true) {
                    focusView = policy.default(it)

                    requestFocusInternal(view)
                }
            }
        }

        requestFocusInternal(focusView)
    }

    private fun registerAncestorListeners() {
        var parent = focusOwner?.parent

        while (parent != null) {
            parent.children_.changed += childrenChanged

            ancestors += parent

            parent = parent.parent
        }
    }

    private fun clearAncestorListeners() {
        for (ancestor in ancestors) {
            ancestor.children_.changed -= childrenChanged
        }

        ancestors.clear()
    }

    private fun startMonitorProperties(view: View) {
        view.enabledChanged      += focusabilityChanged
        view.visibilityChanged   += focusabilityChanged
        view.focusabilityChanged += focusabilityChanged
    }

    private fun stopMonitorProperties(view: View) {
        view.enabledChanged      -= focusabilityChanged
        view.focusabilityChanged -= focusabilityChanged
        view.visibilityChanged   -= focusabilityChanged
    }

    private val childrenChanged: (ObservableList<View, View>, Map<Int, View>, Map<Int, View>, Map<Int, Pair<Int, View>>) -> Unit = { list,removed,added,_ ->
        added.values.forEach {
            if (it === focusOwner || it in ancestors) {
                val owner = focusOwner

                if (owner != null) moveFocusForward(owner) else moveFocusForward()

                return@forEach
            }
        }

        removed.forEach { (index, value) ->
            if (value === focusOwner || value in ancestors) {
                val owner = focusOwner

                if (owner != null) {
                    val sibling = max(0, index - 1)

                    if (sibling < list.source.children_.size) {
                        moveFocusForward(list.source.children_[sibling])
                    }
                }

                return@forEach
            }
        }
    }

    private val focusabilityChanged: (View, Boolean, Boolean) -> Unit = { view,_,_ -> moveFocusForward(view) }
}
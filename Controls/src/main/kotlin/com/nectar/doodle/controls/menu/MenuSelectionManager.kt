package com.nectar.doodle.controls.menu

import com.nectar.doodle.utils.Path
import com.nectar.doodle.utils.PropertyObservers
import com.nectar.doodle.utils.PropertyObserversImpl
import kotlin.math.min

/**
 * Created by Nicholas Eddy on 4/30/18.
 */
interface MenuSelectionManager {
    var selectedPath    : Path<MenuItem>?
    val selectionChanged: PropertyObservers<MenuSelectionManager, Path<MenuItem>?>
}

class MenuSelectionManagerImpl: MenuSelectionManager {
    override val selectionChanged: PropertyObservers<MenuSelectionManager, Path<MenuItem>?> by lazy { PropertyObserversImpl<MenuSelectionManager, Path<MenuItem>?>(this) }

    override var selectedPath: Path<MenuItem>? = null
        set(new) {
            if (new == selectedPath) {
                return
            }

            val old       = field
            var diffStart = 0

            if (new != null) {
                field?.let {
                    val minLength = min(it.depth + 1, new.depth + 1)

                    while (diffStart < minLength && it[diffStart] === new[diffStart]) {
                        diffStart++
                    }
                }
            }

            field?.let {
                for (i in it.depth downTo diffStart) {
                    it[i].menuSelected = false
                }
            }

            if (new != null) {
                for (i in diffStart..new.depth) {
                    new[i].menuSelected = true
                }
            }

            field = new

            (selectionChanged as PropertyObserversImpl<MenuSelectionManager, Path<MenuItem>?>)(old, new)
        }
}

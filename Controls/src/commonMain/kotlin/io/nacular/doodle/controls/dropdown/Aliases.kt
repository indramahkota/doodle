package io.nacular.doodle.controls.dropdown

import io.nacular.doodle.controls.EditOperation
import io.nacular.doodle.core.View

@Deprecated("Use SelectBox instead", ReplaceWith("SelectBox<T, M>"))
public typealias Dropdown<T, M> = SelectBox<T, M>

@Deprecated("Use MutableSelectBox instead", ReplaceWith("MutableSelectBox<T, M>"))
public typealias MutableDropdown<T, M> = MutableSelectBox<T, M>

@Deprecated("Use SelectBoxBehavior instead", ReplaceWith("SelectBoxBehavior<T, M>"))
public typealias DropdownBehavior<T, M> = SelectBoxBehavior<T, M>

@Deprecated("Use SelectBoxEditor instead", ReplaceWith("SelectBoxEditor<T>"))
public typealias DropdownEditor<T> = SelectBoxEditor<T>

@Deprecated("Use MutableSelectBoxBehavior instead", ReplaceWith("MutableSelectBoxBehavior<T, M>"))
public typealias MutableDropdownBehavior<T, M> = MutableSelectBoxBehavior<T, M>

@Deprecated("Use new selectBoxEditor method", replaceWith = ReplaceWith("selectBoxEditor(block)"))
public inline fun <T> dropdownEditor(crossinline block: (dropdown: MutableSelectBox<T, *>, value: T, current: View) -> EditOperation<T>): SelectBoxEditor<T> = selectBoxEditor(block)
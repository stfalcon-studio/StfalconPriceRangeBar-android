package com.stfalcon.pricerangebar.extension

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

inline fun <T> convertable(
    initialValue: T,
    crossinline f: (v: T) -> T,
    crossinline onChange: (property: KProperty<*>, oldValue: T, newValue: T) -> Unit
):
        ReadWriteProperty<Any?, T> = object : ConvertableVar<T>(initialValue) {
    override fun onChange(property: KProperty<*>, oldValue: T, newValue: T) =
        onChange(property, oldValue, newValue)

    override fun convertValue(value: T) = f.invoke(value)
}

open class ConvertableVar<T>(initialValue: T) : ReadWriteProperty<Any?, T> {
    private var value = initialValue

    protected open fun convertValue(value: T): T = value
    protected open fun onChange(property: KProperty<*>, oldValue: T, newValue: T) = Unit
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val oldValue = this.value
        this.value = convertValue(value)
        onChange(property, oldValue, value)
    }
}

internal fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}
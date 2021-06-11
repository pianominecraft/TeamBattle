package com.pianominecraft.teambattle

import java.util.*
import kotlin.collections.HashMap

object Cooltime {

    private val cooltime = HashMap<String, HashMap<UUID, Int>>()

    fun getMap(key: String) : HashMap<UUID, Int>? {
        if (cooltime.containsKey(key)) {
            return cooltime[key]!!
        }
        return null
    }
    fun getOf(key: String, uuid: UUID) : Int {
        if (cooltime.containsKey(key)) {
            if (!cooltime[key]!!.containsKey(uuid)) cooltime[key]!![uuid] = 0
            return cooltime[key]!![uuid]!!
        }
        return -1
    }
    fun addKey(key: String) {
        cooltime[key] = HashMap()
    }
    fun setOf(key: String, uuid: UUID, value: Int) {
        if (cooltime.containsKey(key)) {
            cooltime[key]!![uuid] = value
        }
    }
    fun add(key: String, uuid: UUID, plus: Int){
        if (cooltime.containsKey(key)) {
            if (!cooltime[key]!!.containsKey(uuid)) cooltime[key]!![uuid] = 0
            cooltime[key]!![uuid] = cooltime[key]!![uuid]!! + plus
        }
    }
    fun subtract(key: String, uuid: UUID, minus: Int){
        if (cooltime.containsKey(key)) {
            if (!cooltime[key]!!.containsKey(uuid)) cooltime[key]!![uuid] = 0
            cooltime[key]!![uuid] = cooltime[key]!![uuid]!! - minus
        }
    }
    fun remove(key: String) {
        cooltime.keys.forEach { k ->
            if (k == key) {
                cooltime.remove(k)
                return
            }
        }
    }
    fun keys() : List<String> {
        return cooltime.keys.toList()
    }

}
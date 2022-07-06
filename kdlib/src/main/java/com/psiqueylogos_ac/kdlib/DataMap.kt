package com.psiqueylogos_ac.kdlib


import org.json.JSONObject
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubtypeOf

/**
 * @author Aldo Dell Uomini
 * This interface is intended to give a some class serialization using map and
 * JSON.
 * A class can implement DataMap interface and will have two read/write properties:
 * "map" and "json". With these properties may save and read class implemented properties.
 * If want to exclude a property of serialization process, mark this property with @Excluding
 * annotation tag.
 * @sample
 * class Person : DataMap {
 *      var name =""
 *      var age =""
 * }
 *
 * class Job : DataMap {
 *      @Excluding
 *      var id = 0
 *      var person = Person()
 *      var position =""
 * }
 */
interface DataMap {
    annotation class Excluding

    var map: MutableMap<String, Any>
        get() {
            val r = mutableMapOf<String, Any>()
            this::class.declaredMemberProperties.forEach {
                if (!it.hasAnnotation<Excluding>()) {
                    if (it is KMutableProperty1) {
                        val p = it as KMutableProperty1<DataMap, Any>
                        var obj = p.get(this)
                        if (obj is DataMap) {
                            obj = obj.map
                        }
                        r[p.name] = obj
                    }
                }
            }
            return r
        }
        set(value) {
            value.keys.forEach { key ->
                this::class.declaredMemberProperties.find { it.name == key }?.let { prop ->
                    if (!prop.hasAnnotation<Excluding>()) {
                        if (prop is KMutableProperty1) {
                            val prop0 = prop as KMutableProperty1<DataMap, Any>
                            if (prop0.returnType.isSubtypeOf(DataMap::class.createType())) {
                                val obj = prop0.get(this) as DataMap
                                obj.map = value[prop0.name] as MutableMap<String, Any>
                                prop0.set(this, obj)
                            } else {
                                prop0.set(this, value[prop0.name]!!)
                            }
                        }
                    }
                }

            }
        }

    var json: JSONObject
        get() = JSONObject(this.map as Map<*, *>?)
        set(value) {
            value.keys().forEach { key ->
                this::class.declaredMemberProperties.find { it.name == key }?.let { prop ->
                    if (!prop.hasAnnotation<Excluding>()) {
                        if (prop is KMutableProperty1) {
                            val prop0 = prop as KMutableProperty1<DataMap, Any>
                            if (prop0.returnType.isSubtypeOf(DataMap::class.createType())) {
                                val obj = prop0.get(this) as DataMap
                                obj.json = value[prop0.name] as JSONObject
                                prop0.set(this, obj)
                            } else {
                                prop0.set(this, value[prop0.name]!!)
                            }
                        }

                    }
                }
            }
        }


}


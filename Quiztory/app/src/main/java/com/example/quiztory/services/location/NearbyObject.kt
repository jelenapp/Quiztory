package com.example.quiztory.services.location

import android.os.Parcel
import android.os.Parcelable

data class NearbyObject(
    val id: Long = 0,  // Podrazumevani ID
    val title: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
): Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NearbyObject> {
        override fun createFromParcel(parcel: Parcel): NearbyObject {
            return NearbyObject(parcel)
        }

        override fun newArray(size: Int): Array<NearbyObject?> {
            return arrayOfNulls(size)
        }
    }

    // Implementacija Parcelable
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble()
    )
}
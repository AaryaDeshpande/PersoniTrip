package com.cs407.personitrip

import android.os.Parcel
import android.os.Parcelable

// Data class representing an attraction category with name and image resource ID.
data class AttractionCategory(
    val name: String,
    val imageResourceId: Int, // Default image resource ID
    val photoReference: String? = null // New field for dynamic photo reference
) : Parcelable {
    // Parcelable implementation for inter-activity transfer.
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(imageResourceId)
        parcel.writeString(photoReference)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<AttractionCategory> {
        override fun createFromParcel(parcel: Parcel): AttractionCategory {
            return AttractionCategory(parcel)
        }

        override fun newArray(size: Int): Array<AttractionCategory?> {
            return arrayOfNulls(size)
        }
    }
}

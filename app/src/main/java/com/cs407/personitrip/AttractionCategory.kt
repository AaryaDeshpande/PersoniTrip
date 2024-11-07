package com.cs407.personitrip

import android.os.Parcel
import android.os.Parcelable

// Data class representing an attraction category with name and image resource ID.
// Implements Parcelable so that it can be passed between Activities.
data class AttractionCategory(
    val name: String,
    val imageResourceId: Int // Drawable resource ID for the attraction image
) : Parcelable {
    // Constructor to create an AttractionCategory from a Parcel.
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt()
    )

    // Writes the data to a Parcel for inter-activity transfer.
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(imageResourceId)
    }

    // Describes special contents; usually 0.
    override fun describeContents(): Int {
        return 0
    }

    // Companion object to create instances of AttractionCategory from a Parcel.
    companion object CREATOR : Parcelable.Creator<AttractionCategory> {
        override fun createFromParcel(parcel: Parcel): AttractionCategory {
            return AttractionCategory(parcel)
        }

        override fun newArray(size: Int): Array<AttractionCategory?> {
            return arrayOfNulls(size)
        }
    }
}

package me.fungames.jfortniteparse.ue4.registry.objects

import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.reader.FArchive

class FAssetIdentifier(Ar: FArchive) {
    var packageName: FName? = null
        private set
    var primaryAssetType: FName? = null
        private set
    var objectName: FName? = null
        private set
    var valueName: FName? = null
        private set

    init {
        val fieldBits = Ar.readInt8().toInt()

        if (fieldBits and (1 shl 0) != 0)
            packageName = Ar.readFName()
        if (fieldBits and (1 shl 1) != 0)
            primaryAssetType = Ar.readFName()
        if (fieldBits and (1 shl 2) != 0)
            objectName = Ar.readFName()
        if (fieldBits and (1 shl 3) != 0)
            valueName = Ar.readFName()
    }
}
package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.enums.EBulkDataFlags
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FByteBulkDataHeader : UClass {
    var bulkDataFlags: Int
    var elementCount: Long
    var sizeOnDisk: Long
    var offsetInFile: Long

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        bulkDataFlags = Ar.readInt32()
        if (EBulkDataFlags.BULKDATA_Size64Bit.check(bulkDataFlags)) {
            elementCount = Ar.readInt64()
            sizeOnDisk = Ar.readInt64()
        } else {
            elementCount = Ar.readInt32().toLong()
            sizeOnDisk = Ar.readInt32().toLong()
        }
        offsetInFile = Ar.readInt64() + Ar.bulkDataStartOffset
        if (EBulkDataFlags.BULKDATA_BadDataVersion.check(bulkDataFlags)) {
            Ar.skip(2) //val dummyValue = Ar.readUInt16()
        }
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(bulkDataFlags)
        if (EBulkDataFlags.BULKDATA_Size64Bit.check(bulkDataFlags)) {
            Ar.writeInt64(elementCount)
            Ar.writeInt64(sizeOnDisk)
        } else {
            Ar.writeInt32(elementCount.toInt())
            Ar.writeInt32(sizeOnDisk.toInt())
        }
        Ar.writeInt64(offsetInFile)
        super.completeWrite(Ar)
    }

    constructor(bulkDataFlags: Int, elementCount: Long, sizeOnDisk: Long, offsetInFile: Long) {
        this.bulkDataFlags = bulkDataFlags
        this.elementCount = elementCount
        this.sizeOnDisk = sizeOnDisk
        this.offsetInFile = offsetInFile
    }
}
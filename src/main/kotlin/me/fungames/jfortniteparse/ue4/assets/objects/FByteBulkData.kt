package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.enums.EBulkDataFlags.*
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.PayloadType
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

class FByteBulkData : UClass {
    var header: FByteBulkDataHeader
    var data: ByteArray

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        header = FByteBulkDataHeader(Ar)
        val bulkDataFlags = header.bulkDataFlags
        data = ByteArray(header.elementCount.toInt())
        when {
            header.elementCount == 0L -> {
                // Nothing to do here
            }
            BULKDATA_Unused.check(bulkDataFlags) -> {
                logger.warn("Bulk with no data")
            }
            BULKDATA_ForceInlinePayload.check(bulkDataFlags) -> {
                logger.debug("bulk data in .uexp file (Force Inline Payload) (flags=$bulkDataFlags, pos=${header.offsetInFile}, size=${header.sizeOnDisk})")
                Ar.read(data)
            }
            BULKDATA_PayloadInSeperateFile.check(bulkDataFlags) -> {
                logger.debug("bulk data in .ubulk file (Payload In Seperate File) (flags=$bulkDataFlags, pos=${header.offsetInFile}, size=${header.sizeOnDisk})")
                val ubulkAr = Ar.getPayload(if (BULKDATA_OptionalPayload.check(bulkDataFlags)) PayloadType.UPTNL else PayloadType.UBULK)
                /*val ubulkAr = if (BULKDATA_OptionalPayload.check(bulkDataFlags)) {
                    try {
                        Ar.getPayload(PayloadType.UPTNL)
                    } catch (ignored: Exception) {
                        Ar.getPayload(PayloadType.UBULK)
                    }
                } else {
                    Ar.getPayload(PayloadType.UBULK)
                }*/
                ubulkAr.seek(header.offsetInFile.toInt())
                ubulkAr.read(data)
            }
            BULKDATA_PayloadAtEndOfFile.check(bulkDataFlags) -> {
                //stored in same file, but at different position
                //save archive position
                val savePos = Ar.pos()
                if (header.offsetInFile.toInt() + header.elementCount <= Ar.size()) {
                    Ar.seek(header.offsetInFile.toInt())
                    Ar.read(data)
                } else {
                    throw ParserException("Failed to read PayloadAtEndOfFile, ${header.offsetInFile} is out of range", Ar)
                }
                Ar.seek(savePos)
            }
        }
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        val bulkDataFlags = header.bulkDataFlags
        when {
            BULKDATA_Unused.check(bulkDataFlags) -> {
                header.serialize(Ar)
            }
            BULKDATA_ForceInlinePayload.check(bulkDataFlags) -> {
                header.offsetInFile = (Ar.relativePos() + 28).toLong()
                header.elementCount = data.size.toLong()
                header.sizeOnDisk = data.size.toLong()
                header.serialize(Ar)
                Ar.write(data)
            }
            BULKDATA_PayloadInSeperateFile.check(bulkDataFlags) -> {
                val ubulkAr = Ar.getPayload(PayloadType.UBULK)
                header.offsetInFile = ubulkAr.relativePos().toLong()
                header.elementCount = data.size.toLong()
                header.sizeOnDisk = data.size.toLong()
                header.serialize(Ar)
                ubulkAr.write(data)
            }
            BULKDATA_OptionalPayload.check(bulkDataFlags) -> {
                val ubulkAr = Ar.getPayload(PayloadType.UPTNL)
                header.offsetInFile = ubulkAr.relativePos().toLong()
                header.elementCount = data.size.toLong()
                header.sizeOnDisk = data.size.toLong()
                header.serialize(Ar)
                ubulkAr.write(data)
            }
            else -> throw ParserException("Unsupported BulkData type $bulkDataFlags")
        }
        super.completeWrite(Ar)
    }

    constructor(header: FByteBulkDataHeader, data: ByteArray) {
        this.header = header
        this.data = data
    }
}
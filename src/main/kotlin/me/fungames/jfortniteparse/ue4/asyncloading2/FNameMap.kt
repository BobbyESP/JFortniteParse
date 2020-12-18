package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.ue4.io.EIoChunkType
import me.fungames.jfortniteparse.ue4.io.EIoDispatcherPriority.IoDispatcherPriority_High
import me.fungames.jfortniteparse.ue4.io.FIoChunkId
import me.fungames.jfortniteparse.ue4.io.FIoDispatcher
import me.fungames.jfortniteparse.ue4.io.FIoReadOptions
import me.fungames.jfortniteparse.ue4.objects.uobject.FMinimalName
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FNameEntryId
import me.fungames.jfortniteparse.ue4.objects.uobject.loadNameBatch
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.util.await
import me.fungames.jfortniteparse.util.get
import java.util.concurrent.CompletableFuture

class FNameMap {
    internal var nameEntries = emptyList<String>()
    private var nameMapType = FMappedName.EType.Global

    fun loadGlobal(ioDispatcher: FIoDispatcher) {
        check(nameEntries.isEmpty())

        val namesId = FIoChunkId(0u, 0u, EIoChunkType.LoaderGlobalNames)
        val hashesId = FIoChunkId(0u, 0u, EIoChunkType.LoaderGlobalNameHashes)

        val batch = ioDispatcher.newBatch()
        val nameRequest = batch.read(namesId, FIoReadOptions(), IoDispatcherPriority_High.value)
        val hashRequest = batch.read(hashesId, FIoReadOptions(), IoDispatcherPriority_High.value)
        val batchCompletedEvent = CompletableFuture<Void>()
        batch.issueAndTriggerEvent(batchCompletedEvent)

        /*reserveNameBatch(
            ioDispatcher.getSizeForChunk(namesId),
            ioDispatcher.getSizeForChunk(hashesId))*/

        batchCompletedEvent.await()

        val nameBuffer = nameRequest.result.getOrThrow()
        val hashBuffer = hashRequest.result.getOrThrow()

        load(nameBuffer, hashBuffer, FMappedName.EType.Global)
    }

    fun size() = nameEntries.size

    fun load(nameBuffer: ByteArray, hashBuffer: ByteArray, nameMapType: FMappedName.EType) {
        nameEntries = loadNameBatch(FByteArchive(nameBuffer), FByteArchive(hashBuffer))
        this.nameMapType = nameMapType
    }

    fun load(nameBuffer: FArchive, hashBuffer: FArchive, nameMapType: FMappedName.EType) {
        nameEntries = loadNameBatch(nameBuffer, hashBuffer)
        this.nameMapType = nameMapType
    }

    fun getName(mappedName: FMappedName): FName {
        check(mappedName.getType() == nameMapType)
        check(mappedName.getIndex() < nameEntries.size.toUInt())
        val nameEntry = nameEntries[mappedName.getIndex()]
        return FName.createFromDisplayId(nameEntry, mappedName.number.toInt())
    }

    fun tryGetName(mappedName: FMappedName): FName? {
        check(mappedName.getType() == nameMapType)
        val index = mappedName.getIndex()
        if (index < nameEntries.size.toUInt()) {
            val nameEntry = nameEntries[mappedName.getIndex()]
            return FName.createFromDisplayId(nameEntry, mappedName.number.toInt())
        }
        return null
    }

    fun getMinimalName(mappedName: FMappedName): FMinimalName {
        check(mappedName.getType() == nameMapType)
        check(mappedName.getIndex() < nameEntries.size.toUInt())
        return FMinimalName(FNameEntryId(mappedName.getIndex()), mappedName.number.toInt(), nameEntries)
    }
}
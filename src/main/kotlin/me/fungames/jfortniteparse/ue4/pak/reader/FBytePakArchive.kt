package me.fungames.jfortniteparse.ue4.pak.reader

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.min

@ExperimentalUnsignedTypes
class FBytePakArchive(val data : ByteBuffer, fileName: String, val offsetInPakFile : Long, val pakFileSize : Long) : FPakArchive(fileName) {
    init {
        data.order(ByteOrder.LITTLE_ENDIAN)
    }

    constructor(data : ByteArray, fileName: String, offsetInPakFile : Long, pakFileSize : Long) : this(ByteBuffer.wrap(data), fileName, offsetInPakFile, pakFileSize)

    override var littleEndian : Boolean
        get() = data.order() == ByteOrder.LITTLE_ENDIAN
        set(value) {
            if (value)
                data.order(ByteOrder.LITTLE_ENDIAN)
            else
                data.order(ByteOrder.BIG_ENDIAN)
        }

    private var pos : Int
        get() = data.position()
        set(value) { data.position(value) }
    private val size = data.limit()

    override fun clone(): FBytePakArchive {
        val clone = FBytePakArchive(data, fileName, offsetInPakFile, pakFileSize)
        clone.pos = pos
        clone.pakInfo = pakInfo
        return clone
    }

    override fun seek(pos: Long) {
        //rangeCheck(pos.toInt())
        this.pos = pos.toInt()
    }

    override fun skip(n: Long): Long {
        //rangeCheck(pos + n.toInt())
        this.pos += n.toInt()
        return n
    }

    override fun pakSize() = pakFileSize

    override fun pakPos() = offsetInPakFile

    override fun readBuffer(size: Int): ByteBuffer {
        return data.duplicate().apply {
            order(data.order())
            limit(position() + size)
            pos += size
        }
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val count = min(size - pos, len)
        if (count == 0) return -1
        data.get(b, off, len)
        return count
    }

    override fun readDouble() = data.double
    override fun readFloat32() = data.float
    override fun readInt8() = data.get()
    override fun readInt16() = data.short
    override fun readInt32() = data.int
    override fun readInt64() = data.long
}
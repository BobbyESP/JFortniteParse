package me.fungames.jfortniteparse.exceptions

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

open class ParserException(message: String?, cause: Throwable? = null) : Exception(message, cause) {
    @ExperimentalUnsignedTypes
    constructor(message: String, Ar: FArchive, cause: Throwable? = null) : this(
        """
            $message
            ${Ar.printError()}
        """.trimIndent(), cause
    )

    @ExperimentalUnsignedTypes
    constructor(message: String, Ar: FArchiveWriter, cause: Throwable? = null) : this(
        """
            $message
            ${Ar.printError()}
        """.trimIndent(), cause
    )
}

class InvalidAesKeyException(message: String?, cause: Throwable? = null) : ParserException(message, cause)
class UnknownCompressionMethodException(message: String?, cause: Throwable? = null) : RuntimeException(message, cause)
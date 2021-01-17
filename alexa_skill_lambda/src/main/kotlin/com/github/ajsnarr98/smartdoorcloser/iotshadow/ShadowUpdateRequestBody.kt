package com.github.ajsnarr98.smartdoorcloser.iotshadow

import java.time.Instant

/**
 * Body sent in a shadow update.
 *
 * Use ONLY the static builder method to create manually.
 */
data class ShadowUpdateRequestBody(
    var state: StatePair? = null,
) {
    companion object {
        /**
         * Creates an update body to issue a close command, by setting the
         * lastCloseCommand field to the current epoch time in milliseconds.
         */
        fun createCloseCommand(): ShadowUpdateRequestBody {
            return ShadowUpdateRequestBody(
                state = StatePair(
                    desired = State(
                        lastCloseCmd = Instant.now().toEpochMilli().toString()
                    )
                )
            )
        }
    }

    data class StatePair(
        var desired: State? = null,
    )

    data class State(
        var lastCloseCmd: String? = null,
    )
}
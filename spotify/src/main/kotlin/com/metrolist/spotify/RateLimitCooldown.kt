package com.metrolist.spotify

/**
 * Remembers a 429's Retry-After so callers stop hitting the API until it expires.
 * Spotify rate-limits per app (all REST endpoints share the window), and every
 * request made while limited can stretch it — so one shared cooldown, not one per
 * endpoint.
 */
internal class RateLimitCooldown(private val now: () -> Long = System::currentTimeMillis) {
    @Volatile private var untilMs = 0L

    /** Seconds left (rounded up), or 0 when requests may go out. */
    fun remainingSec(): Long {
        val left = untilMs - now()
        return if (left <= 0) 0 else (left + 999) / 1000
    }

    /** Start (or extend) the cooldown; never shortens one already running. */
    fun start(retryAfterSec: Long) {
        if (retryAfterSec <= 0) return
        untilMs = maxOf(untilMs, now() + retryAfterSec * 1000)
    }
}

package com.metrolist.spotify

import org.junit.Assert.assertEquals
import org.junit.Test

/** [RateLimitCooldown] decides whether a REST call may go out after a 429. */
class RateLimitCooldownTest {

    private var clock = 1_000_000L
    private val cooldown = RateLimitCooldown { clock }

    @Test
    fun `no cooldown before any 429`() {
        assertEquals(0, cooldown.remainingSec())
    }

    @Test
    fun `cooldown lasts retry-after and then expires`() {
        cooldown.start(30)
        assertEquals(30, cooldown.remainingSec())
        clock += 29_500
        assertEquals(1, cooldown.remainingSec())
        clock += 500
        assertEquals(0, cooldown.remainingSec())
    }

    @Test
    fun `a shorter retry-after does not shorten a running cooldown`() {
        cooldown.start(40)
        clock += 10_000
        cooldown.start(5)
        assertEquals(30, cooldown.remainingSec())
    }

    @Test
    fun `a longer retry-after extends the cooldown`() {
        cooldown.start(10)
        cooldown.start(55)
        assertEquals(55, cooldown.remainingSec())
    }

    @Test
    fun `zero or negative retry-after is ignored`() {
        cooldown.start(0)
        cooldown.start(-3)
        assertEquals(0, cooldown.remainingSec())
    }
}

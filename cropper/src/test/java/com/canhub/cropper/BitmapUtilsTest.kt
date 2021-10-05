package com.canhub.cropper

import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

private val RECT_POINTS: FloatArray = floatArrayOf(1f, 2f, 3f,4f, 5f, 6f, 7f, 8f)
private val LOW_RECT_POINTS: FloatArray = floatArrayOf(1f)

class BitmapUtilsTest {

    @Before
    fun setup() {
        mockkObject(BitmapUtils)
    }

    @After
    fun teardown() {
        unmockkObject(BitmapUtils)
    }

    @Test
    fun `WHEN float array of rectangle points is provided, THEN result should be left value of the bounding rectangle`() {
        // WHEN
        val rectLeft = BitmapUtils.getRectLeft(RECT_POINTS)

        // THEN
        assertEquals(1f, rectLeft)
    }

    @Test
    fun `WHEN float array of rectangle points is provided, THEN result should be right value of the bounding rectangle`() {
        // WHEN
        val rectRight = BitmapUtils.getRectRight(RECT_POINTS)

        // THEN
        assertEquals(7f, rectRight)
    }

    @Test
    fun `WHEN float array of rectangle points is provided, THEN result should be bottom value of the bounding rectangle`() {
        // WHEN
        val rectBottom = BitmapUtils.getRectBottom(RECT_POINTS)

        // THEN
        assertEquals(8f, rectBottom)
    }

    @Test
    fun `WHEN float array of rectangle points is provided, THEN result should be top value of the bounding rectangle`() {
        // WHEN
        val rectTop = BitmapUtils.getRectTop(RECT_POINTS)

        // THEN
        assertEquals(2f, rectTop)
    }

    @Test
    fun `WHEN float array of rectangle points is provided, THEN result should be rect height`() {
        // WHEN
        val rectHeight = BitmapUtils.getRectHeight(RECT_POINTS)

        // THEN
        assertEquals(6f, rectHeight)
    }

    @Test
    fun `WHEN float array of rectangle points is provided, THEN result should be rect width`() {
        // WHEN
        val rectWidth = BitmapUtils.getRectWidth(RECT_POINTS)

        // THEN
        assertEquals(6f, rectWidth)
    }

    @Test
    fun `WHEN float array of rectangle points is provided, THEN result should be rect centerX`() {
        // WHEN
        val rectCenterX = BitmapUtils.getRectCenterX(RECT_POINTS)

        // THEN
        assertEquals(4f, rectCenterX)
    }

    @Test
    fun `WHEN float array of rectangle points is provided, THEN result should be rect centerY`() {
        // WHEN
        val rectCenterY = BitmapUtils.getRectCenterY(RECT_POINTS)

        // THEN
        assertEquals(5f, rectCenterY)
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun `WHEN low rectangle points is provided to getRectCenterY, THEN resultArrayOutOfIndexException`() {
        BitmapUtils.getRectCenterY(LOW_RECT_POINTS)
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun `WHEN low rectangle points is provided to getRectCenterX, THEN resultArrayOutOfIndexException`() {
        BitmapUtils.getRectCenterX(LOW_RECT_POINTS)
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun `WHEN low rectangle points is provided to getRectLeft, THEN resultArrayOutOfIndexException`() {
        BitmapUtils.getRectLeft(LOW_RECT_POINTS)
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun `WHEN low rectangle points is provided to getRectRight, THEN resultArrayOutOfIndexException`() {
        BitmapUtils.getRectRight(LOW_RECT_POINTS)
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun `WHEN low rectangle points is provided getRectTop, THEN resultArrayOutOfIndexException`() {
        BitmapUtils.getRectTop(LOW_RECT_POINTS)
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun `WHEN low rectangle points is provided getRectBottom, THEN resultArrayOutOfIndexException`() {
        BitmapUtils.getRectBottom(LOW_RECT_POINTS)
    }
}

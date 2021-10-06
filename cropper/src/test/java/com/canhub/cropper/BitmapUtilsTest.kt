package com.canhub.cropper

import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

private const val RECT_WIDTH = 10f
private const val RECT_HEIGHT = 13f
private const val RECT_LEFT = 0f
private const val RECT_RIGHT = 10f
private const val RECT_BOTTOM = 15f
private const val RECT_TOP = 2f
private const val RECT_CENTER_X = 5f
private const val RECT_CENTER_Y = 8.5f
private val RECTANGLE_IMAGE_POINTS: FloatArray = floatArrayOf(RECT_LEFT, RECT_TOP, RECT_WIDTH, RECT_TOP, RECT_WIDTH, RECT_BOTTOM, RECT_LEFT, RECT_BOTTOM)
private val LOW_RECT_POINTS: FloatArray = floatArrayOf(RECT_LEFT)

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
        val rectLeft = BitmapUtils.getRectLeft(RECTANGLE_IMAGE_POINTS)

        // THEN
        assertEquals(RECT_LEFT, rectLeft)
    }

    @Test
    fun `WHEN float array of rectangle points is provided, THEN result should be right value of the bounding rectangle`() {
        // WHEN
        val rectRight = BitmapUtils.getRectRight(RECTANGLE_IMAGE_POINTS)

        // THEN
        assertEquals(RECT_RIGHT, rectRight)
    }

    @Test
    fun `WHEN float array of rectangle points is provided, THEN result should be bottom value of the bounding rectangle`() {
        // WHEN
        val rectBottom = BitmapUtils.getRectBottom(RECTANGLE_IMAGE_POINTS)

        // THEN
        assertEquals(RECT_BOTTOM, rectBottom)
    }

    @Test
    fun `WHEN float array of rectangle points is provided, THEN result should be top value of the bounding rectangle`() {
        // WHEN
        val rectTop = BitmapUtils.getRectTop(RECTANGLE_IMAGE_POINTS)

        // THEN
        assertEquals(RECT_TOP, rectTop)
    }

    @Test
    fun `WHEN float array of rectangle points is provided, THEN result should be rect height`() {
        // WHEN
        val rectHeight = BitmapUtils.getRectHeight(RECTANGLE_IMAGE_POINTS)

        // THEN
        assertEquals(RECT_HEIGHT, rectHeight)
    }

    @Test
    fun `WHEN float array of rectangle points is provided, THEN result should be rect width`() {
        // WHEN
        val rectWidth = BitmapUtils.getRectWidth(RECTANGLE_IMAGE_POINTS)

        // THEN
        assertEquals(RECT_WIDTH, rectWidth)
    }

    @Test
    fun `WHEN float array of rectangle points is provided, THEN result should be rect centerX`() {
        // WHEN
        val rectCenterX = BitmapUtils.getRectCenterX(RECTANGLE_IMAGE_POINTS)

        // THEN
        assertEquals(RECT_CENTER_X, rectCenterX)
    }

    @Test
    fun `WHEN float array of rectangle points is provided, THEN result should be rect centerY`() {
        // WHEN
        val rectCenterY = BitmapUtils.getRectCenterY(RECTANGLE_IMAGE_POINTS)

        // THEN
        assertEquals(RECT_CENTER_Y, rectCenterY)
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

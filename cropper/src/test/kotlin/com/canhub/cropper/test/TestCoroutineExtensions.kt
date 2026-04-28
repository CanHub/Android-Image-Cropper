package com.canhub.cropper.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit rule for setting up coroutine test dispatchers.
 *
 * Usage:
 * ```kotlin
 * @get:Rule
 * val coroutineRule = CoroutineTestRule()
 * ```
 *
 * This automatically sets up and tears down the Main dispatcher for testing.
 * Uses UnconfinedTestDispatcher which executes coroutines eagerly and immediately,
 * without requiring manual advancement of virtual time.
 *
 * This is critical for testing code that uses Dispatchers.Default or Dispatchers.IO,
 * since kotlinx.coroutines.test only provides setMain() to replace the Main dispatcher.
 * UnconfinedTestDispatcher ensures coroutines complete synchronously regardless of
 * which dispatcher they use, preventing test timeouts.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineTestRule(
  val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {

  override fun starting(description: Description) {
    super.starting(description)
    Dispatchers.setMain(testDispatcher)
  }

  override fun finished(description: Description) {
    super.finished(description)
    Dispatchers.resetMain()
  }
}

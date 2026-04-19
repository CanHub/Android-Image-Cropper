---
name: run-tests
description: Run tests for Android Image Cropper
argument-hint: "[test-scope: all|unit|snapshot|specific-class]"
level: 1
triggers:
  - "run tests"
  - "test"
  - "run all tests"
---

# Run Tests Skill

Run the test suite for Android Image Cropper with various configurations.

## Usage

Run this skill to execute tests with different scopes:
- All tests (default)
- Unit tests only
- Specific test class
- With code coverage

## Commands

### Run All Tests
```bash
./gradlew testDebug
```

### Run With Full Validation
```bash
./gradlew licensee ktlint testDebug build --stacktrace
```

### Run Specific Test Class
```bash
./gradlew testDebug --tests "com.canhub.cropper.BitmapUtilsTest"
```

### Run Paparazzi Snapshot Tests
```bash
./gradlew verifyPaparazziDebug
```

### Record New Paparazzi Snapshots
```bash
./gradlew recordPaparazziDebug
```

## Test Output

Tests output to: `cropper/build/reports/tests/testDebugUnitTest/index.html`

## What to Check After Running

1. ✅ All tests pass
2. ✅ No flaky tests
3. ✅ Coverage is adequate
4. ✅ No test errors or warnings
5. ✅ Snapshot tests match (if applicable)

## Common Issues

### Issue: Paparazzi snapshots differ
**Solution**: Review changes, if correct run: `./gradlew recordPaparazziDebug`

### Issue: Robolectric tests fail
**Solution**: Check Android version compatibility, update Robolectric if needed

### Issue: Tests timeout
**Solution**: Large images may cause timeouts, check bitmap sizes in tests

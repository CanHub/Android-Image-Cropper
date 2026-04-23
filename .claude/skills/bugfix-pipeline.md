---
name: bugfix-pipeline
description: Complete pipeline for fixing bugs (investigate → test → fix → verify)
argument-hint: "<bug-description or issue-number>"
level: 4
triggers:
  - "fix bug"
  - "debug"
  - "investigate bug"
pipeline: [bug-investigator, test-writer, code-reviewer]
---

# Bug Fix Pipeline Skill

Complete end-to-end workflow for investigating and fixing bugs in Android Image Cropper.

## Pipeline Stages

### Stage 1: Investigation (bug-investigator agent)
**Goal**: Reproduce and diagnose the bug with evidence

**CRITICAL**: Reproduce FIRST - Never fix without reproduction

1. Extract reproduction steps from issue
2. Reproduce locally (fail test or sample app)
3. Gather evidence in parallel (grep/read)
4. Form hypothesis about root cause
5. Verify hypothesis with code evidence
6. Identify affected components with file:line references
7. Propose solution with ≥2 alternatives

**Output**: Bug investigation report with:
- Verified reproduction steps
- Root cause with file:line evidence
- Evidence quality ranking (controlled > observed > speculated)
- Proposed solutions with trade-offs
- Impact assessment

**Success Gate**: Bug reproduced, root cause identified with evidence

---

### Stage 2: Regression Test (test-writer agent)
**Goal**: Write failing test that reproduces the bug

**CRITICAL**: Test must fail BEFORE the fix

1. Write test that reproduces exact bug scenario
2. Reference GitHub issue number in test
3. Use real code, not mocks
4. Test on affected Android versions
5. Verify test fails with current code
6. Document expected vs actual behavior

**Output**: Failing regression test

**Test naming convention**:
```kotlin
@Test
fun `regression #656 - crop window no longer jumps on multi-touch release`() {
  // Test that reproduces issue #656
  // MUST fail before fix, pass after fix
}
```

**Success Gate**: Test fails consistently, reproducing the bug

---

### Stage 3: Fix Implementation (manual or executor)
**Goal**: Fix the bug following investigation findings

1. Read bug investigation report
2. Implement proposed solution (Option 1 unless justified)
3. Keep changes minimal and focused
4. Avoid "while I'm here" changes
5. Update CHANGELOG.md with fix
6. Reference issue number in commit

**CHANGELOG format**:
```markdown
- Fix: [Description of bug fixed] [\#656](url) ([github-handle](url))
```

**Success Gate**: Regression test now passes

---

### Stage 4: Verification (test-writer + code-reviewer)
**Goal**: Ensure fix is correct and complete

**Test Writer verifies**:
1. Regression test passes
2. All existing tests still pass
3. No new edge cases introduced
4. Test coverage maintained or improved

**Code Reviewer verifies**:
1. Fix addresses root cause (not just symptoms)
2. No new issues introduced
3. Change is minimal and focused
4. Security implications considered
5. Backward compatibility maintained

**Output**: Verification report with approval or issues

**Success Gate**: All tests pass, review approved

---

### Stage 5: Final Verification
**Goal**: Full system check before merge

**Commands to run**:
```bash
# Format code
./gradlew ktlintFormat

# Run full test suite
./gradlew licensee ktlint testDebug build --stacktrace

# Test on sample app
./gradlew :sample:installDebug
```

**Manual verification**:
1. Reproduce original bug in sample app
2. Verify bug is fixed in sample app
3. Test on affected Android versions
4. Check for regressions in related features

**Checklist**:
- [ ] Regression test added (references issue #XXX)
- [ ] Regression test failed before fix ✅
- [ ] Regression test passes after fix ✅
- [ ] All existing tests still pass
- [ ] ktlint passes
- [ ] CHANGELOG.md updated
- [ ] Fix verified in sample app
- [ ] No new issues introduced
- [ ] Code review approved

---

## Usage Examples

### Example 1: Simple Bug Fix
```
User: "Use bugfix-pipeline to fix issue #656 - crop window jumps on multi-touch"

Stage 1 (Bug Investigator):
- Reads issue #656
- Reproduces: two fingers, release first, window jumps
- Evidence: CropOverlayView.kt:200 assumes pointer index 0
- Root cause: Pointer index shifts after release
- Solution: Track pointer ID instead of index

Stage 2 (Test Writer):
- Writes failing test: `regression #656 - crop window no longer jumps`
- Uses real MotionEvent, real CropOverlayView
- Test fails ✅ (reproduces bug)

Stage 3 (Fix Implementation):
- Changes line 200: event.getX(pointerIndex) instead of event.getX(0)
- Tracks primary pointer ID
- Updates CHANGELOG.md

Stage 4 (Verification):
- Regression test passes ✅
- All other tests pass ✅
- Code review: minimal change, addresses root cause ✅

Stage 5 (Final):
- Full build passes
- Sample app tested: bug fixed
- Ready to merge
```

### Example 2: Complex Bug with Multiple Causes
```
User: "Fix issue #680 - SecurityException on certain URIs"

Stage 1 (Bug Investigator):
- Reproduces with specific URI patterns
- Evidence in GetFilePathFromUri.kt:45
- Multiple issues found:
  1. No URI validation
  2. File provider misconfiguration
  3. Missing permission checks
- Proposes 3-part fix

Stage 2 (Test Writer):
- Test 1: Invalid URI handling
- Test 2: File provider URIs
- Test 3: Permission scenarios
- All tests fail initially ✅

Stage 3 (Fix Implementation):
- Adds URI validation utility
- Updates file provider config
- Adds permission checks
- Security-focused changes

Stage 4 (Verification):
- Security review (critical for security fixes)
- All regression tests pass
- No bypass possibilities identified

Stage 5 (Final):
- Tested on Android 21, 28, 34
- Sample app tested with various URI sources
- Security implications documented
```

### Example 3: Performance Bug
```
User: "Fix issue #XXX - OutOfMemoryError with large images"

Stage 1 (Bug Investigator):
- Reproduces with 5000x5000 image
- Evidence: BitmapUtils not sampling properly
- Root cause: calculateSampleSize returns 1 for large images
- Impact: Critical (crash)

Stage 2 (Test Writer):
- Test with large bitmap
- Verifies OOM occurs
- Test fails ✅

Stage 3 (Fix Implementation):
- Fixes calculateSampleSize logic
- Adds max size limits
- Improves sampling algorithm

Stage 4 (Verification):
- Performance test: large images now work
- Memory profiling: allocation reduced
- Edge case tests: various sizes

Stage 5 (Final):
- Tested with 10000x10000 images
- Memory usage reasonable
- No regressions
```

---

## Investigation Protocol

**ALWAYS follow this sequence** (from bug-investigator):

### 1. Reproduce FIRST
- Never investigate without reproduction
- If can't reproduce, ask for more details
- Reproduction > speculation

### 2. Gather Evidence in Parallel
```bash
# Run these simultaneously for speed
Grep("method_name", output_mode: "files_with_matches")
Grep("error_message", output_mode: "content")
Read("suspected_file.kt")
```

### 3. Form Hypothesis
- Predict issue BEFORE reading code
- Activates deliberate search mode
- Example: "I predict the issue is in touch handling, likely pointer index confusion"

### 4. Verify with Evidence
- Test hypothesis against actual code
- Rank evidence: Controlled > Observed > Speculation
- Strong evidence = controlled reproduction

### 5. Circuit Breaker
- Stop after 3 failed reproduction attempts
- Escalate or ask for help
- Don't waste time on unreproducible issues

---

## Regression Test Requirements

### Must Have
1. **Issue reference**: Test name includes `#123`
2. **Exact reproduction**: Test recreates exact bug scenario
3. **Real code**: Use real components, not mocks
4. **Fails initially**: Verify test fails before fix
5. **Passes after**: Verify test passes after fix
6. **Multiple SDK versions**: Test on 21, 28, 34 if relevant

### Example Structure
```kotlin
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21, 28, 34])
class RegressionTests {
  
  @Test
  fun `regression #656 - crop window jumps on multi-touch pointer release`() {
    // Arrange - Real components
    val context = ApplicationProvider.getApplicationContext<Context>()
    val overlay = CropOverlayView(context)
    val bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888)
    overlay.setImageBitmap(bitmap)
    
    // Act - Reproduce exact bug scenario from issue #656
    val initialRect = overlay.cropWindowRect
    
    // Two pointers down
    val downEvent1 = createMotionEvent(MotionEvent.ACTION_DOWN, x1, y1, 0)
    val downEvent2 = createMotionEvent(MotionEvent.ACTION_POINTER_DOWN, x2, y2, 1)
    overlay.onTouchEvent(downEvent1)
    overlay.onTouchEvent(downEvent2)
    
    // First pointer released (this caused jump before fix)
    val upEvent1 = createMotionEvent(MotionEvent.ACTION_POINTER_UP, x1, y1, 0)
    overlay.onTouchEvent(upEvent1)
    
    val finalRect = overlay.cropWindowRect
    
    // Assert - Window should not have jumped
    assertThat(finalRect).isEqualTo(initialRect)
  }
}
```

---

## Fix Implementation Guidelines

### Do's
✅ **Fix the root cause**, not symptoms
✅ **Keep changes minimal** - only what's needed
✅ **Add defensive checks** where appropriate
✅ **Update CHANGELOG.md** immediately
✅ **Reference issue number** in commit
✅ **Test on multiple Android versions**

### Don'ts
❌ **Don't fix unrelated issues** ("while I'm here")
❌ **Don't refactor** during bug fix (separate PR)
❌ **Don't add features** while fixing bugs
❌ **Don't skip the regression test**
❌ **Don't make breaking changes** (unless critical security fix)

### Commit Message Format
```
Fix: [Description of bug fixed] (fixes #656)

- Root cause: [Brief explanation]
- Solution: [What was changed]
- Impact: [What this affects]

Co-Authored-By: Claude Sonnet 4.5 <noreply@anthropic.com>
```

---

## Best Practices

### 1. Evidence Over Speculation
Always cite `file:line` references. Rank evidence quality.

### 2. Minimal Surgical Fixes
Change only what's necessary. Resist scope creep.

### 3. Test Before and After
Regression test must fail before fix, pass after fix. Verify manually.

### 4. Consider Side Effects
Check what else might be affected by the change.

### 5. Security Implications
For security bugs, extra scrutiny. No bypass possibilities.

---

## Anti-Patterns to Avoid

### ❌ Fixing Without Reproduction
```
BAD: "I think I know what's wrong, let me fix it"
GOOD: "Let me reproduce it first to confirm"
```

### ❌ Symptom Fixes
```
BAD: Adding null check without understanding why it's null
GOOD: Fix why it's null in the first place
```

### ❌ Scope Creep
```
BAD: "While fixing this, let me also refactor..."
GOOD: "Focus on the bug fix, refactor separately"
```

### ❌ Skipping Regression Test
```
BAD: "The fix is simple, no need for a test"
GOOD: "Every bug gets a regression test"
```

---

*Every bug fixed without a regression test is a bug waiting to return.*

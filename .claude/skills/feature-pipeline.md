---
name: feature-pipeline
description: Complete pipeline for adding new features (design → implement → test → review)
argument-hint: "<feature-description>"
level: 4
triggers:
  - "add feature"
  - "new feature"
  - "implement feature"
pipeline: [api-designer, test-writer, code-reviewer]
---

# Feature Pipeline Skill

Complete end-to-end workflow for adding new features to Android Image Cropper.

## Pipeline Stages

### Stage 1: API Design (api-designer agent)
**Goal**: Design the public API before implementation

1. Analyze feature requirements
2. Design API following Kotlin best practices
3. Evaluate ≥2 alternatives with trade-offs
4. Document API contract with KDoc
5. Create usage examples
6. Output: API design document with rationale

**Success Gate**: Design reviewed and approved

---

### Stage 2: Implementation (manual or executor)
**Goal**: Implement the designed API

1. Read the API design document
2. Implement in `cropper/` module
3. Follow code style (2-space indent, trailing commas)
4. Update `CropImageOptions` if adding configuration
5. Add to sample app for demonstration
6. Update CHANGELOG.md under "In development"

**Success Gate**: Implementation matches design spec

---

### Stage 3: Test Writing (test-writer agent)
**Goal**: Comprehensive test coverage with minimal mocking

1. Write public API contract tests
2. Write behavior verification tests
3. Write edge case tests
4. Test on multiple SDK versions (21, 28, 34)
5. Ensure ≥80% coverage on public APIs
6. Use real code, not mocks

**Success Gate**: All tests pass, coverage ≥80%

---

### Stage 4: Code Review (code-reviewer agent)
**Goal**: Quality gate before merge

1. Review code quality and style
2. Check API compatibility (no breaking changes)
3. Verify test coverage
4. Check security (URI validation, input validation)
5. Verify documentation (CHANGELOG, README if needed)
6. Identify trade-offs and risks

**Success Gate**: Review APPROVE or issues resolved

---

### Stage 5: Final Verification
**Goal**: Everything passes before merge

**Commands to run**:
```bash
# Format code
./gradlew ktlintFormat

# Run all checks
./gradlew licensee ktlint testDebug build --stacktrace

# Test sample app
./gradlew :sample:installDebug
```

**Checklist**:
- [ ] ktlint passes
- [ ] All tests pass
- [ ] Sample app demonstrates feature
- [ ] CHANGELOG.md updated
- [ ] README.md updated (if user-facing)
- [ ] No breaking API changes (or properly deprecated)
- [ ] Code review approved

---

## Usage Examples

### Example 1: Simple Feature
```
User: "Use the feature-pipeline skill to add a resetZoom() method"

Stage 1 (API Designer):
- Designs API: fun resetZoom()
- Provides alternatives: resetZoom() vs resetScale()
- Documents behavior

Stage 2 (Implementation):
- Adds method to CropImageView
- Updates CHANGELOG.md

Stage 3 (Test Writer):
- Writes API contract test
- Writes behavior test
- Tests on SDK 21, 28, 34

Stage 4 (Code Reviewer):
- Reviews implementation
- Checks test coverage
- Approves

Stage 5 (Verification):
- Runs all checks
- Tests sample app
```

### Example 2: Complex Feature with Options
```
User: "Use feature-pipeline to add custom crop shapes beyond rectangle and oval"

Stage 1 (API Designer):
- Designs CropImageOptions extension
- Evaluates Shape enum vs sealed class
- Documents API with examples
- Considers backward compatibility

Stage 2 (Implementation):
- Extends CropImageOptions
- Updates CropOverlayView rendering
- Adds to sample app

Stage 3 (Test Writer):
- API contract tests for new options
- Rendering tests (snapshot tests)
- Edge cases (custom paths)

Stage 4 (Code Reviewer):
- Reviews complexity
- Checks performance impact
- Verifies tests

Stage 5 (Verification):
- Full build passes
- Sample app works
- README.md updated
```

---

## Pipeline Coordination

### Agent Handoffs

**API Designer → Implementation**:
- Handoff: API design document
- Implementation reads design spec
- Implementation must match designed API contract

**Implementation → Test Writer**:
- Handoff: Implemented code
- Test writer verifies all public methods
- Test writer creates contract tests

**Test Writer → Code Reviewer**:
- Handoff: Code + tests
- Reviewer checks coverage
- Reviewer verifies test quality

### State Management

Track progress through pipeline:
1. **Design Complete**: API design document exists
2. **Implementation Complete**: Code written, CHANGELOG updated
3. **Tests Complete**: Tests written, coverage ≥80%
4. **Review Complete**: Code review approved
5. **Verified**: All checks pass, ready to merge

### Failure Handling

**If API design rejected**:
- Revise design
- Re-evaluate alternatives
- Get stakeholder input

**If implementation doesn't match design**:
- Fix implementation to match spec
- Or revise design if requirements changed

**If tests fail**:
- Fix implementation bugs
- Or fix incorrect tests

**If code review finds issues**:
- Address CRITICAL and HIGH severity
- Consider MEDIUM suggestions
- Document trade-off decisions

---

## Integration with Workflows

### Adding to Common Workflows (README)

```markdown
#### Adding a New Feature
1. 🎯 Use **feature-pipeline** skill (orchestrates entire workflow)
   OR manually:
   1. 🎨 Use **API Designer** to design the API
   2. Implement the feature
   3. 🧪 Use **Test Writer** to add comprehensive tests
   4. ✅ Use **Run Tests** to verify
   5. 🎨 Use **Check Code Style** to format
   6. 📝 Use **Update CHANGELOG** to document
   7. 🔍 Use **Code Reviewer** before submitting PR
```

---

## Best Practices

### 1. Design First
Never skip API design stage. Bad APIs are hard to change in a published library.

### 2. Test During Development
Don't wait until the end to write tests. Test-driven development catches issues early.

### 3. Incremental Reviews
Review code as you go, not all at once at the end.

### 4. Documentation Discipline
Update CHANGELOG.md immediately when implementation is done. Update README.md for user-facing features.

### 5. Sample App Demonstration
Always add feature to sample app. It's the best documentation and manual test.

### 6. Backward Compatibility
This is a published library serving thousands of apps. Breaking changes require deprecation cycle.

---

## Anti-Patterns to Avoid

### ❌ Implementing Before Designing
```
BAD: "I'll just code it and see how it looks"
GOOD: "Let me design the API first, evaluate alternatives"
```

### ❌ Skipping Tests
```
BAD: "I'll add tests later"
GOOD: "Tests are part of the feature, not optional"
```

### ❌ Self-Approval
```
BAD: Same agent/person designs, implements, and approves
GOOD: Separate design, implementation, and review roles
```

### ❌ Weak Test Coverage
```
BAD: One happy-path test with mocks
GOOD: Contract tests, behavior tests, edge cases with real code
```

---

*Complete pipeline ensures quality and consistency across all features.*

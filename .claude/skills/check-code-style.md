---
name: check-code-style
description: Check and fix code style with ktlint
argument-hint: "[action: format|check]"
level: 1
triggers:
  - "format"
  - "ktlint"
  - "check style"
  - "fix style"
---

# Check Code Style Skill

Check and automatically fix code style issues using ktlint.

## Usage

Run this skill before committing code to ensure style compliance.

## Commands

### Auto-Fix Code Style
```bash
./gradlew ktlintFormat
```

### Check Code Style (No Auto-Fix)
```bash
./gradlew ktlint
```

### Check Specific Module
```bash
./gradlew :cropper:ktlint
```

## Style Rules

This project uses:
- **ktlint 1.3.1**
- **IntelliJ IDEA code style**
- **2-space indentation**
- **Trailing commas enabled**
- **Experimental ktlint features enabled**

See `.editorconfig` for specific rule configuration.

## Common Style Issues

### Indentation
**Rule**: 2 spaces, no tabs
**Fix**: Run `./gradlew ktlintFormat`

### Trailing Commas
**Rule**: Allowed and encouraged
**Example**:
```kotlin
// Good
listOf(
  "one",
  "two",
  "three", // Trailing comma OK
)

// Also OK
listOf("one", "two", "three")
```

### Import Order
**Fix**: Auto-fixed by ktlintFormat

### Line Length
**Rule**: Disabled (no max length)
**Note**: Still keep lines reasonable for readability

## What to Check

After running ktlintFormat:
1. ✅ Review the changes made
2. ✅ Ensure formatting is correct
3. ✅ Run `./gradlew ktlint` to verify
4. ✅ Commit the formatted code

## Integration with Build

ktlint runs automatically in CI/CD pipeline.
Always run locally before pushing.

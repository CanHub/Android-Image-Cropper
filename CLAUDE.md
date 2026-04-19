# Android Image Cropper - Claude AI Development Guide

This document provides project-specific instructions for AI-assisted development on the Android Image Cropper library.

## Project Overview

**Android Image Cropper** optimized image cropping capabilities for Android applications.

## Tech Stack

- **Language**: Kotlin 2.0.0
- **Build System**: Gradle 8.5.2 with Kotlin DSL
- **Android**: minSdk 21, compileSdk 34, targetSdk 34
- **Key Dependencies**:
  - AndroidX (AppCompat, Core, Activity, ExifInterface)
  - Kotlin Coroutines 1.8.1
  - Material Components
- **Testing**: JUnit, MockK, Robolectric, Paparazzi (snapshot testing)
- **Code Quality**: ktlint 1.3.1, Dokka (documentation)

## Module Structure

```
Android-Image-Cropper/
├── cropper/           # Main library module (published artifact)
│   └── src/
│       ├── main/      # Library source code
│       └── test/      # Unit tests
├── sample/            # Sample app demonstrating usage
│   └── src/main/      # Sample implementations
├── .github/           # CI/CD workflows
└── gradle/            # Build configuration
```

## Code Style & Conventions

### Kotlin Style
- **Code Style**: IntelliJ IDEA
- **Indentation**: 2 spaces (no tabs)
- **Continuation Indent**: 2 spaces
- **Trailing Commas**: Allowed and encouraged
- **Line Length**: No maximum (disabled)
- **Linter**: ktlint with experimental features enabled
- **Warnings**: All Kotlin warnings treated as errors

### File Organization
- Package structure: `com.canhub.cropper.*`
- Internal APIs: Classes/methods not meant for public use should be marked `internal`
- Public API: Keep minimal and well-documented
- Deprecated APIs: Mark with `@Deprecated` and provide migration path

### Code Quality Standards
1. **No unused code**: Remove unused variables, functions, and imports
2. **Type safety**: Leverage Kotlin's type system
3. **Null safety**: Use Kotlin null-safety features appropriately
4. **Coroutines**: Use for async operations (already in use for bitmap operations)
5. **Resource management**: Always close resources properly (see BitmapUtils)

## Build & Test Commands

```bash
# Build the project
./gradlew build

# Run all checks (linting, tests, etc.)
./gradlew licensee ktlint testDebug build --stacktrace

# Run unit tests
./gradlew testDebug

# Run ktlint
./gradlew ktlint

# Format code with ktlint
./gradlew ktlintFormat

# Run Paparazzi snapshot tests
./gradlew verifyPaparazziDebug

# Generate documentation
./gradlew dokkaHtml
```

## Development Workflow

### Making Changes

1. **Branch Naming**: Use developer github name `canato/` prefix (e.g., `canato/fix-rotation-bug`)
2. **Code Changes**:
   - Make changes in `cropper/` module
   - Update tests as needed
   - Update sample app if adding new features
3. **Before Committing**:
   - Run `./gradlew ktlintFormat` to auto-format
   - Run `./gradlew ktlint testDebug` to validate
   - Ensure all tests pass
4. **Commit Messages**: Follow existing style in CHANGELOG.md
   - Start with category: "API:", "Fix:", "Security:", "Technical:", etc.
   - Reference issue/PR numbers: `[#123]`

### Testing Strategy

1. **Unit Tests**: All business logic should have unit tests
   - Location: `cropper/src/test/kotlin/`
   - Use MockK for mocking
   - Use Robolectric for Android framework dependencies
2. **Snapshot Tests**: Use Paparazzi for UI component tests
3. **Sample App**: Manual testing via `sample/` module
4. **StrictMode**: Sample app has StrictMode enabled - no violations allowed

### API Changes

⚠️ **This is a published library** - API changes affect thousands of apps!

#### Breaking Changes
- **Avoid** breaking changes whenever possible
- If unavoidable:
  - Deprecate old API first
  - Provide migration guide
  - Increment major version
  - Update CHANGELOG.md with migration notes

#### Deprecation Process
1. Mark as `@Deprecated` with message and replacement
2. Keep deprecated code for at least one minor version
3. Document in CHANGELOG.md
4. Update README.md with migration guide
5. Remove only in next major version

#### Adding New APIs
1. Add to public API only if necessary
2. Document with KDoc
3. Add usage example to sample app
4. Update README.md if user-facing
5. Consider making `internal` if not needed publicly

## Release Process

**DO NOT manually trigger releases** - handled by maintainer via GitHub Actions.

1. **Snapshot Releases**: Auto-published from `main` branch to Maven Central
2. **Release Versions**: 
   - Update `VERSION_NAME` in `gradle.properties`
   - Update `CHANGELOG.md`
   - Tag release
   - GitHub Actions publishes to Maven Central

## Common Tasks

### Adding a New Feature
1. Read existing code in `cropper/src/main/kotlin/com/canhub/cropper/`
2. Understand how `CropImageView` and related classes work
3. Add feature with appropriate tests
4. Update `CropImageOptions` if adding new configuration
5. Add example to sample app
6. Update CHANGELOG.md under "In development" section
7. Update README.md if user-facing

### Fixing a Bug
1. Add a failing test that reproduces the bug
2. Fix the bug
3. Ensure test passes
4. Run full test suite
5. Update CHANGELOG.md with fix description and issue number

### Updating Dependencies
1. Dependencies managed in `gradle/libs.versions.toml`
2. Test thoroughly after updates (especially Android/Kotlin versions)
3. Check for API changes in dependencies
4. Run full test suite
5. Check sample app still works

### Adding Translations
1. Add strings to `cropper/src/main/res/values-{lang}/strings.xml`
2. Copy structure from `values/strings.xml`
3. Test in sample app with device language change
4. Update CHANGELOG.md noting new language support

## Important Files

- **README.md**: User-facing documentation, API examples
- **CHANGELOG.md**: All changes by version (REQUIRED for every change)
- **gradle.properties**: Version, Maven coordinates, publishing config
- **gradle/libs.versions.toml**: Dependency versions catalog
- **build.gradle.kts**: Root build configuration
- **cropper/build.gradle.kts**: Library module configuration
- **lint.xml**: Android Lint configuration
- **.editorconfig**: Code formatting rules
- **.github/workflows/**: CI/CD pipelines

## Key Classes to Understand

1. **CropImageView**: Main public API - the custom view users add to layouts
2. **CropImageOptions**: Configuration data class for cropping behavior
3. **CropImageActivity**: (Deprecated) Activity-based cropping
4. **CropImageContract**: Activity contract for cropping
5. **CropOverlayView**: Internal - handles crop window UI
6. **BitmapUtils**: Internal - image processing utilities
7. **BitmapCroppingWorkerJob**: Internal - async cropping
8. **BitmapLoadingWorkerJob**: Internal - async image loading

## Security Considerations

⚠️ **Security is critical** - this library handles user content and file URIs.

1. **URI Validation**: Always validate URIs (see PR #680)
2. **File Provider**: Proper file provider configuration required
3. **Permissions**: Handle camera/storage permissions appropriately
4. **Input Validation**: Validate all user inputs and image dimensions
5. **Resource Limits**: Prevent OOM with large images (sampling is used)

## Troubleshooting

### Build Issues
- Clean build: `./gradlew clean build`
- Check Java version: Requires JDK 11+ (toolchain configured)
- Check Android SDK: Requires SDK 34

### Test Failures
- Paparazzi failures: May need to record new snapshots
- Robolectric failures: Check Android version compatibility

### Lint Issues
- Run `./gradlew ktlintFormat` to auto-fix
- Check `.editorconfig` for disabled rules
- See `lint.xml` for Android Lint configuration

## AI Assistant Guidelines

When working on this project:

1. **Always read before editing**: Use Read tool on files before making changes
2. **Respect code style**: Follow ktlint rules (2-space indent, trailing commas, etc.)
3. **Update CHANGELOG.md**: Every change must be documented
4. **Test your changes**: Add/update tests, run test suite
5. **Check public API impact**: Breaking changes require deprecation cycle
6. **Update documentation**: README.md for user-facing changes
7. **Security first**: Validate inputs, handle errors, prevent security issues
8. **Performance matters**: Large images are common - optimize for memory
9. **Backward compatibility**: Support minSdk 21 (Android 5.0)
10. **Sample app**: Update if adding features users should see

### When Uncertain

- **Architecture questions**: Check existing patterns in `CropImageView`
- **API design**: Look at existing public APIs in the class
- **Testing approach**: Check existing tests in `cropper/src/test/`
- **Dependencies**: Check `gradle/libs.versions.toml` first
- **Android compatibility**: Remember minSdk is 21

## Helpful Resources

- **Sample App**: Best reference for library usage
- **CHANGELOG.md**: History of changes and API evolution
- **GitHub Issues**: Known bugs and feature requests
- **README.md**: User documentation and examples

---

*This document is maintained for AI-assisted development. Keep it updated as the project evolves.*

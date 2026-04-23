---
name: build-project
description: Build the Android Image Cropper project with various configurations
argument-hint: "[build-type: full|library|sample|clean]"
level: 1
triggers:
  - "build"
  - "build project"
  - "compile"
---

# Build Project Skill

Build the Android Image Cropper project with different configurations and targets.

## Quick Commands

### Full Build with All Checks
```bash
./gradlew clean build --stacktrace
```

### Build with All Quality Checks
```bash
./gradlew licensee ktlint testDebug build --stacktrace
```

### Build Library Only
```bash
./gradlew :cropper:build
```

### Build Sample App
```bash
./gradlew :sample:assembleDebug
```

### Install Sample App on Device
```bash
./gradlew :sample:installDebug
```

## Build Configurations

### Clean Build
**When**: Before releases, after major changes, when build is acting weird

```bash
./gradlew clean
./gradlew build --stacktrace
```

### Debug Build
**When**: During development

```bash
./gradlew assembleDebug
```

### Release Build
**When**: Testing release configuration

```bash
./gradlew assembleRelease
```

## Quality Checks

### All Quality Checks
```bash
./gradlew licensee ktlint testDebug build --stacktrace
```

### Individual Checks

**License Check**:
```bash
./gradlew licensee
```

**Code Style**:
```bash
./gradlew ktlint
```

**Tests**:
```bash
./gradlew testDebug
```

**Lint**:
```bash
./gradlew lint
```

## Documentation Generation

### Generate API Docs with Dokka
```bash
./gradlew dokkaHtml
```

**Output**: `cropper/build/dokka/html/index.html`

### Generate All Documentation
```bash
./gradlew dokka
```

## Build Artifacts

### Library AAR
**Location**: `cropper/build/outputs/aar/`

```bash
./gradlew :cropper:assembleRelease
ls -lh cropper/build/outputs/aar/
```

### Sample APK
**Location**: `sample/build/outputs/apk/debug/`

```bash
./gradlew :sample:assembleDebug
ls -lh sample/build/outputs/apk/debug/
```

## Publishing (Local)

### Publish to Maven Local
**When**: Testing library changes in another project locally

```bash
./gradlew publishToMavenLocal
```

**Then in consuming project**:
```groovy
repositories {
  mavenLocal()
}

dependencies {
  implementation("com.vanniktech:android-image-cropper:4.8.0-SNAPSHOT")
}
```

## Gradle Tasks Reference

### View All Tasks
```bash
./gradlew tasks --all
```

### View Specific Module Tasks
```bash
./gradlew :cropper:tasks
./gradlew :sample:tasks
```

### Dependency Tasks
```bash
# View dependencies
./gradlew :cropper:dependencies

# Check for dependency updates
./gradlew dependencyUpdates
```

## Build Performance

### Build with Performance Metrics
```bash
./gradlew build --profile
```

**Report**: `build/reports/profile/`

### Build Cache Info
```bash
./gradlew build --build-cache
```

### Parallel Build
```bash
./gradlew build --parallel
```

## Troubleshooting Builds

### Issue: Build fails with "Out of Memory"
**Solution**:
```bash
# Increase heap size (already set in gradle.properties)
# If still failing, try:
export GRADLE_OPTS="-Xmx4096m"
./gradlew build
```

### Issue: Build fails with "Permission denied"
**Solution**:
```bash
chmod +x gradlew
./gradlew build
```

### Issue: Gradle daemon issues
**Solution**:
```bash
./gradlew --stop
./gradlew build
```

### Issue: Dependency resolution fails
**Solution**:
```bash
./gradlew build --refresh-dependencies
```

### Issue: Build cache corruption
**Solution**:
```bash
rm -rf ~/.gradle/caches/
./gradlew build
```

### Issue: Android SDK not found
**Solution**:
1. Install Android SDK
2. Set `ANDROID_HOME` environment variable
3. Create `local.properties`:
```properties
sdk.dir=/path/to/android/sdk
```

## Build Verification

### Pre-Commit Checks
```bash
./gradlew ktlintFormat testDebug
```

### Pre-PR Checks
```bash
./gradlew clean licensee ktlint testDebug build --stacktrace
```

### Pre-Release Checks
```bash
./gradlew clean
./gradlew licensee ktlint testDebug verifyPaparazziDebug build --stacktrace
./gradlew :sample:installDebug
# Manual testing of sample app
```

## Continuous Integration

### CI Build Command
This is what runs in GitHub Actions:
```bash
./gradlew licensee ktlint testDebug build --stacktrace
```

### Local CI Simulation
```bash
# Clean environment
./gradlew clean

# Run CI checks
./gradlew licensee ktlint testDebug build --stacktrace

# Check exit code
echo $?  # Should be 0
```

## Build Output Locations

```
Android-Image-Cropper/
├── cropper/build/
│   ├── outputs/aar/              # Library artifacts
│   ├── reports/tests/            # Test reports
│   ├── reports/lint/             # Lint reports
│   └── dokka/                    # API documentation
├── sample/build/
│   └── outputs/apk/              # Sample APKs
└── build/
    ├── reports/                  # Root project reports
    └── dokka/                    # Combined documentation
```

## Quick Reference

| Task | Command |
|------|---------|
| **Full build** | `./gradlew build` |
| **Clean build** | `./gradlew clean build` |
| **With checks** | `./gradlew licensee ktlint testDebug build --stacktrace` |
| **Format code** | `./gradlew ktlintFormat` |
| **Run tests** | `./gradlew testDebug` |
| **Install sample** | `./gradlew :sample:installDebug` |
| **Generate docs** | `./gradlew dokkaHtml` |
| **Publish local** | `./gradlew publishToMavenLocal` |

## Best Practices

1. **Always clean before release builds**: `./gradlew clean`
2. **Use --stacktrace for debugging**: Shows full error details
3. **Run ktlintFormat before committing**: Auto-fix style issues
4. **Test both modules**: Library and sample app
5. **Check build reports**: Review test and lint reports
6. **Use build cache**: Speeds up incremental builds
7. **Keep Gradle updated**: Follow project's Gradle version

---

*A clean build is a happy build.*

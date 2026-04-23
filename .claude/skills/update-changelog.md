---
name: update-changelog
description: Update CHANGELOG.md with changes
argument-hint: "<change-description>"
level: 1
triggers:
  - "update changelog"
  - "add to changelog"
  - "changelog"
---

# Update CHANGELOG Skill

Update CHANGELOG.md following the project's conventions.

## Usage

Run this skill whenever you make changes that should be documented.

## CHANGELOG Format

All changes go under the "In development" section at the top:

```markdown
# Change Log

Version X.Y.Z *(In development)*
--------------------------------

- Category: Description of change [\#PR](link) ([github-handle](link))
```

## Categories

Use these category prefixes:

### API Changes
```markdown
- API: New CropImageView method for custom overlay. [\#123](url) ([username](url))
- API: Deprecate CropImageActivity. [\#124](url) ([username](url))
```

### Bug Fixes
```markdown
- Fix: Crop window jumps during multi-touch. [\#656](url) ([username](url))
- Fix issue #610: keep layout params. [\#665](url) ([username](url))
```

### Security
```markdown
- Security: Added URI validation to prevent file system manipulation (fixes #613) [\#680](url) ([username](url))
```

### Features
```markdown
- Added new language - Malayalam [\#638](url) ([username](url))
- Add support for south indian languages [\#658](url) ([username](url))
```

### Technical/Internal
```markdown
- Technical: Update Kotlin to 2.1.0. ([username](url))
- Technical: Use Central Portal for publishing. [\#673](url) ([username](url))
```

### Resources
```markdown
- Resource: Missing Translations added for korean / japanese [\#552](url) ([username](url))
```

### Behavior Changes
```markdown
- Behavior Change: Always try to return resized bitmap in CropResult. [\#509](url) ([username](url))
```

## Template

```markdown
- [Category]: [Description] [\#[issue-or-pr-number]](github-url) ([your-handle](profile-url))
```

## Examples

### Adding a Feature
```markdown
- Added Danish language support [\#666](https://github.com/CanHub/Android-Image-Cropper/pull/666) ([andreastorp](https://github.com/andreastorp))
```

### Fixing a Bug
```markdown
- Fix: Crop overlay jumps during multiple pointers active, but initial pointer is released [\#656](https://github.com/CanHub/Android-Image-Cropper/pull/656) ([dinaraparanid](https://github.com/dinaraparanid))
```

### API Change
```markdown
- API: CropImageOptions#canChangeCropWindow option. [\#636](https://github.com/CanHub/Android-Image-Cropper/pull/636) ([vanniktech](https://github.com/vanniktech))
```

### Dependency Update
```markdown
- Technical: Update Kotlin to 2.1.0. ([yourusername](https://github.com/yourusername))
```

## Rules

1. **Always update**: Every change must be in CHANGELOG.md
2. **Top of file**: Add under "In development" section
3. **Proper category**: Use correct category prefix
4. **Link to PR/issue**: Include GitHub links
5. **Credit author**: Include GitHub username
6. **Clear description**: What changed and why (if not obvious)
7. **Reference issues**: If fixing an issue, mention it

## What NOT to Include

- Internal refactoring that doesn't affect users
- Build script changes (unless significant)
- Documentation-only changes (unless major)
- Test-only changes

## Checklist

- [ ] Entry added under "In development"
- [ ] Correct category prefix used
- [ ] Clear description of change
- [ ] PR/issue number included (if applicable)
- [ ] GitHub username included
- [ ] Links are correct
- [ ] Follows existing formatting

---
name: add-translation
description: Add new language translations to the library
argument-hint: "<language-code> [language-name]"
level: 1
triggers:
  - "add translation"
  - "translate"
  - "new language"
---

# Add Translation Skill

Add support for new languages or update existing translations.

## Usage

Run this skill when adding translations for a new language or updating existing ones.

## Translation Files

Translations are located in:
```
cropper/src/main/res/
├── values/strings.xml           # English (default)
├── values-es/strings.xml        # Spanish
├── values-fr/strings.xml        # French
├── values-hi/strings.xml        # Hindi
├── values-ml/strings.xml        # Malayalam
├── values-da/strings.xml        # Danish
└── values-{lang}/strings.xml    # Other languages
```

## String Keys

Based on `values/strings.xml`, the library has these translatable strings:

```xml
<resources>
    <string name="crop_image_menu_crop">Crop</string>
    <string name="pick_image_intent_chooser_title">Select source</string>
    <string name="crop_image_activity_no_permissions">Cancelling, required permissions are not granted</string>
    <string name="crop_image_menu_rotate_left">Rotate left</string>
    <string name="crop_image_menu_rotate_right">Rotate right</string>
    <string name="crop_image_menu_flip">Flip</string>
    <string name="crop_image_menu_flip_horizontally">Flip horizontally</string>
    <string name="crop_image_menu_flip_vertically">Flip vertically</string>
</resources>
```

## Steps to Add New Language

### 1. Create Language Directory
```bash
mkdir -p cropper/src/main/res/values-{language-code}/
```

**Common Language Codes:**
- Spanish: `es`
- French: `fr`
- German: `de`
- Italian: `it`
- Portuguese: `pt`
- Japanese: `ja`
- Korean: `ko`
- Chinese (Simplified): `zh-rCN`
- Chinese (Traditional): `zh-rTW`
- Hindi: `hi`
- Arabic: `ar`
- Russian: `ru`

### 2. Create strings.xml

Copy the template:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="crop_image_menu_crop">[Translation for "Crop"]</string>
    <string name="pick_image_intent_chooser_title">[Translation for "Select source"]</string>
    <string name="crop_image_activity_no_permissions">[Translation for "Cancelling, required permissions are not granted"]</string>
    <string name="crop_image_menu_rotate_left">[Translation for "Rotate left"]</string>
    <string name="crop_image_menu_rotate_right">[Translation for "Rotate right"]</string>
    <string name="crop_image_menu_flip">[Translation for "Flip"]</string>
    <string name="crop_image_menu_flip_horizontally">[Translation for "Flip horizontally"]</string>
    <string name="crop_image_menu_flip_vertically">[Translation for "Flip vertically"]</string>
</resources>
```

### 3. Translate Strings

Replace `[Translation for ...]` with actual translations in target language.

**Translation Guidelines:**
- Keep technical terms consistent
- Respect cultural context
- Keep length similar to English (for UI fit)
- Use formal/informal tone appropriate for language
- Test with sample app in that language

### 4. Verify Translation

1. Change device language to new language
2. Run sample app
3. Verify all strings display correctly
4. Check for truncation or overflow
5. Ensure proper RTL support (for Arabic, Hebrew, etc.)

### 5. Update CHANGELOG.md

```markdown
- Added [Language Name] language support [\#PR](url) ([your-handle](url))
```

## Example: Adding Portuguese (Brazil)

### Create Directory
```bash
mkdir -p cropper/src/main/res/values-pt-rBR/
```

### Create strings.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="crop_image_menu_crop">Cortar</string>
    <string name="pick_image_intent_chooser_title">Selecionar origem</string>
    <string name="crop_image_activity_no_permissions">Cancelando, as permissões necessárias não foram concedidas</string>
    <string name="crop_image_menu_rotate_left">Girar à esquerda</string>
    <string name="crop_image_menu_rotate_right">Girar à direita</string>
    <string name="crop_image_menu_flip">Espelhar</string>
    <string name="crop_image_menu_flip_horizontally">Espelhar horizontalmente</string>
    <string name="crop_image_menu_flip_vertically">Espelhar verticalmente</string>
</resources>
```

### Update CHANGELOG.md
```markdown
- Added Brazilian Portuguese language support [\#XXX](url) ([yourusername](url))
```

## Updating Existing Translations

### Find Missing Strings
1. Check `values/strings.xml` for all strings
2. Compare with existing translation
3. Add missing strings

### Update Translation
1. Open `values-{lang}/strings.xml`
2. Update or add strings
3. Test in sample app
4. Update CHANGELOG.md

## RTL Language Support

For RTL languages (Arabic, Hebrew, etc.):

1. Create `values-{lang}/strings.xml` as normal
2. Android handles RTL automatically
3. Test UI layout in RTL mode
4. Ensure crop window works correctly

## Quality Checklist

- [ ] All string keys from `values/strings.xml` translated
- [ ] No missing translations
- [ ] Proper XML encoding (UTF-8)
- [ ] Proper XML format
- [ ] Tested in sample app
- [ ] No UI overflow/truncation
- [ ] CHANGELOG.md updated
- [ ] Language code is correct

## Common Issues

### Issue: Strings not appearing
**Solution**: Check language code is correct, rebuild app

### Issue: Special characters broken
**Solution**: Ensure file is UTF-8 encoded

### Issue: Text truncated
**Solution**: Keep translations similar length or adjust UI

## Testing

```bash
# Build and install sample app
./gradlew :sample:installDebug

# Change device language to test language
# Open sample app
# Verify all strings appear correctly
```

---

*Quality translations improve user experience worldwide.*

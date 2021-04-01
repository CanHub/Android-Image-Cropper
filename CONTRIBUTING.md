# How to become a contributor and submit your own code

## Useful links
- [Good First Issues](https://github.com/CanHub/Android-Image-Cropper/contribute)
- [Roadmap](https://github.com/CanHub/Android-Image-Cropper/projects/1)
- We encourage to [Join the team](https://github.com/CanHub/Android-Image-Cropper/discussions/42) and be an active member

## Doing some code
1. If there is no issue yet: Submit an issue describing your proposed change.
1. The repo owner will respond to your issue promptly. (This is important to keep the discussion split of the code review, but you can already start you code if you think will be accept)
1. Fork the repo, develop and test your code changes.
1. Don't forget to the Project [CodeStyle](#code-style). CI will break if you don't.
1. Please always check the provided templates for Issues and Pull Requests, deleting what is not needed.
1. Update the `CHANGELOG.md`, like [illustrated below](#updating-changelog.md).
1. If needed add tests and README/WIKI sessions.
1. Submit your Pull Request

### Updating CHANGELOG.md
- Check the file `CHANGELOG.md`
- Add your changes under `## [unreleased x.x.x] -` following the format (`Added, Changed, etc`)
- Link the issue to your changes using `Small description [#XX](link_to_the_issue)` where XX is the number of the issue or
- Check previous versions for more guidance

### Code Style
Path: `.idea/codeStyles/` Please never change anything inside this folders

- The file `codeStyleCondig` should be responsible to change the kotlin lint source.
- Please check if Android Studio right imported the `Project` code style.
- After changes you can run `./gradlew ktlintFormat` or the shortcuts in your touched files.

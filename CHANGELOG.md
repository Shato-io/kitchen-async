# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [0.2.2]
### Changed
- Add sync versions of most of the functions in order to avoid promises if not necessary.

## [0.1.1] - 2017-02-28
### Changed
- Documentation on how to make the widgets.

### Removed
- `make-widget-sync` - we're all async, all the time.

### Fixed
- Fixed widget maker to keep working when daylight savings switches over.

## 0.1.0 - 2017-02-28
### Added
- Files from the new template.
- Widget maker public API - `make-widget-sync`.

[Unreleased]: https://github.com/your-name/kitchen-async/compare/0.1.1...HEAD
[0.1.1]: https://github.com/your-name/kitchen-async/compare/0.1.0...0.1.1

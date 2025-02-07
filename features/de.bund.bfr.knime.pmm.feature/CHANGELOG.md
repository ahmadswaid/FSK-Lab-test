# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.2.2] - 2019-5-10

### Fixed
- Ignore model variables with missing values. Reader was producing NPE with missing values.

## [1.2.1] - 2019-3-14 

### Fixed
- Fix Variable Data Reader in PMMLab by using the up to date version of FileUpload Node.

## [1.2.0] - 2019-2-4

### Changed
- Simplify API of common classes.

## [1.1.0] - 2018-12-12

### Added
- Generated PMFX files with the writer node are added a README file.

### Changed
- Proper validation in PMF Writer. Users are prompted to fix faulty settings before closing the dialog.
- Vendor property of PMM-Lab feature to the original BfR name: "German Federal Institute for Risk Assessment (BfR)"

### Fixed
- Replace NPE in writer when an unit is not found in DB with meaningful exception

[Unreleased]: https://github.com/SiLeBAT/FSK-Lab/compare/v1.0.0...HEAD

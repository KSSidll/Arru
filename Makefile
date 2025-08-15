PHONY: release
release:
	gradlew clean assembleRelease --no-build-cache --no-configuration-cache --no-daemon
